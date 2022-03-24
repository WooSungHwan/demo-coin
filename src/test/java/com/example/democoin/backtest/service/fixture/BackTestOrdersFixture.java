package com.example.democoin.backtest.service.fixture;

import com.example.democoin.backtest.entity.BackTestOrders;
import com.example.democoin.backtest.strategy.ask.AskReason;
import com.example.democoin.backtest.strategy.bid.BidReason;
import com.example.democoin.upbit.db.entity.FiveMinutesCandle;
import com.example.democoin.upbit.enums.MarketType;
import com.example.democoin.upbit.enums.OrdSideType;

import java.time.LocalDateTime;

import static com.example.democoin.backtest.strategy.ask.AskReason.BB_UDD_UNDERING;
import static com.example.democoin.backtest.strategy.bid.BidReason.RSI_OVERING;
import static com.example.democoin.upbit.enums.OrdSideType.ASK;
import static com.example.democoin.upbit.enums.OrdSideType.BID;

public class BackTestOrdersFixture {

    public static BackTestOrders standardBackTestOrders(OrdSideType ordSideType) {
        return BackTestOrders.builder()
                .id(1L)
                .market(MarketType.KRW_BTC)
                .side(ordSideType)
                .reason(ordSideType == ASK ? BB_UDD_UNDERING.getType() : RSI_OVERING.getType())
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
                .reason(RSI_OVERING.getType())
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
                .reason(BB_UDD_UNDERING.getType())
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
