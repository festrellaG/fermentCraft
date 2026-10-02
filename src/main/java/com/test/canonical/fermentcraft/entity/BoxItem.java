package com.test.canonical.fermentcraft.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "box_items")
public class BoxItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "box_id", nullable = false)
    private SubscriptionBox box;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_batch_id", nullable = false)
    private ProductBatch productBatch;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    public BoxItem() {
    }

    public BoxItem(SubscriptionBox box, ProductBatch productBatch, Integer quantity, BigDecimal unitPrice) {
        this.box = box;
        this.productBatch = productBatch;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SubscriptionBox getBox() {
        return box;
    }

    public void setBox(SubscriptionBox box) {
        this.box = box;
    }

    public ProductBatch getProductBatch() {
        return productBatch;
    }

    public void setProductBatch(ProductBatch productBatch) {
        this.productBatch = productBatch;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
}
