package com.example.democoin.backtest.entity;

import com.example.democoin.upbit.enums.MarketType;
import com.example.democoin.utils.NumberUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

import static com.example.democoin.DemoCoinApplication.df;
import static java.math.RoundingMode.HALF_EVEN;
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

    @Column(name = "bid_time")
    private LocalDateTime bidTime; // 매수캔들시간(utc)

    public static AccountCoinWallet of(MarketType market, Double balance) {
        return AccountCoinWallet.builder()
                .market(market)
                .balance(balance)
                .build();
    }

    public void allAsk(double valAmount, double fee) {
        this.balance += (valAmount - fee);
        this.avgPrice = null;
        this.volume = null;
        this.allPrice = null;
        this.proceeds = null;
        this.valAmount = null;
        this.proceedRate = null;
        this.maxProceedRate = null;
        this.bidTime = null;
    }

    public void allBid(double tradePrice, double bidAmount, double volume, double fee, LocalDateTime bidTime) {
        this.avgPrice = tradePrice;
        this.volume = volume;
        this.allPrice = bidAmount - fee;
        this.valAmount = bidAmount - fee;
        this.balance = 0d;
        this.bidTime = bidTime; // utc

        this.proceeds = this.valAmount - this.allPrice;
        double proceedRate = this.proceeds / this.allPrice * 100;
        this.maxProceedRate = NumberUtils.max(this.maxProceedRate, proceedRate);
        this.proceedRate = proceedRate;
//        setProceeds();
    }

    public void fetch(double tradePrice) {
        if (!isEmpty()) {

            this.valAmount = tradePrice * this.volume;
            this.proceeds = this.valAmount - this.allPrice;
            double proceedRate = this.proceeds / this.allPrice * 100;
            this.maxProceedRate = NumberUtils.max(this.maxProceedRate, proceedRate);
            this.proceedRate = proceedRate;

//            setProceeds();
        }
    }

    public boolean isEmpty() {
        return Objects.isNull(volume) || volume == 0;
    }

    public boolean isNotEmpty() {
        return !isEmpty();
    }

    public boolean isMaxProceedRateFall() {
        if (!isEmpty()) {
            if (this.proceedRate < 3) {
                return false;
            }
            if (this.maxProceedRate >= 3) {
                return this.maxProceedRate - 2 >= this.proceedRate;
            }
        }
        return false;
    }

    public void rebalance(Double balance) {
        this.balance = balance;
    }

    public void addAmount(double amount) {
        if (this.balance != null) {
            this.balance += amount;
        } else {
            this.balance = amount;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AccountCoinWallet that = (AccountCoinWallet) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (market != that.market) return false;
        if (avgPrice != null ? !avgPrice.equals(that.avgPrice) : that.avgPrice != null) return false;
        if (volume != null ? !volume.equals(that.volume) : that.volume != null) return false;
        if (allPrice != null ? !allPrice.equals(that.allPrice) : that.allPrice != null) return false;
        if (proceeds != null ? !proceeds.equals(that.proceeds) : that.proceeds != null) return false;
        if (valAmount != null ? !valAmount.equals(that.valAmount) : that.valAmount != null) return false;
        if (proceedRate != null ? !proceedRate.equals(that.proceedRate) : that.proceedRate != null) return false;
        if (balance != null ? !balance.equals(that.balance) : that.balance != null) return false;
        if (bidTime != null ? !bidTime.equals(that.bidTime) : that.bidTime != null) return false;
        return maxProceedRate != null ? maxProceedRate.equals(that.maxProceedRate) : that.maxProceedRate == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (market != null ? market.hashCode() : 0);
        result = 31 * result + (avgPrice != null ? avgPrice.hashCode() : 0);
        result = 31 * result + (volume != null ? volume.hashCode() : 0);
        result = 31 * result + (allPrice != null ? allPrice.hashCode() : 0);
        result = 31 * result + (proceeds != null ? proceeds.hashCode() : 0);
        result = 31 * result + (valAmount != null ? valAmount.hashCode() : 0);
        result = 31 * result + (proceedRate != null ? proceedRate.hashCode() : 0);
        result = 31 * result + (balance != null ? balance.hashCode() : 0);
        result = 31 * result + (maxProceedRate != null ? maxProceedRate.hashCode() : 0);
        result = 31 * result + (bidTime != null ? bidTime.hashCode() : 0);
        return result;
    }
}
