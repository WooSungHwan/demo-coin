package com.example.democoin.backtest.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static java.math.RoundingMode.HALF_UP;
import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
@Table
@Entity
public class AccountCoinWallet {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(name = "market")
    private String market;

    @Column(name = "avg_price")
    private Double avgPrice; // 평단가

    @Column(name = "volume")
    private Double volume;

    @Column(name = "all_price")
    private Double allPrice; // 총 매수 KRW

    public static AccountCoinWallet of(String market, Double tradePrice, Double tradeVolume, Double allPrice) {
        return new AccountCoinWallet(null, market, tradePrice, tradeVolume, allPrice);
    }

    public void addBid(Double bidVolume, Double bidPrice, Double fee) {
        this.volume += bidVolume;
        this.allPrice += bidPrice + fee;
        this.avgPrice = allPrice / volume;
    }

    public void addAsk(Double askVolume, Double askPrice, Double fee) {
        this.volume -= askVolume;
        this.allPrice -= askPrice + fee;
        this.avgPrice = allPrice / volume;
    }

    public double 수익률(double nowPrice) {
        return new BigDecimal(((nowPrice / this.avgPrice) * 100) - 100).setScale(2, HALF_UP).doubleValue();
    }
}
