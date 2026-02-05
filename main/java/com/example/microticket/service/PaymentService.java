package com.example.microticket.service;

/**
 * 简单的支付 Service 接口（示例）
 */
public interface PaymentService {
    /**
     * 发起支付（返回支付 URL 或 token）
     */
    String initiatePayment(String orderNo, Double amount);

    /**
     * 处理支付网关回调（示例：解析 payload / 签名并处理业务）。
     * 返回 true 表示处理成功。
     */
    boolean handleGatewayCallback(String payload, String signature);
}