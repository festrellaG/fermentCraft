package com.test.canonical.fermentcraft.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ProductBatchRequestDTO(
        @NotBlank(message = "El nombre del producto es obligatorio")
        String productName,
        @NotBlank(message = "La categoría es obligatoria")
        String category,
        @NotNull(message = "La cantidad disponible es obligatoria")
        @Min(value = 0, message = "La cantidad no puede ser negativa")
        Integer availableQuantity,
        @NotNull(message = "El precio unitario es obligatorio")
        @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
        BigDecimal unitPrice,
        @FutureOrPresent(message = "La fecha de expiración no puede ser pasada")
        LocalDate expirationDate
) {
}
