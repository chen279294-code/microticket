package com.example.microticket.service;

import com.example.microticket.dto.AdminChartsDto;
import com.example.microticket.dto.AdminStatsDto;
import com.example.microticket.repository.CinemaRepository;
import com.example.microticket.repository.MovieRepository;
import com.example.microticket.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class AdminDashboardService {

    private final MovieRepository movieRepository;
    private final CinemaRepository cinemaRepository;
    private final OrderRepository orderRepository;

    // ✅ 唯一构造器（只保留这一个）
    public AdminDashboardService(MovieRepository movieRepository,
                                 CinemaRepository cinemaRepository,
                                 OrderRepository orderRepository) {
        this.movieRepository = movieRepository;
        this.cinemaRepository = cinemaRepository;
        this.orderRepository = orderRepository;
    }

    public AdminStatsDto stats() {
        long movieCount = movieRepository.count();
        long cinemaCount = cinemaRepository.count();
        BigDecimal today = orderRepository.sumTodayPaid();
        BigDecimal total = orderRepository.sumTotalPaid();
        return new AdminStatsDto(movieCount, cinemaCount, today, total);
    }

    public AdminChartsDto charts() {
        AdminChartsDto dto = new AdminChartsDto();

        LocalDate today = LocalDate.now();
        LocalDate startDay = today.minusDays(6);
        LocalDateTime startTime = startDay.atStartOfDay();

        Map<LocalDate, BigDecimal> daySum = new HashMap<>();
        for (Object[] row : orderRepository.sumPaidByDaySince(startTime)) {
            LocalDate d = ((java.sql.Date) row[0]).toLocalDate();
            BigDecimal s = (BigDecimal) row[1];
            daySum.put(d, s);
        }

        List<String> weekDates = new ArrayList<>();
        List<BigDecimal> weekBox = new ArrayList<>();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MM-dd");
        for (int i = 0; i < 7; i++) {
            LocalDate d = startDay.plusDays(i);
            weekDates.add(d.format(fmt));
            weekBox.add(daySum.getOrDefault(d, BigDecimal.ZERO));
        }
        dto.setWeekDates(weekDates);
        dto.setWeekBox(weekBox);

        Map<String, Integer> typeCount = new LinkedHashMap<>();
        movieRepository.findAll().forEach(m -> {
            String type = deriveType(m.getDescription());
            typeCount.put(type, typeCount.getOrDefault(type, 0) + 1);
        });

        List<AdminChartsDto.NameValue> typeMovieCount = new ArrayList<>();
        typeCount.forEach((k, v) -> typeMovieCount.add(new AdminChartsDto.NameValue(k, v)));
        dto.setTypeMovieCount(typeMovieCount);

        Map<String, BigDecimal> typeBox = new LinkedHashMap<>();
        for (Object[] row : orderRepository.sumPaidByMovie()) {
            String desc = (String) row[0];
            BigDecimal sum = (BigDecimal) row[1];
            String type = deriveType(desc);
            typeBox.put(type, typeBox.getOrDefault(type, BigDecimal.ZERO).add(sum));
        }

        List<String> types = new ArrayList<>(typeBox.keySet());
        List<BigDecimal> values = new ArrayList<>();
        for (String t : types) values.add(typeBox.get(t));
        dto.setTypeBoxOffice(new AdminChartsDto.TypeBoxOffice(types, values));

        return dto;
    }

    private String deriveType(String desc) {
        if (desc == null) return "其他";
        String s = desc.trim();
        if (s.contains("科幻")) return "科幻";
        if (s.contains("动画")) return "动画";
        if (s.contains("喜剧")) return "喜剧";
        if (s.contains("剧情")) return "剧情";
        if (s.contains("爱情")) return "爱情";
        if (s.contains("动作")) return "动作";
        if (s.contains("纪录")) return "纪录片";
        if (s.contains("恐怖")) return "恐怖";
        if (s.contains("悬疑")) return "悬疑";
        return "其他";
    }
}
