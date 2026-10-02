package com.test.canonical.fermentcraft.dto;

import java.math.BigDecimal;

public record BoxItemResponseDTO(
        Long id,
        Long productBatchId,
        String productName,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal subtotal
) {
}
