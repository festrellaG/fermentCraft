package com.test.canonical.fermentcraft.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record BoxItemRequestDTO(
        @NotNull(message = "El ID del lote es obligatorio")
        Long productBatchId,
        @NotNull(message = "La cantidad es obligatoria")
        @Min(value = 1, message = "Debe solicitar al menos una unidad")
        Integer quantity
) {
}
