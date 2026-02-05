package com.example.microticket.domain;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
public class Payment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long orderId;
    private String tradeNo;
    private Double amount;
    private String status;
    private String platform;
    private LocalDateTime payTime;
}