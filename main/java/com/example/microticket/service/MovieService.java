package com.example.microticket.service;

import com.example.microticket.domain.Movie;

import java.util.List;

public interface MovieService {
    List<Movie> findAll();
    Movie findById(Long id);
    Movie create(Movie movie);
    Movie update(Long id, Movie movie);
    void delete(Long id);
}