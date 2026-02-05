package com.example.microticket.service;

import com.example.microticket.domain.Order;
import com.example.microticket.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 简单的 Facade，包装底层 TicketService 并补充一些查询方法，使用显式构造器避免 Lombok 依赖问题。
 */
@Service
public class TicketServiceFacade {

    private final TicketService ticketService;
    private final OrderRepository orderRepository;

    public TicketServiceFacade(TicketService ticketService, OrderRepository orderRepository) {
        this.ticketService = ticketService;
        this.orderRepository = orderRepository;
    }

    public boolean lockSeats(Long screeningId, java.util.List<String> seats, String orderNo) {
        return ticketService.lockSeats(screeningId, seats, orderNo);
    }

    public void releaseSeats(Long screeningId, java.util.List<String> seats, String orderNo) {
        ticketService.releaseSeats(screeningId, seats, orderNo);
    }

    @Transactional
    public Order createOrder(Long userId, Long screeningId, java.util.List<String> seats, double totalAmount, String orderNo) {
        return ticketService.createOrder(userId, screeningId, seats, totalAmount, orderNo);
    }

    @Transactional
    public boolean handlePaymentSuccess(String orderNo, String tradeNo, java.util.List<String> seats) {
        return ticketService.handlePaymentSuccess(orderNo, tradeNo, seats);
    }

    /**
     * 通过 orderNo 查询订单
     */
    public Optional<Order> findByOrderNo(String orderNo) {
        return orderRepository.findByOrderNo(orderNo);
    }

    /**
     * 简单按 userId 筛选订单（示例）。若订单量大建议在 repository 增加查询方法。
     */
    public List<Order> listOrdersByUserId(Long userId) {
        return orderRepository.findAll().stream()
                .filter(o -> o.getUserId() != null && o.getUserId().equals(userId))
                .collect(Collectors.toList());
    }
}