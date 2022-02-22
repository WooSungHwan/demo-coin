package com.example.democoin.upbit.result.candles;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.Column;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class MinuteCandle {
    private String market;                                  // 시장 KRW-BTC
    @JsonProperty("candle_date_time_utc")
    private LocalDateTime candleDateTimeUtc;                // 캔들 생성 UTC 시간
    @JsonProperty("candle_date_time_kst")
    private LocalDateTime candleDateTimeKst;                // 캔들 생성 KST 시간
    @JsonProperty("opening_price")
    private Double openingPrice;                        // 시가
    @JsonProperty("high_price")
    private Double highPrice;                           // 고가
    @JsonProperty("low_price")
    private Double lowPrice;                            // 저가
    @JsonProperty("trade_price")
    private Double tradePrice;                          // 종가
    private Long timestamp;                                 // 해당 캔들에서 마지막 틱이 저장된 시각
    @JsonProperty("candle_acc_trade_price")
    private Double candleAccTradePrice;                 // 누적 거래 금액
    @JsonProperty("candle_acc_trade_volume")
    private Double candleAccTradeVolume;                // 누적 거래량
}
