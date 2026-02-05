package com.example.microticket.repository;

import com.example.microticket.domain.Screening;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ScreeningRepository extends JpaRepository<Screening, Long> {

    @Modifying
    @Query("update Screening s set s.availableSeats = s.availableSeats - ?2 where s.id = ?1 and s.availableSeats >= ?2")
    int deductAvailableSeats(Long screeningId, int count);
}