package com.example.microticket.controller;

import com.example.microticket.domain.Screening;
import com.example.microticket.service.ScreeningService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ScreeningController - 场次管理（查询 / 创建 / 更新 / 删除 / 可用座位）。
 *
 * 假定存在 ScreeningService 提供相应方法。
 */
@RestController
@RequestMapping("/api/screenings")
public class ScreeningController {

    private final ScreeningService screeningService;

    public ScreeningController(ScreeningService screeningService) {
        this.screeningService = screeningService;
    }

    @GetMapping
    public ResponseEntity<List<Screening>> listAll() {
        List<Screening> list = screeningService.findAll();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Screening> getById(@PathVariable Long id) {
        Screening s = screeningService.findById(id);
        if (s == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(s);
    }

    @PostMapping
    public ResponseEntity<Screening> create(@RequestBody ScreeningCreateRequest req) {
        Screening s = new Screening();
        s.setTotalSeats(req.getTotalSeats());
        s.setAvailableSeats(req.getAvailableSeats() != null ? req.getAvailableSeats() : req.getTotalSeats());
        Screening created = screeningService.create(s);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Screening> update(@PathVariable Long id, @RequestBody ScreeningCreateRequest req) {
        Screening s = new Screening();
        s.setTotalSeats(req.getTotalSeats());
        s.setAvailableSeats(req.getAvailableSeats());
        Screening updated = screeningService.update(id, s);
        if (updated == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        screeningService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/available-seats")
    public ResponseEntity<Integer> availableSeats(@PathVariable Long id) {
        Integer avail = screeningService.getAvailableSeats(id);
        if (avail == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(avail);
    }

    /* ---------------- DTO ---------------- */

    public static class ScreeningCreateRequest {
        private Integer totalSeats;
        private Integer availableSeats;

        public ScreeningCreateRequest() {}

        public Integer getTotalSeats() { return totalSeats; }
        public void setTotalSeats(Integer totalSeats) { this.totalSeats = totalSeats; }

        public Integer getAvailableSeats() { return availableSeats; }
        public void setAvailableSeats(Integer availableSeats) { this.availableSeats = availableSeats; }
    }
}