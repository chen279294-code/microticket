package com.example.microticket.controller;

import com.example.microticket.domain.Movie;
import com.example.microticket.service.MovieService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * MovieController - 影片管理 CRUD 示例。
 */
@RestController
@RequestMapping("/api/movies")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping
    public ResponseEntity<List<Movie>> listAll() {
        List<Movie> movies = movieService.findAll();
        return ResponseEntity.ok(movies);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Movie> getById(@PathVariable Long id) {
        Movie m = movieService.findById(id);
        if (m == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(m);
    }

    @PostMapping
    public ResponseEntity<Movie> create(@RequestBody MovieCreateRequest req) {
        Movie movie = new Movie();
        movie.setTitle(req.getTitle());
        movie.setDescription(req.getDescription());
        movie.setDurationMinutes(req.getDurationMinutes());
        Movie created = movieService.create(movie);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Movie> update(@PathVariable Long id, @RequestBody MovieCreateRequest req) {
        Movie movie = new Movie();
        movie.setTitle(req.getTitle());
        movie.setDescription(req.getDescription());
        movie.setDurationMinutes(req.getDurationMinutes());
        Movie updated = movieService.update(id, movie);
        if (updated == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        movieService.delete(id);
        return ResponseEntity.ok().build();
    }

    /* ---------------- DTO ---------------- */

    public static class MovieCreateRequest {
        private String title;
        private String description;
        private Integer durationMinutes;

        public MovieCreateRequest() {}

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public Integer getDurationMinutes() { return durationMinutes; }
        public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }
    }
}