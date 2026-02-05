package com.example.microticket.domain;

import javax.persistence.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Order 实体：表示用户创建的订单（不含明细）
 */
@Entity
@Table(name = "orders")
public class Order implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="order_no", unique = true, nullable = false)
    private String orderNo;

    private Long userId;
    private Long screeningId;
    private Double totalAmount;

    private String status; // CREATED, PAID, CANCELLED...

    private LocalDateTime createdAt;
    private LocalDateTime paidAt;

    public Order() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getScreeningId() { return screeningId; }
    public void setScreeningId(Long screeningId) { this.screeningId = screeningId; }

    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getPaidAt() { return paidAt; }
    public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }
}