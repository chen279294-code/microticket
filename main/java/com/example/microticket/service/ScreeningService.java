package com.example.microticket.service;

import com.example.microticket.domain.Screening;

import java.util.List;

public interface ScreeningService {
    List<Screening> findAll();
    Screening findById(Long id);
    Screening create(Screening s);
    Screening update(Long id, Screening s);
    void delete(Long id);
    Integer getAvailableSeats(Long id);
}