package com.example.microticket.controller;

import com.example.microticket.repository.MovieRepository;
import com.example.microticket.repository.OrderRepository;
import com.example.microticket.repository.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/home")
@PreAuthorize("hasRole('ADMIN')")
public class AdminHomeController {

    private final MovieRepository movieRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    public AdminHomeController(MovieRepository movieRepository,
                               UserRepository userRepository,
                               OrderRepository orderRepository) {
        this.movieRepository = movieRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
    }

    /**
     * 管理员首页统计数据
     */
    @GetMapping("/dashboard")
    public DashboardDto dashboard() {
        DashboardDto dto = new DashboardDto();
        dto.setMovieCount(movieRepository.count());
        dto.setUserCount(userRepository.count());
        dto.setOrderCount(orderRepository.count());
        return dto;
    }

    /* -------- DTO -------- */
    public static class DashboardDto {
        private long movieCount;
        private long userCount;
        private long orderCount;

        public long getMovieCount() {
            return movieCount;
        }

        public void setMovieCount(long movieCount) {
            this.movieCount = movieCount;
        }

        public long getUserCount() {
            return userCount;
        }

        public void setUserCount(long userCount) {
            this.userCount = userCount;
        }

        public long getOrderCount() {
            return orderCount;
        }

        public void setOrderCount(long orderCount) {
            this.orderCount = orderCount;
        }
    }
}
