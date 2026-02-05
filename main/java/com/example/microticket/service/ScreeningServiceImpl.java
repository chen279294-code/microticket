package com.example.microticket.service;

import com.example.microticket.domain.Screening;
import com.example.microticket.repository.ScreeningRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * ScreeningService 基于 JPA 的实现
 */
@Service
public class ScreeningServiceImpl implements ScreeningService {

    private final ScreeningRepository screeningRepository;

    public ScreeningServiceImpl(ScreeningRepository screeningRepository) {
        this.screeningRepository = screeningRepository;
    }

    @Override
    public List<Screening> findAll() {
        return screeningRepository.findAll();
    }

    @Override
    public Screening findById(Long id) {
        Optional<Screening> opt = screeningRepository.findById(id);
        return opt.orElse(null);
    }

    @Override
    public Screening create(Screening s) {
        return screeningRepository.save(s);
    }

    @Override
    @Transactional
    public Screening update(Long id, Screening s) {
        return screeningRepository.findById(id).map(existing -> {
            existing.setTotalSeats(s.getTotalSeats());
            existing.setAvailableSeats(s.getAvailableSeats());
            return screeningRepository.save(existing);
        }).orElse(null);
    }

    @Override
    public void delete(Long id) {
        screeningRepository.deleteById(id);
    }

    @Override
    public Integer getAvailableSeats(Long id) {
        return screeningRepository.findById(id).map(Screening::getAvailableSeats).orElse(null);
    }
}