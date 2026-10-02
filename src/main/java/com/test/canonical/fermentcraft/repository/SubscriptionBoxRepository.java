package com.test.canonical.fermentcraft.repository;

import com.test.canonical.fermentcraft.entity.BoxStatus;
import com.test.canonical.fermentcraft.entity.SubscriptionBox;
import io.quarkus.panache.common.Page;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class SubscriptionBoxRepository implements PanacheRepository<SubscriptionBox> {

    /**
     * Busca una caja de suscripción por su número de orden único.
     */
    public Optional<SubscriptionBox> findByOrderNumber(String orderNumber) {
        return find("orderNumber", orderNumber).firstResultOptional();
    }

    /**
     * Filtra cajas por estado con soporte de paginación.
     */
    public PanacheQuery<SubscriptionBox> findByStatus(BoxStatus status, Page page) {
        if (status == null) {
            return findAll().page(page);
        }
        return find("status", status).page(page);
    }
}
