package com.example.microticket.dto;

import lombok.Data;

@Data
public class CreateOrderResponse {
    private String orderNo;
    private Double amount;
    private String message;
}