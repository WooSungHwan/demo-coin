package com.example.democoin.backtest.strategy.bid;

import com.example.democoin.BollingerBands;
import com.example.democoin.RSIs;
import com.example.democoin.upbit.db.entity.FiveMinutesCandle;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class BackTestBidSignal {

    public static boolean strategy_1(RSIs rsi14, BollingerBands bollingerBands, FiveMinutesCandle candle) {
        if (rsi14.isUnder(35) && bollingerBands.isBollingerBandLddTouch(candle)) {
            log.info("{} 해당 코인 매수 신호 발생, KST 캔들 시각 : {}, rsi : {}, 볼밴하단 : {}",
                    candle.getMarket(), candle.getCandleDateTimeKst(), rsi14.getRsi().get(0), bollingerBands.getLdd().get(0));
            return true;
        }
        return false;
    }

    public static boolean strategy_2(BollingerBands bollingerBands, FiveMinutesCandle candle) {
        if (bollingerBands.isReduceFiveCandle()) {
            log.info("{} 해당 코인 매수 신호 발생, KST 캔들 시각 : {}",
                    candle.getMarket(), candle.getCandleDateTimeKst());
            return true;
        }
        return false;
    }

    public static boolean strategy_3(List<FiveMinutesCandle> candles) {
        return false;
    }

    public static boolean strategy_4(BollingerBands bollingerBands, FiveMinutesCandle candle) {
        if (bollingerBands.isReduceFiveCandle() && candle.getTradePrice() >= bollingerBands.getMdd().get(0).doubleValue()) {
            log.info("{} 해당 코인 매수 신호 발생, KST 캔들 시각 : {}",
                    candle.getMarket(), candle.getCandleDateTimeKst());
            return true;
        }
        return false;
    }
}
