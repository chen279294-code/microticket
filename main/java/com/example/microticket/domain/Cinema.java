package com.example.microticket.domain;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "cinemas")
public class Cinema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 影院名称
    @Column(nullable = false)
    private String name;

    // 地址
    @Column(nullable = false)
    private String address;

    // 联系电话
    private String phone;

    // 创建时间
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
