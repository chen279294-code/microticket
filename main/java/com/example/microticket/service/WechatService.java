package com.example.microticket.service;

import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 微信相关接口的 stub。真实项目需要实现：
 * - code -> openid/session_key（调用微信 API）
 * - 调用统一下单接口生成 prepay_id
 * - 支付签名生成
 */
@Service
public class WechatService {

    public Map<String, String> code2Session(String code) {
        // TODO: 调用微信 API 换取 openid & session_key
        return Map.of("openid", "mock_openid_" + code, "session_key", "mock_session");
    }

    public Map<String, String> unifyOrder(String orderNo, double totalAmount) {
        // TODO: 调用微信统一下单 API，返回 prepay_id 等
        return Map.of("prepay_id", "mock_prepay_" + orderNo);
    }
}