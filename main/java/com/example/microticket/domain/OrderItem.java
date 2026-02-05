package com.example.microticket.domain;

import javax.persistence.*;

import javax.persistence.*;
import java.io.Serializable;

/**
 * OrderItem：订单明细（单张票/座位）
 */
@Entity
@Table(name = "order_items")
public class OrderItem implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long orderId;
    private Integer seatRow;
    private Integer seatCol;
    private String seatNo;

    private Double price;

    public OrderItem() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public Integer getSeatRow() { return seatRow; }
    public void setSeatRow(Integer seatRow) { this.seatRow = seatRow; }

    public Integer getSeatCol() { return seatCol; }
    public void setSeatCol(Integer seatCol) { this.seatCol = seatCol; }

    public String getSeatNo() { return seatNo; }
    public void setSeatNo(String seatNo) { this.seatNo = seatNo; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
}