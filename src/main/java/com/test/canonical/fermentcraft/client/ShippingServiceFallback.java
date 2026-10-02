package com.test.canonical.fermentcraft.client;

import org.eclipse.microprofile.faulttolerance.ExecutionContext;
import org.eclipse.microprofile.faulttolerance.FallbackHandler;

import java.math.BigDecimal;

public class ShippingServiceFallback implements FallbackHandler<ShippingRateResponseDTO> {

    @Override
    public ShippingRateResponseDTO handle(ExecutionContext context) {
        return new ShippingRateResponseDTO(
                "DEFAULT",
                new BigDecimal("45.00"),
                "3-5 business days"
        );
    }
}
