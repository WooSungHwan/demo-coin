package com.example.democoin.backtest.strategy.ask;

import com.example.democoin.BollingerBands;
import com.example.democoin.RSIs;
import com.example.democoin.upbit.db.entity.FiveMinutesCandle;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class BackTestAskSignal {

    public static boolean strategy_1(RSIs rsi14, BollingerBands bollingerBands, FiveMinutesCandle candle) {
        if (rsi14.isOver(65) && bollingerBands.isBollingerBandUddTouch(candle)) {
            log.info("{} 해당 코인 매도 신호 발생, KST 캔들 시각 : {}, rsi : {}, 볼밴 상단 : {}",
                    candle.getMarket(), candle.getCandleDateTimeKst(), rsi14.getRsi().get(0), bollingerBands.getLdd().get(0));
            return true;
        }
        return false;
    }

    public static boolean strategy_2(BollingerBands bollingerBands, FiveMinutesCandle candle) {
        if (bollingerBands.isBollingerBandWidthMax()) {
            log.info("{} 해당 코인 매도 신호 발생, KST 캔들 시각 : {}",
                    candle.getMarket(), candle.getCandleDateTimeKst());
            return true;
        }
        return false;
    }

    public static boolean strategy_3(List<FiveMinutesCandle> candles) {
        if (candles.size() < 10) {
            return false;
        }
        int tick = 0;

        return false;
    }

    public static boolean strategy_4(RSIs rsi14, BollingerBands bollingerBands, FiveMinutesCandle candle) {
        if (bollingerBands.isBollingerBandWidthMax() && rsi14.isOver(70)) {
            log.info("{} 해당 코인 매도 신호 발생, KST 캔들 시각 : {}",
                    candle.getMarket(), candle.getCandleDateTimeKst());
            return true;
        }
        return false;
    }
}
