package com.example.microticket.service;

import com.example.microticket.domain.Movie;
import com.example.microticket.repository.MovieRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 基于 JPA 的 MovieService 实现
 */
@Service
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;

    public MovieServiceImpl(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @Override
    public List<Movie> findAll() {
        return movieRepository.findAll();
    }

    @Override
    public Movie findById(Long id) {
        Optional<Movie> opt = movieRepository.findById(id);
        return opt.orElse(null);
    }

    @Override
    public Movie create(Movie movie) {
        return movieRepository.save(movie);
    }

    @Override
    public Movie update(Long id, Movie movie) {
        return movieRepository.findById(id).map(existing -> {
            existing.setTitle(movie.getTitle());
            existing.setDescription(movie.getDescription());
            existing.setDurationMinutes(movie.getDurationMinutes());
            return movieRepository.save(existing);
        }).orElse(null);
    }

    @Override
    public void delete(Long id) {
        movieRepository.deleteById(id);
    }
}