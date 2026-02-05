package com.example.microticket.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class AdminStatsDto {
    private long movieCount;
    private long cinemaCount;
    private BigDecimal todayBox;
    private BigDecimal totalBox;
}
