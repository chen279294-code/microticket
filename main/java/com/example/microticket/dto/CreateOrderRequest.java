package com.example.microticket.dto;

import lombok.Data;

import java.util.List;

@Data
public class CreateOrderRequest {
    private Long screeningId;
    // seat represented as "r_c" e.g. "5_8"
    private List<String> seats;
    private Double totalAmount;
}