package com.test.canonical.fermentcraft.dto;

import java.util.List;
import java.util.Objects;

public record PageResultDTO<T>(
        List<T> content,
        int pageIndex,
        int pageSize,
        long totalElements,
        int totalPages,
        boolean hasNext,
        boolean hasPrevious
) {
    public static <T> PageResultDTO<T> of(
            List<T> content,
            int pageIndex,
            int pageSize,
            long totalElements
    ) {
        Objects.requireNonNull(content, "content no puede ser null");
        if (pageIndex < 0) {
            throw new IllegalArgumentException("pageIndex no puede ser negativo");
        }
        if (pageSize <= 0) {
            throw new IllegalArgumentException("pageSize debe ser mayor que cero");
        }
        if (totalElements < 0) {
            throw new IllegalArgumentException("totalElements no puede ser negativo");
        }

        int totalPages = Math.toIntExact(
                totalElements / pageSize + (totalElements % pageSize == 0 ? 0 : 1)
        );
        return new PageResultDTO<>(
                content,
                pageIndex,
                pageSize,
                totalElements,
                totalPages,
                pageIndex < totalPages - 1,
                pageIndex > 0
        );
    }
}
