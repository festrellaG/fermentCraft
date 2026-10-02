package com.test.canonical.fermentcraft.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "subscription_boxes")
public class SubscriptionBox {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_number", unique = true, nullable = false, length = 64)
    private String orderNumber;

    @Column(name = "customer_email", nullable = false, length = 150)
    private String customerEmail;

    @Column(name = "shipping_address", nullable = false, length = 255)
    private String shippingAddress;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BoxStatus status;

    @Column(name = "shipping_fee", precision = 10, scale = 2)
    private BigDecimal shippingFee;

    @Column(name = "subtotal", precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "total_amount", precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "box", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<BoxItem> items = new ArrayList<>();

    public SubscriptionBox() {
    }

    public SubscriptionBox(String orderNumber, String customerEmail, String shippingAddress,
                            BoxStatus status, BigDecimal shippingFee, BigDecimal subtotal,
                            BigDecimal totalAmount, LocalDateTime createdAt) {
        this.orderNumber = orderNumber;
        this.customerEmail = customerEmail;
        this.shippingAddress = shippingAddress;
        this.status = status;
        this.shippingFee = shippingFee;
        this.subtotal = subtotal;
        this.totalAmount = totalAmount;
        this.createdAt = createdAt;
    }

    /**
     * Sincroniza la relación bidireccional agregando el item a la caja
     * y estableciendo la referencia inversa en el item.
     */
    public void addItem(BoxItem item) {
        items.add(item);
        item.setBox(this);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public BoxStatus getStatus() {
        return status;
    }

    public void setStatus(BoxStatus status) {
        this.status = status;
    }

    public BigDecimal getShippingFee() {
        return shippingFee;
    }

    public void setShippingFee(BigDecimal shippingFee) {
        this.shippingFee = shippingFee;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<BoxItem> getItems() {
        return items;
    }

    public void setItems(List<BoxItem> items) {
        this.items = items;
    }
}
