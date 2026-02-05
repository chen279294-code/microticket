package com.example.microticket.controller;

import com.example.microticket.domain.Order;
import com.example.microticket.service.TicketServiceFacade;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * OrderController - 提供锁座、释放座位、创建订单、查询订单、支付回调等接口。
 *
 * 使用显式构造器注入并为 DTO 提供显式 getter/setter，避免依赖 Lombok。
 */
@RestController
@RequestMapping("/api")
public class OrderController {

    private final TicketServiceFacade service;

    public OrderController(TicketServiceFacade service) {
        this.service = service;
    }

    @PostMapping("/seat/lock")
    public ResponseEntity<?> lockSeat(@RequestBody LockRequest req) {
        boolean ok = service.lockSeats(req.getScreeningId(), req.getSeats(), req.getOrderNo());
        if (ok) return ResponseEntity.ok("locked");
        else return ResponseEntity.status(409).body("failed to lock seats");
    }

    @PostMapping("/seat/release")
    public ResponseEntity<?> releaseSeat(@RequestBody ReleaseRequest req) {
        service.releaseSeats(req.getScreeningId(), req.getSeats(), req.getOrderNo());
        return ResponseEntity.ok("released");
    }

    @PostMapping("/orders")
    public ResponseEntity<Order> createOrder(@RequestBody CreateOrderRequest req) {
        Order order = service.createOrder(
                req.getUserId(),
                req.getScreeningId(),
                req.getSeats(),
                req.getTotalAmount(),
                req.getOrderNo()
        );
        return ResponseEntity.ok(order);
    }

    /**
     * GET /api/orders/{orderNo} - 查询单个订单
     */
    @GetMapping("/orders/{orderNo}")
    public ResponseEntity<Order> getOrder(@PathVariable String orderNo) {
        Optional<Order> opt = service.findByOrderNo(orderNo);
        return opt.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * GET /api/orders?userId=...
     * - 如果提供 userId，返回该用户的订单列表。
     */
    @GetMapping("/orders")
    public ResponseEntity<List<Order>> listOrders(@RequestParam(required = false) Long userId) {
        if (userId == null) {
            return ResponseEntity.badRequest().build();
        }
        List<Order> orders = service.listOrdersByUserId(userId);
        return ResponseEntity.ok(orders);
    }

    @PostMapping("/orders/{orderNo}/pay")
    public ResponseEntity<?> paymentCallback(@PathVariable String orderNo, @RequestBody PaymentRequest req) {
        boolean ok = service.handlePaymentSuccess(orderNo, req.getTradeNo(), req.getSeats());
        if (ok) return ResponseEntity.ok("paid");
        else return ResponseEntity.status(409).body("payment processing failed");
    }

    /* ----------------------- DTOs (explicit getters/setters) ----------------------- */

    public static class LockRequest {
        private Long screeningId;
        private java.util.List<String> seats;
        private String orderNo;

        public LockRequest() {}

        public Long getScreeningId() { return screeningId; }
        public void setScreeningId(Long screeningId) { this.screeningId = screeningId; }

        public java.util.List<String> getSeats() { return seats; }
        public void setSeats(java.util.List<String> seats) { this.seats = seats; }

        public String getOrderNo() { return orderNo; }
        public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    }

    public static class ReleaseRequest {
        private Long screeningId;
        private java.util.List<String> seats;
        private String orderNo;

        public ReleaseRequest() {}

        public Long getScreeningId() { return screeningId; }
        public void setScreeningId(Long screeningId) { this.screeningId = screeningId; }

        public java.util.List<String> getSeats() { return seats; }
        public void setSeats(java.util.List<String> seats) { this.seats = seats; }

        public String getOrderNo() { return orderNo; }
        public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    }

    public static class CreateOrderRequest {
        private Long userId;
        private Long screeningId;
        private java.util.List<String> seats;
        private Double totalAmount;
        private String orderNo;

        public CreateOrderRequest() {}

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }

        public Long getScreeningId() { return screeningId; }
        public void setScreeningId(Long screeningId) { this.screeningId = screeningId; }

        public java.util.List<String> getSeats() { return seats; }
        public void setSeats(java.util.List<String> seats) { this.seats = seats; }

        public Double getTotalAmount() { return totalAmount; }
        public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

        public String getOrderNo() { return orderNo; }
        public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    }

    public static class PaymentRequest {
        private String tradeNo;
        private java.util.List<String> seats;

        public PaymentRequest() {}

        public String getTradeNo() { return tradeNo; }
        public void setTradeNo(String tradeNo) { this.tradeNo = tradeNo; }

        public java.util.List<String> getSeats() { return seats; }
        public void setSeats(java.util.List<String> seats) { this.seats = seats; }
    }
}