package com.test.canonical.fermentcraft.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CreateBoxRequestDTO(
        @NotBlank(message = "El email del cliente es obligatorio")
        @Email(message = "Formato de email inválido")
        String customerEmail,
        @NotBlank(message = "La dirección de envío es obligatoria")
        String shippingAddress,
        @NotEmpty(message = "La caja debe contener al menos un producto")
        @Valid List<BoxItemRequestDTO> items
) {
}
