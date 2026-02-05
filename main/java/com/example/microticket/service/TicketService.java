package com.example.microticket.service;

import com.example.microticket.domain.Order;
import com.example.microticket.domain.OrderItem;
import com.example.microticket.repository.OrderItemRepository;
import com.example.microticket.repository.OrderRepository;
import com.example.microticket.repository.ScreeningRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 核心：选座锁定 -> 创建订单 -> 支付回调（确认）
 *
 * 说明：显式构造器避免 Lombok 依赖问题。
 */
@Service
public class TicketService {

    private final StringRedisTemplate redisTemplate;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ScreeningRepository screeningRepository;

    private final static long LOCK_TTL_SECONDS = 10 * 60; // 10 minutes

    public TicketService(StringRedisTemplate redisTemplate,
                         OrderRepository orderRepository,
                         OrderItemRepository orderItemRepository,
                         ScreeningRepository screeningRepository) {
        this.redisTemplate = redisTemplate;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.screeningRepository = screeningRepository;
    }

    private String lockKey(Long screeningId, String seat) {
        return "lock:screening:" + screeningId + ":seat:" + seat;
    }

    /**
     * 尝试锁定多个座位（逐个 SETNX；若任何失败，回滚已锁）
     */
    public boolean lockSeats(Long screeningId, List<String> seats, String orderNo) {
        List<String> locked = new ArrayList<>();
        for (String s : seats) {
            String key = lockKey(screeningId, s);
            Boolean ok = redisTemplate.opsForValue().setIfAbsent(key, orderNo, LOCK_TTL_SECONDS, TimeUnit.SECONDS);
            if (Boolean.TRUE.equals(ok)) {
                locked.add(key);
            } else {
                // 回滚
                for (String lk : locked) redisTemplate.delete(lk);
                return false;
            }
        }
        return true;
    }

    public void releaseSeats(Long screeningId, List<String> seats, String orderNo) {
        for (String s : seats) {
            String key = lockKey(screeningId, s);
            String val = redisTemplate.opsForValue().get(key);
            if (orderNo.equals(val)) {
                redisTemplate.delete(key);
            }
        }
    }

    @Transactional
    public Order createOrder(Long userId, Long screeningId, List<String> seats, double totalAmount, String orderNo) {
        // Insert order
        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setScreeningId(screeningId);
        order.setTotalAmount(totalAmount);
        order.setStatus("CREATED");
        order.setCreatedAt(LocalDateTime.now());
        order = orderRepository.save(order);

        // Insert order_items
        for (String s : seats) {
            String[] parts = s.split("_");
            int r = Integer.parseInt(parts[0]);
            int c = Integer.parseInt(parts[1]);
            OrderItem item = new OrderItem();
            item.setOrderId(order.getId());
            item.setSeatRow(r);
            item.setSeatCol(c);
            item.setSeatNo("R" + r + "C" + c);
            item.setPrice(totalAmount / seats.size());
            orderItemRepository.save(item);
        }
        return order;
    }

    /**
     * 支付回调处理：确认订单并扣减库存（available_seats），释放锁
     */
    @Transactional
    public boolean handlePaymentSuccess(String orderNo, String tradeNo, List<String> seats) {
        Optional<Order> opt = orderRepository.findByOrderNo(orderNo);
        if (opt.isEmpty()) return false;
        Order order = opt.get();
        if (!"CREATED".equals(order.getStatus())) return false;

        // 扣减 available_seats：使用 repository 的方法（乐观检查）
        int updated = screeningRepository.deductAvailableSeats(order.getScreeningId(), seats.size());
        if (updated <= 0) {
            // 库存不足，退款或标记异常
            return false;
        }

        // 标注订单为已支付
        order.setStatus("PAID");
        order.setPaidAt(LocalDateTime.now());
        orderRepository.save(order);

        // 删除锁（由订单号匹配）
        for (String s : seats) {
            String key = lockKey(order.getScreeningId(), s);
            String val = redisTemplate.opsForValue().get(key);
            if (orderNo.equals(val)) {
                redisTemplate.delete(key);
            }
        }
        return true;
    }
}