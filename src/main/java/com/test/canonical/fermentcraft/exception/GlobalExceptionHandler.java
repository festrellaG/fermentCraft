package com.test.canonical.fermentcraft.exception;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class GlobalExceptionHandler {

    @ServerExceptionMapper
    public Response handleResourceNotFound(ResourceNotFoundException exception) {
        return response(
                Response.Status.NOT_FOUND.getStatusCode(),
                "RESOURCE_NOT_FOUND",
                exception.getMessage(),
                List.of()
        );
    }

    @ServerExceptionMapper
    public Response handleInsufficientStock(InsufficientStockException exception) {
        return response(
                422,
                "INSUFFICIENT_STOCK",
                exception.getMessage(),
                List.of()
        );
    }

    @ServerExceptionMapper
    public Response handleConstraintViolation(ConstraintViolationException exception) {
        List<String> details = exception.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .sorted()
                .toList();
        return response(
                Response.Status.BAD_REQUEST.getStatusCode(),
                "VALIDATION_ERROR",
                "Uno o más campos no son válidos",
                details
        );
    }

    @ServerExceptionMapper
    public Response handleUnexpectedException(Exception exception) {
        return response(
                Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                "INTERNAL_SERVER_ERROR",
                "Ocurrió un error interno en el servidor",
                List.of()
        );
    }

    private Response response(
            int status,
            String code,
            String message,
            List<String> details
    ) {
        ErrorResponseDTO body = new ErrorResponseDTO(
                code,
                message,
                LocalDateTime.now(),
                details
        );
        return Response.status(status)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(body)
                .build();
    }
}
