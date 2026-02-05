package com.example.microticket.service;

import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Mock 实现：用于本地测试与示例，不接真实支付网关。
 */
@Service
public class PaymentServiceImpl implements PaymentService {

    @Override
    public String initiatePayment(String orderNo, Double amount) {
        return "https://pay.mock.local/checkout?order=" + orderNo + "&amount=" + amount + "&token=" + UUID.randomUUID();
    }

    @Override
    public boolean handleGatewayCallback(String payload, String signature) {
        // 示例：不做真实签名校验，直接返回 true
        return true;
    }
}