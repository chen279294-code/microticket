package com.example.microticket.domain;

import javax.persistence.*;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Screening 实体：表示一个放映场次、可售座位等信息
 */
@Entity
@Table(name = "screenings")
public class Screening implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 场次的总座位数（可选）
    private Integer totalSeats;

    // 剩余��售座位
    private Integer availableSeats;

    public Screening() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getTotalSeats() { return totalSeats; }
    public void setTotalSeats(Integer totalSeats) { this.totalSeats = totalSeats; }

    public Integer getAvailableSeats() { return availableSeats; }
    public void setAvailableSeats(Integer availableSeats) { this.availableSeats = availableSeats; }
}