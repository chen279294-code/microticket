package com.example.microticket.controller;

import com.example.microticket.domain.Movie;
import com.example.microticket.repository.MovieRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user/home")
public class UserHomeController {

    private final MovieRepository movieRepository;

    // 构造器注入（推荐写法）
    public UserHomeController(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    /**
     * 用户首页：热映电影（最新 8 部）
     */
    @GetMapping("/hot")
    public List<Movie> hotMovies() {
        return movieRepository.findTop8ByOrderByIdDesc();
    }
}
