package com.example.microticket.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    /**
     * 用于测试服务是否启动成功
     * 访问：GET http://localhost:8081/
     */
    @GetMapping("/")
    public String index() {
        return "microticket backend is running";
    }

    /**
     * 用于健康检查
     * 访问：GET http://localhost:8081/health
     */
    @GetMapping("/health")
    public String health() {
        return "ok";
    }
}
