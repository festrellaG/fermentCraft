package com.test.canonical.fermentcraft.client;

import java.math.BigDecimal;

public record ShippingRateResponseDTO(String zone, BigDecimal fee, String estimatedDays) {
}
