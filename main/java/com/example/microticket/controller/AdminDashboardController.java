package com.example.microticket.controller;

import com.example.microticket.repository.CinemaRepository;
import com.example.microticket.repository.MovieRepository;
import com.example.microticket.repository.OrderRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/admin")
public class AdminDashboardController {

    private final MovieRepository movieRepository;
    private final OrderRepository orderRepository;
    private final CinemaRepository cinemaRepository;

    public AdminDashboardController(MovieRepository movieRepository,
                                    OrderRepository orderRepository,
                                    CinemaRepository cinemaRepository) {
        this.movieRepository = movieRepository;
        this.orderRepository = orderRepository;
        this.cinemaRepository = cinemaRepository;
    }

    // 顶部4个统计卡片
    @GetMapping("/stats")
    public StatsResp stats() {
        long movieCount = movieRepository.count();
        long cinemaCount = cinemaRepository.count();
        BigDecimal todayBox = orderRepository.sumTodayPaid();
        BigDecimal totalBox = orderRepository.sumTotalPaid();
        return new StatsResp(movieCount, cinemaCount, todayBox, totalBox);
    }

    // 图表数据
    @GetMapping("/charts")
    public ChartsResp charts() {
        ChartsResp resp = new ChartsResp();

        // ===== 近7天票房：补齐没有订单的日期为0 =====
        LocalDate today = LocalDate.now();
        LocalDate startDay = today.minusDays(6);
        LocalDateTime startTime = startDay.atStartOfDay();

        Map<LocalDate, BigDecimal> dayMap = new HashMap<>();
        List<Object[]> rows = orderRepository.sumPaidByDaySince(startTime);
        for (Object[] row : rows) {
            LocalDate d = ((java.sql.Date) row[0]).toLocalDate();
            BigDecimal s = (BigDecimal) row[1];
            dayMap.put(d, s);
        }

        List<String> weekDates = new ArrayList<>();
        List<BigDecimal> weekBox = new ArrayList<>();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MM-dd");
        for (int i = 0; i < 7; i++) {
            LocalDate d = startDay.plusDays(i);
            weekDates.add(d.format(fmt));
            weekBox.add(dayMap.getOrDefault(d, BigDecimal.ZERO));
        }
        resp.weekDates = weekDates;
        resp.weekBox = weekBox;

        // ===== 按“类型”统计（库里没有类型字段 -> 从 description 识别） =====
        Map<String, Integer> typeCount = new LinkedHashMap<>();
        movieRepository.findAll().forEach(m -> {
            String type = deriveType(m.getDescription());
            typeCount.put(type, typeCount.getOrDefault(type, 0) + 1);
        });

        List<PieItem> typeMovieCount = new ArrayList<>();
        for (Map.Entry<String, Integer> e : typeCount.entrySet()) {
            typeMovieCount.add(new PieItem(e.getKey(), e.getValue()));
        }
        resp.typeMovieCount = typeMovieCount;

        Map<String, BigDecimal> typeBoxMap = new LinkedHashMap<>();
        for (Object[] row : orderRepository.sumPaidByMovie()) {
            String desc = (String) row[0];
            BigDecimal sum = (BigDecimal) row[1];
            String type = deriveType(desc);
            typeBoxMap.put(type, typeBoxMap.getOrDefault(type, BigDecimal.ZERO).add(sum));
        }

        List<String> types = new ArrayList<>(typeBoxMap.keySet());
        List<BigDecimal> values = new ArrayList<>();
        for (String t : types) values.add(typeBoxMap.get(t));

        resp.typeBoxOffice = new TypeBoxOffice(types, values);
        return resp;
    }

    // 从电影描述里自动识别类型（Java11 兼容）
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

    // ===== DTO =====

    public static class StatsResp {
        public long movieCount;
        public long cinemaCount;
        public BigDecimal todayBox;
        public BigDecimal totalBox;

        public StatsResp(long movieCount, long cinemaCount, BigDecimal todayBox, BigDecimal totalBox) {
            this.movieCount = movieCount;
            this.cinemaCount = cinemaCount;
            this.todayBox = todayBox;
            this.totalBox = totalBox;
        }
    }

    public static class ChartsResp {
        public List<String> weekDates;
        public List<BigDecimal> weekBox;
        public List<PieItem> typeMovieCount;
        public TypeBoxOffice typeBoxOffice;
    }

    public static class PieItem {
        public String name;
        public int value;

        public PieItem(String name, int value) {
            this.name = name;
            this.value = value;
        }
    }

    public static class TypeBoxOffice {
        public List<String> types;
        public List<BigDecimal> values;

        public TypeBoxOffice(List<String> types, List<BigDecimal> values) {
            this.types = types;
            this.values = values;
        }
    }
}
