package com.test.canonical.fermentcraft.repository;

import com.test.canonical.fermentcraft.entity.ProductBatch;
import io.quarkus.panache.common.Page;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.LockModeType;

import java.util.Optional;

@ApplicationScoped
public class ProductBatchRepository implements PanacheRepository<ProductBatch> {

    /**
     * Filtra lotes activos, opcionalmente por categoría, con soporte de paginación.
     */
    public PanacheQuery<ProductBatch> findActiveByCategory(String category, Page page) {
        PanacheQuery<ProductBatch> query;
        if (category != null && !category.isBlank()) {
            query = find("active = true and category = ?1", category);
        } else {
            query = find("active = true");
        }
        return query.page(page);
    }

    /**
     * Busca un lote por id, validando que se encuentre activo.
     */
    public Optional<ProductBatch> findActiveById(Long id) {
        return find("id = ?1 and active = true", id).firstResultOptional();
    }

    /**
     * Bloquea el lote mientras se valida y actualiza su inventario.
     */
    public Optional<ProductBatch> findActiveByIdForUpdate(Long id) {
        return find("id = ?1 and active = true", id)
                .withLock(LockModeType.PESSIMISTIC_WRITE)
                .firstResultOptional();
    }
}
