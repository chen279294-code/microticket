package com.example.microticket.controller;

import com.example.microticket.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * PaymentController - 支付相关接口示例（发起支付、接收回调）。
 *
 * 假定存在 PaymentService 提供对应逻辑。
 */
@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/initiate")
    public ResponseEntity<InitiateResponse> initiate(@RequestBody InitiateRequest req) {
        String paymentTokenOrUrl = paymentService.initiatePayment(req.getOrderNo(), req.getAmount());
        InitiateResponse resp = new InitiateResponse();
        resp.setPaymentUrlOrToken(paymentTokenOrUrl);
        return ResponseEntity.ok(resp);
    }

    /**
     * 支付网关回调（Webhook）。
     */
    @PostMapping("/callback")
    public ResponseEntity<String> callback(@RequestBody String payload, @RequestHeader(value = "X-Signature", required = false) String signature) {
        boolean ok = paymentService.handleGatewayCallback(payload, signature);
        if (ok) return ResponseEntity.ok("success");
        else return ResponseEntity.status(400).body("failed");
    }

    /* ---------------- DTOs ---------------- */

    public static class InitiateRequest {
        private String orderNo;
        private Double amount;
        private String returnUrl;

        public InitiateRequest() {}

        public String getOrderNo() { return orderNo; }
        public void setOrderNo(String orderNo) { this.orderNo = orderNo; }

        public Double getAmount() { return amount; }
        public void setAmount(Double amount) { this.amount = amount; }

        public String getReturnUrl() { return returnUrl; }
        public void setReturnUrl(String returnUrl) { this.returnUrl = returnUrl; }
    }

    public static class InitiateResponse {
        private String paymentUrlOrToken;

        public InitiateResponse() {}

        public String getPaymentUrlOrToken() { return paymentUrlOrToken; }
        public void setPaymentUrlOrToken(String paymentUrlOrToken) { this.paymentUrlOrToken = paymentUrlOrToken; }
    }
}