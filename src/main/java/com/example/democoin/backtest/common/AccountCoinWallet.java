package com.example.democoin.backtest.common;

import com.example.democoin.upbit.enums.MarketType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.example.democoin.utils.NumberUtils;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

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

    @Column(name = "max_proceed_rate")
    private Double maxProceedRate; // 최고 수익률

    @Deprecated
    public static AccountCoinWallet of(MarketType market, Double tradePrice, Double tradeVolume, Double allPrice, Double balance) {
        double valAmount = tradePrice * tradeVolume;
        double proceeds = valAmount - allPrice;
        double proceedRate = proceeds / allPrice * 100;
        return new AccountCoinWallet(null, market, tradePrice, tradeVolume, allPrice, proceeds, valAmount, proceedRate, balance, proceedRate);
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
        double amount = tradePrice * this.volume;
        this.balance += (amount - (amount * 0.0005)); // 매도 수수료 포함
        this.avgPrice = null;
        this.volume = null;
        this.allPrice = null;
        this.proceeds = null;
        this.valAmount = null;
        this.proceedRate = null;
        this.maxProceedRate = null;
        this.balance = new BigDecimal(this.balance).setScale(2, HALF_UP).doubleValue();
    }

    public void allBid(double tradePrice, double price, double volume, double fee) {
        this.avgPrice = tradePrice;
        this.volume = new BigDecimal(volume).setScale(8, HALF_UP).doubleValue();
        this.allPrice = price + fee;
        this.valAmount = new BigDecimal(this.allPrice).setScale(2, HALF_UP).doubleValue();
        this.balance = 0d;
        this.proceeds = new BigDecimal(this.valAmount - this.allPrice).setScale(2, HALF_UP).doubleValue();
        double proceedRate = new BigDecimal(this.proceeds / this.allPrice * 100).setScale(2, HALF_UP).doubleValue();
        this.maxProceedRate = NumberUtils.max(this.proceedRate, proceedRate);
        this.proceedRate = proceedRate;
    }

    public void fetch(double tradePrice) {
        this.valAmount = new BigDecimal(tradePrice * volume).setScale(2, HALF_UP).doubleValue();
        this.proceeds = new BigDecimal(this.valAmount - this.allPrice).setScale(2, HALF_UP).doubleValue();
        double proceedRate = new BigDecimal(this.proceeds / this.allPrice * 100).setScale(2, HALF_UP).doubleValue();
        this.maxProceedRate = NumberUtils.max(this.proceedRate, proceedRate);
        this.proceedRate = proceedRate;
    }

    public boolean isEmpty() {
        return Objects.isNull(volume) || volume == 0;
    }

    public boolean isMaxProceedRateFall() {
        if (this.proceedRate < 5) return false;
        return this.maxProceedRate > 5 && this.maxProceedRate - 2 >= this.proceedRate;
    }
}
