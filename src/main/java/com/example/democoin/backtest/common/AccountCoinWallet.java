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

    @Column(name = "proceeds")
    private Double proceeds; // 수익금

    @Column(name = "val_amount")
    private Double valAmount; // 평가금액

    public static AccountCoinWallet of(String market, Double tradePrice, Double tradeVolume, Double allPrice) {
        double valAmount = tradePrice * tradeVolume;
        double proceeds = valAmount - allPrice;
        return new AccountCoinWallet(null, market, tradePrice, tradeVolume, allPrice, proceeds, valAmount);
    }

    public void addBid(Double tradePrice, Double bidVolume, Double bidPrice, Double fee) {
        this.volume += bidVolume;
        this.allPrice += (bidPrice + fee);
        this.avgPrice = this.allPrice / this.volume;
        this.valAmount = tradePrice * volume;
        this.proceeds = this.valAmount - this.allPrice;
    }

    public void addAsk(Double tradePrice, Double askVolume, Double askPrice, Double fee) {
        this.volume -= askVolume;
        this.allPrice -= (askPrice + fee);
        this.avgPrice = this.allPrice / this.volume;
        this.valAmount = tradePrice * volume;
        this.proceeds = this.valAmount - this.allPrice;
    }

    public double 수익률() {
        return (proceeds / allPrice * 100);
    }
}
