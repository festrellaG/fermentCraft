package com.test.canonical.fermentcraft.dto;

import com.test.canonical.fermentcraft.entity.BoxStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record SubscriptionBoxResponseDTO(
        Long id,
        String orderNumber,
        String customerEmail,
        String shippingAddress,
        BoxStatus status,
        BigDecimal shippingFee,
        BigDecimal subtotal,
        BigDecimal totalAmount,
        LocalDateTime createdAt,
        List<BoxItemResponseDTO> items
) {
}
