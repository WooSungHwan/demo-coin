package com.example.democoin.backtest.common;

import com.example.democoin.upbit.enums.MarketType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

import static java.math.RoundingMode.HALF_UP;
import static javax.persistence.EnumType.STRING;
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
    private MarketType market;

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

    @Column(name = "proceed_rate")
    private Double proceedRate; // 수익률

    @Column(name = "balance")
    private Double balance; // 잔액

    public static AccountCoinWallet of(MarketType market, Double tradePrice, Double tradeVolume, Double allPrice, Double balance) {
        double valAmount = tradePrice * tradeVolume;
        double proceeds = valAmount - allPrice;
        double proceedRate = proceeds / allPrice * 100;
        return new AccountCoinWallet(null, market, tradePrice, tradeVolume, allPrice, proceeds, valAmount, proceedRate, balance);
    }

    public static AccountCoinWallet of(MarketType market, Double balance) {
        return AccountCoinWallet.builder()
                .market(market)
                .balance(balance)
                .build();
    }

    @Deprecated
    public void addBid(Double tradePrice, Double bidVolume, Double bidPrice, Double fee) {
        this.volume += bidVolume;
        this.allPrice += (bidPrice + fee);
        this.avgPrice = this.allPrice / this.volume;
        this.valAmount = tradePrice * volume;
        this.proceeds = this.valAmount - this.allPrice;
        this.proceedRate = this.proceeds / this.allPrice * 100;
    }

    @Deprecated
    public void addAsk(Double tradePrice, Double askVolume, Double askPrice, Double fee) {
        this.volume -= askVolume;
        this.allPrice -= (askPrice + fee);
        this.avgPrice = this.allPrice / this.volume;
        this.valAmount = tradePrice * volume;
        this.proceeds = this.valAmount - this.allPrice;
    }

    public void allAsk(Double tradePrice) {
        this.balance += tradePrice * valAmount * 0.0005; // 수수료 포함
        this.avgPrice = null;
        this.volume = null;
        this.allPrice = null;
        this.proceeds = null;
        this.valAmount = null;
        this.proceedRate = null;
    }

    public void allBid(double tradePrice, double price, double volume, double fee) {
        this.avgPrice = tradePrice;
        this.volume = volume;
        this.allPrice = price;
        this.valAmount = this.allPrice - fee;
        this.balance = 0d;
        this.proceeds = this.valAmount - this.allPrice;
        this.proceedRate = this.proceeds / this.allPrice * 100;
    }

    public void fetch(double tradePrice) {
        this.valAmount = tradePrice * volume;
        this.proceeds = this.valAmount - this.allPrice;
        this.proceedRate = this.proceeds / this.allPrice * 100;
    }

    public boolean isEmpty() {
        return Objects.isNull(volume) || volume == 0;
    }
}
