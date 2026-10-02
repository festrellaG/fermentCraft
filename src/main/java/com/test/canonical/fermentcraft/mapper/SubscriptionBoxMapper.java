package com.test.canonical.fermentcraft.mapper;

import com.test.canonical.fermentcraft.dto.BoxItemResponseDTO;
import com.test.canonical.fermentcraft.dto.SubscriptionBoxResponseDTO;
import com.test.canonical.fermentcraft.entity.BoxItem;
import com.test.canonical.fermentcraft.entity.ProductBatch;
import com.test.canonical.fermentcraft.entity.SubscriptionBox;
import jakarta.enterprise.context.ApplicationScoped;

import java.math.BigDecimal;
import java.util.Objects;

@ApplicationScoped
public class SubscriptionBoxMapper {

    public SubscriptionBoxResponseDTO toDTO(SubscriptionBox entity) {
        Objects.requireNonNull(entity, "entity no puede ser null");
        return new SubscriptionBoxResponseDTO(
                entity.getId(),
                entity.getOrderNumber(),
                entity.getCustomerEmail(),
                entity.getShippingAddress(),
                entity.getStatus(),
                entity.getShippingFee(),
                entity.getSubtotal(),
                entity.getTotalAmount(),
                entity.getCreatedAt(),
                entity.getItems().stream()
                        .map(this::toItemDTO)
                        .toList()
        );
    }

    private BoxItemResponseDTO toItemDTO(BoxItem item) {
        ProductBatch productBatch = item.getProductBatch();
        return new BoxItemResponseDTO(
                item.getId(),
                productBatch.getId(),
                productBatch.getProductName(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
        );
    }
}
