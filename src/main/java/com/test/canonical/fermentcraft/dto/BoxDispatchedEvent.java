package com.test.canonical.fermentcraft.dto;

import com.test.canonical.fermentcraft.entity.BoxStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BoxDispatchedEvent(
        String orderNumber,
        String customerEmail,
        BigDecimal totalAmount,
        BoxStatus status,
        LocalDateTime timestamp
) {}
