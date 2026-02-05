package com.example.microticket.domain;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(
        name = "seats",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"hall_id", "row_num", "col_num"})
        }
)
@Data
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "hall_id", nullable = false)
    private Long hallId;

    @Column(name = "row_num", nullable = false)
    private Integer rowNum;

    @Column(name = "col_num", nullable = false)
    private Integer colNum;

    @Column(name = "seat_no")
    private String seatNo;
    private String type;
}