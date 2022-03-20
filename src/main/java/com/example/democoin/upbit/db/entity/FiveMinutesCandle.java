package com.example.democoin.upbit.db.entity;

import com.example.democoin.upbit.enums.MarketType;
import com.example.democoin.upbit.result.candles.MinuteCandle;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

import static java.math.RoundingMode.HALF_UP;
import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@EqualsAndHashCode(of = "id")
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
@Table(
        name = "five_minutes_candle",
        indexes = {
                @Index(name = "fmc_market_candle_date_time_utc_idx", columnList = "market,candle_date_time_utc"),
                @Index(name = "fmc_market_candle_date_time_kst_desc_timestamp_idx", columnList = "market,candle_date_time_kst desc,timestamp"),
                @Index(name = "fmc_market_candle_date_time_kst_idx", columnList = "market,candle_date_time_kst"),
                @Index(name = "fmc_market_timestamp_idx", columnList = "market,timestamp")
        }
)
@Entity
public class FiveMinutesCandle {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(name = "market", length = 10)
    private MarketType market;

    @Column(name = "candle_date_time_utc")
    private LocalDateTime candleDateTimeUtc;                // 캔들 생성 UTC 시간

    @Column(name = "candle_date_time_kst")
    private LocalDateTime candleDateTimeKst;                // 캔들 생성 KST 시간

    @Column(name = "opening_price")
    private Double openingPrice;                            // 시가

    @Column(name = "high_price")
    private Double highPrice;                               // 고가

    @Column(name = "low_price")
    private Double lowPrice;                                // 저가

    @Column(name = "trade_price")
    private Double tradePrice;                              // 종가

    private Long timestamp;                                 // 해당 캔들에서 마지막 틱이 저장된 시각

    @Column(name = "candle_acc_trade_price")
    private Double candleAccTradePrice;                     // 누적 거래 금액

    @Column(name = "candle_acc_trade_volume")
    private Double candleAccTradeVolume;                    // 누적 거래량

    public static FiveMinutesCandle of(MinuteCandle candle) {
        return new FiveMinutesCandle(null,
                MarketType.find(candle.getMarket()),
                candle.getCandleDateTimeUtc(),
                candle.getCandleDateTimeKst(),
                candle.getOpeningPrice(),
                candle.getHighPrice(),
                candle.getLowPrice(),
                candle.getTradePrice(),
                candle.getTimestamp(),
                candle.getCandleAccTradePrice(),
                candle.getCandleAccTradeVolume());
    }

    // 캔들의 상승률
    public double getCandlePercent() {
        return BigDecimal.valueOf((this.tradePrice / this.openingPrice * 100) - 100)
                .setScale(2, HALF_UP)
                .doubleValue();
    }

    // 캔들 양봉 여부
    public boolean isPositive() {
        return getCandlePercent() > 0;
    }

    // 캔들 음봉 여부
    public boolean isNegative() {
        return !isPositive();
    }
}
