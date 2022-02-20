package com.example.democoin.upbit.result.candles;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class MinuteCandle {
    private String market;
    private LocalDateTime candle_date_time_utc;
    private LocalDateTime candle_date_time_kst;
    private BigDecimal opening_price;
    private BigDecimal high_price;
    private BigDecimal low_price;
    private BigDecimal trade_price;
    private Long timestamp;
    private BigDecimal candle_acc_trade_price;
    private BigDecimal candle_acc_trade_volume;
    private Integer unit;
}
