package com.test.canonical.fermentcraft.mapper;

import com.test.canonical.fermentcraft.dto.ProductBatchRequestDTO;
import com.test.canonical.fermentcraft.dto.ProductBatchResponseDTO;
import com.test.canonical.fermentcraft.entity.ProductBatch;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Objects;

@ApplicationScoped
public class ProductBatchMapper {

    public ProductBatchResponseDTO toDTO(ProductBatch entity) {
        Objects.requireNonNull(entity, "entity no puede ser null");
        return new ProductBatchResponseDTO(
                entity.getId(),
                entity.getProductName(),
                entity.getCategory(),
                entity.getAvailableQuantity(),
                entity.getUnitPrice(),
                entity.getExpirationDate(),
                entity.getActive()
        );
    }

    public ProductBatch toEntity(ProductBatchRequestDTO dto) {
        Objects.requireNonNull(dto, "dto no puede ser null");
        return new ProductBatch(
                dto.productName(),
                dto.category(),
                dto.availableQuantity(),
                dto.unitPrice(),
                dto.expirationDate(),
                true
        );
    }

    public void updateEntityFromDTO(ProductBatch entity, ProductBatchRequestDTO dto) {
        Objects.requireNonNull(entity, "entity no puede ser null");
        Objects.requireNonNull(dto, "dto no puede ser null");
        entity.setProductName(dto.productName());
        entity.setCategory(dto.category());
        entity.setAvailableQuantity(dto.availableQuantity());
        entity.setUnitPrice(dto.unitPrice());
        entity.setExpirationDate(dto.expirationDate());
    }
}
