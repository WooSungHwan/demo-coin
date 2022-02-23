package com.example.democoin.upbit.db.entity;

import com.example.democoin.upbit.result.candles.MinuteCandle;
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
@Table(
        name = "five_minutes_candle",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_five_minutes_candle_timestamp", columnNames = {"timestamp"})
        },
        indexes = {
                @Index(name = "five_minutes_candle_candle_date_time_utc_idx", columnList = "candle_date_time_utc"),
                @Index(name = "five_minutes_candle_candle_date_time_kst_idx", columnList = "candle_date_time_kst")
        }
)
@Entity
public class FiveMinutesCandle {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(name = "market", length = 10)
    private String market;

    @Column(name = "candle_date_time_utc")
    private LocalDateTime candleDateTimeUtc;                // 캔들 생성 UTC 시간

    @Column(name = "candle_date_time_kst")
    private LocalDateTime candleDateTimeKst;                // 캔들 생성 KST 시간

    @Column(name = "opening_price")
    private Double openingPrice;                        // 시가

    @Column(name = "high_price")
    private Double highPrice;                           // 고가

    @Column(name = "low_price")
    private Double lowPrice;                            // 저가

    @Column(name = "trade_price")
    private Double tradePrice;                          // 종가

    private Long timestamp;                                 // 해당 캔들에서 마지막 틱이 저장된 시각

    @Column(name = "candle_acc_trade_price")
    private Double candleAccTradePrice;                 // 누적 거래 금액

    @Column(name = "candle_acc_trade_volume")
    private Double candleAccTradeVolume;                // 누적 거래량

    public static FiveMinutesCandle of(MinuteCandle candle) {
        return new FiveMinutesCandle(null,
                candle.getMarket(),
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
}
