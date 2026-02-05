package com.example.microticket.repository;

import com.example.microticket.domain.Movie;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    /**
     * 根据标题模糊查询（搜索用）
     */
    List<Movie> findByTitleContaining(String title);

    /**
     * 获取最新上映电影（用户首页）
     */
    List<Movie> findAllByOrderByIdDesc(Pageable pageable);
    List<Movie> findTop8ByOrderByIdDesc();

    /**
     * 电影总数（后台统计）
     */
    @Query("select count(m) from Movie m")
    long countMovies();
}
