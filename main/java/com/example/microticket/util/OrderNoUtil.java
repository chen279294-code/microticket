package com.example.microticket.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class OrderNoUtil {
    public static String generate() {
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String rand = UUID.randomUUID().toString().replace("-", "").substring(0,6);
        return "ORD" + time + rand;
    }
}