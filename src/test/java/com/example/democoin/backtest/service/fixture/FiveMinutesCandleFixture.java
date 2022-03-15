package com.example.democoin.backtest.service.fixture;

import com.example.democoin.upbit.db.entity.FiveMinutesCandle;
import com.example.democoin.upbit.enums.MarketType;

import java.time.LocalDateTime;

public class FiveMinutesCandleFixture {

    public static FiveMinutesCandle standardFiveMinutesCandle() {
        return FiveMinutesCandle.builder()
                .market(MarketType.KRW_BTC)
                .id(1L)
                .candleAccTradePrice(100d)
                .candleAccTradeVolume(1000d)
                .candleDateTimeKst(LocalDateTime.now())
                .candleDateTimeUtc(LocalDateTime.now())
                .openingPrice(5d)
                .highPrice(10d)
                .lowPrice(1d)
                .tradePrice(7d)
                .timestamp(1645630200133L)
                .build();
    }

}
