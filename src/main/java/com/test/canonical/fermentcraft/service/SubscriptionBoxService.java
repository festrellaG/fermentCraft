package com.test.canonical.fermentcraft.service;

import com.test.canonical.fermentcraft.client.ShippingRateResponseDTO;
import com.test.canonical.fermentcraft.client.ShippingServiceClient;
import com.test.canonical.fermentcraft.dto.BoxDispatchedEvent;
import com.test.canonical.fermentcraft.dto.BoxItemRequestDTO;
import com.test.canonical.fermentcraft.dto.CreateBoxRequestDTO;
import com.test.canonical.fermentcraft.dto.PageResultDTO;
import com.test.canonical.fermentcraft.dto.SubscriptionBoxResponseDTO;
import com.test.canonical.fermentcraft.entity.BoxItem;
import com.test.canonical.fermentcraft.entity.BoxStatus;
import com.test.canonical.fermentcraft.entity.ProductBatch;
import com.test.canonical.fermentcraft.entity.SubscriptionBox;
import com.test.canonical.fermentcraft.exception.InsufficientStockException;
import com.test.canonical.fermentcraft.exception.ResourceNotFoundException;
import com.test.canonical.fermentcraft.mapper.SubscriptionBoxMapper;
import com.test.canonical.fermentcraft.repository.ProductBatchRepository;
import com.test.canonical.fermentcraft.repository.SubscriptionBoxRepository;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.UUID;

@ApplicationScoped
public class SubscriptionBoxService {

    @Inject
    SubscriptionBoxRepository subscriptionBoxRepository;

    @Inject
    ProductBatchRepository productBatchRepository;

    @Inject
    SubscriptionBoxMapper subscriptionBoxMapper;

    @Inject
    @RestClient
    ShippingServiceClient shippingClient;

    @Inject
    @Channel("box-dispatched")
    Emitter<BoxDispatchedEvent> eventEmitter;

    public SubscriptionBoxResponseDTO getById(Long id) {
        Objects.requireNonNull(id, "id no puede ser null");
        SubscriptionBox box = subscriptionBoxRepository.find("id", id)
                .firstResultOptional()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró una caja con ID: " + id
                ));
        return subscriptionBoxMapper.toDTO(box);
    }

    public SubscriptionBoxResponseDTO getByOrderNumber(String orderNumber) {
        Objects.requireNonNull(orderNumber, "orderNumber no puede ser null");
        SubscriptionBox box = subscriptionBoxRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró una caja con número de orden: " + orderNumber
                ));
        return subscriptionBoxMapper.toDTO(box);
    }

    public PageResultDTO<SubscriptionBoxResponseDTO> listBoxes(int page, int size, BoxStatus status) {
        if (page < 0) {
            throw new IllegalArgumentException("page no puede ser negativo");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("size debe ser mayor que cero");
        }

        var query = subscriptionBoxRepository.findByStatus(status, Page.of(page, size));
        var content = query.list().stream()
                .map(subscriptionBoxMapper::toDTO)
                .toList();
        return PageResultDTO.of(content, page, size, query.count());
    }

    @Transactional
    public SubscriptionBoxResponseDTO updateStatus(Long id, BoxStatus newStatus) {
        Objects.requireNonNull(newStatus, "newStatus no puede ser null");
        SubscriptionBox box = subscriptionBoxRepository.find("id", id)
                .firstResultOptional()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró una caja con ID: " + id
                ));
        box.setStatus(newStatus);
        return subscriptionBoxMapper.toDTO(box);
    }

    @Transactional
    public SubscriptionBoxResponseDTO createBox(CreateBoxRequestDTO request) {
        Objects.requireNonNull(request, "request no puede ser null");
        Objects.requireNonNull(request.items(), "items no puede ser null");
        if (request.items().isEmpty()) {
            throw new IllegalArgumentException("La caja debe contener al menos un producto");
        }

        Map<Long, Integer> requestedQuantities = new TreeMap<>();
        for (BoxItemRequestDTO item : request.items()) {
            Objects.requireNonNull(item, "El item de la caja no puede ser null");
            Objects.requireNonNull(item.productBatchId(), "El ID del lote es obligatorio");
            Objects.requireNonNull(item.quantity(), "La cantidad es obligatoria");
            if (item.quantity() <= 0) {
                throw new IllegalArgumentException("La cantidad debe ser mayor que cero");
            }
            requestedQuantities.merge(
                    item.productBatchId(),
                    item.quantity(),
                    Math::addExact
            );
        }

        ShippingRateResponseDTO shippingRate = Objects.requireNonNull(
                shippingClient.calculateShippingRate(request.shippingAddress()),
                "El servicio de envío devolvió una respuesta null"
        );
        BigDecimal shippingFee = Objects.requireNonNull(
                shippingRate.fee(),
                "La tarifa de envío no puede ser null"
        );

        Map<Long, ProductBatch> lockedBatches = new TreeMap<>();
        for (Map.Entry<Long, Integer> requestEntry : requestedQuantities.entrySet()) {
            Long batchId = requestEntry.getKey();
            ProductBatch batch = productBatchRepository.findActiveByIdForUpdate(batchId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "No se encontró un lote activo con ID: " + batchId
                    ));
            if (batch.getAvailableQuantity() < requestEntry.getValue()) {
                throw new InsufficientStockException(
                        "Stock insuficiente para el lote con ID: " + batchId
                );
            }
            lockedBatches.put(batchId, batch);
        }

        LocalDateTime createdAt = LocalDateTime.now();
        SubscriptionBox box = new SubscriptionBox(
                "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                request.customerEmail(),
                request.shippingAddress(),
                BoxStatus.CONFIRMED,
                shippingFee,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                createdAt
        );

        BigDecimal subtotal = BigDecimal.ZERO;
        for (BoxItemRequestDTO itemRequest : request.items()) {
            ProductBatch batch = lockedBatches.get(itemRequest.productBatchId());
            BigDecimal unitPrice = batch.getUnitPrice();
            BigDecimal itemSubtotal = unitPrice.multiply(BigDecimal.valueOf(itemRequest.quantity()));

            box.addItem(new BoxItem(box, batch, itemRequest.quantity(), unitPrice));
            subtotal = subtotal.add(itemSubtotal);
        }

        for (Map.Entry<Long, Integer> requestEntry : requestedQuantities.entrySet()) {
            ProductBatch batch = lockedBatches.get(requestEntry.getKey());
            batch.setAvailableQuantity(batch.getAvailableQuantity() - requestEntry.getValue());
        }

        box.setSubtotal(subtotal);
        box.setTotalAmount(subtotal.add(shippingFee));
        subscriptionBoxRepository.persist(box);

        eventEmitter.send(new BoxDispatchedEvent(
                box.getOrderNumber(),
                box.getCustomerEmail(),
                box.getTotalAmount(),
                box.getStatus(),
                createdAt
        ));

        return subscriptionBoxMapper.toDTO(box);
    }
}
