package com.example.democoin.backtest.common;

import com.example.democoin.upbit.enums.OrdSideType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.time.LocalDateTime;

import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
@Table
@Entity
public class BackTestOrders {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(name = "market", length = 10)
    private String market;

    @Enumerated(EnumType.STRING)
    private OrdSideType side;

    @Column(name = "price")
    private Double price; // 1코인 당 거래가격

    @Column(name = "volume")
    private Double volume; // 거래량

    @Column(name = "fee")
    private Double fee; // 수수료

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "timestamp")
    private long timestamp; // 매수한 캔들의 타임스탬프

    public static BackTestOrders of(String market, OrdSideType side, Double price, Double volume, Double fee, long timestamp) {
        return new BackTestOrders(null, market, side, price, volume, fee, LocalDateTime.now(), timestamp);
    }

}
