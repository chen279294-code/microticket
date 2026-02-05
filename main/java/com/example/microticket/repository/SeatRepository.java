package com.example.microticket.repository;

import com.example.microticket.domain.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByHallId(Long hallId);
}