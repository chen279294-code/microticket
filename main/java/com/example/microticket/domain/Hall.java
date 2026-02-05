package com.example.microticket.domain;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "halls")
@Data
public class Hall {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long cinemaId;
    private String name;
    private Integer rows;
    private Integer cols;
}
