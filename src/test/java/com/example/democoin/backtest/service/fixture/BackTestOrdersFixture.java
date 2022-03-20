package com.example.democoin.backtest.service.fixture;

import com.example.democoin.backtest.entity.BackTestOrders;
import com.example.democoin.upbit.db.entity.FiveMinutesCandle;
import com.example.democoin.upbit.enums.MarketType;
import com.example.democoin.upbit.enums.OrdSideType;

import java.time.LocalDateTime;

import static com.example.democoin.upbit.enums.OrdSideType.ASK;
import static com.example.democoin.upbit.enums.OrdSideType.BID;

public class BackTestOrdersFixture {

    public static BackTestOrders standardBackTestOrders(OrdSideType ordSideType) {
        return BackTestOrders.builder()
                .id(1L)
                .market(MarketType.KRW_BTC)
                .side(ordSideType)
                .volume(100d)
                .price(1000d)
                .fee(5d)
                .proceeds(10d)
                .proceedRate(10d)
                .maxProceedRate(10d)
                .timestamp(1645630200133L)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static BackTestOrders bidBackTestOrders(FiveMinutesCandle targetCandle, double fee, double volume) {
        return BackTestOrders.builder()
                .id(1L)
                .market(targetCandle.getMarket())
                .side(BID)
                .price(targetCandle.getTradePrice())
                .volume(volume)
                .fee(fee)
                .timestamp(targetCandle.getTimestamp())
                .build();
    }

    public static BackTestOrders askBackTestOrders(FiveMinutesCandle targetCandle,
                                                   double fee,
                                                   double volume,
                                                   double proceeds,
                                                   double proceedRate,
                                                   double maxProceedRate) {
        return BackTestOrders.builder()
                .id(1L)
                .market(targetCandle.getMarket())
                .side(ASK)
                .price(targetCandle.getTradePrice())
                .volume(volume)
                .fee(fee)
                .proceeds(proceeds)
                .proceedRate(proceedRate)
                .maxProceedRate(maxProceedRate)
                .timestamp(targetCandle.getTimestamp())
                .build();
    }

}
