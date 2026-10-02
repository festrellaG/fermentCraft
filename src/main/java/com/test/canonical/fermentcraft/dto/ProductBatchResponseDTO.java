package com.test.canonical.fermentcraft.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ProductBatchResponseDTO(
        Long id,
        String productName,
        String category,
        Integer availableQuantity,
        BigDecimal unitPrice,
        LocalDate expirationDate,
        Boolean active
) {
}
