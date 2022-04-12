package com.example.democoin.backtest.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

/**
 * 일자별 수익률을 기록한다.
 */
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
@Table
@Entity
public class ResultHistory {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(name = "base_date")
    private String baseDate;

    @Column(name = "assets")
    private Double assets;

}
