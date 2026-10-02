package com.test.canonical.fermentcraft.health;

import com.test.canonical.fermentcraft.repository.ProductBatchRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

@Readiness
@ApplicationScoped
public class DatabaseReadinessCheck implements HealthCheck {

    private final ProductBatchRepository repository;

    @Inject
    public DatabaseReadinessCheck(ProductBatchRepository repository) {
        this.repository = repository;
    }

    @Override
    public HealthCheckResponse call() {
        try {
            long productBatchCount = repository.count();
            return HealthCheckResponse.named("FermentCraft database")
                    .up()
                    .withData("productBatchCount", productBatchCount)
                    .build();
        } catch (RuntimeException exception) {
            return HealthCheckResponse.named("FermentCraft database")
                    .down()
                    .withData("error", exception.getClass().getSimpleName())
                    .withData("message", safeMessage(exception))
                    .build();
        }
    }

    private String safeMessage(RuntimeException exception) {
        return exception.getMessage() == null
                ? "No fue posible consultar PostgreSQL"
                : exception.getMessage();
    }
}
