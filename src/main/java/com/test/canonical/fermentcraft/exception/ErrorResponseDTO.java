package com.test.canonical.fermentcraft.exception;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponseDTO(
        String code,
        String message,
        LocalDateTime timestamp,
        List<String> details
) {
}
