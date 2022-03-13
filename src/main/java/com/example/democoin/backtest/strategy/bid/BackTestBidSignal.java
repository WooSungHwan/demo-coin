package com.example.democoin.backtest.strategy.bid;

import com.example.democoin.BollingerBands;
import com.example.democoin.Indicator;
import com.example.democoin.RSIs;
import com.example.democoin.upbit.db.entity.FiveMinutesCandle;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

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
        FiveMinutesCandle baseCandle0 = candles.get(0);
        FiveMinutesCandle baseCandle1 = candles.get(1);
        FiveMinutesCandle baseCandle2 = candles.get(2);
        FiveMinutesCandle baseCandle3 = candles.get(3);
        FiveMinutesCandle baseCandle4 = candles.get(4);
        FiveMinutesCandle baseCandle5 = candles.get(5);
        FiveMinutesCandle baseCandle6 = candles.get(6);
        FiveMinutesCandle baseCandle7 = candles.get(7);

        int tick = 0;
        for (int i = 0; i < 10; i++) {
            FiveMinutesCandle targetCandle = candles.get(i);
            FiveMinutesCandle beforeTargetCandle = candles.get(i + 1);

            if (!targetCandle.isPositive()) {
                // 현재 타겟 캔들이 음봉
            }
        }

        return false;
    }

    /**
     * 볼린저밴드 간격 5봉 연속 줄어들고 캔들 종가가 20이평보다 클때
     * @param bollingerBands
     * @param candle
     * @return
     */
    public static boolean strategy_4(BollingerBands bollingerBands, FiveMinutesCandle candle) {
        if (bollingerBands.isReduceFiveCandle() && candle.getTradePrice() >= bollingerBands.getMdd().get(0).doubleValue()) {
            log.info("{} 해당 코인 매수 신호 발생, KST 캔들 시각 : {}",
                    candle.getMarket(), candle.getCandleDateTimeKst());
            return true;
        }
        return false;
    }

    public static boolean strategy_5(RSIs rsi14, List<FiveMinutesCandle> candles) {
        // 최근 0.1 퍼센트 이하의 음봉 갯수
        long count = candles.stream().limit(10).filter(candle -> candle.getCandlePercent() < -0.1).count();

        if (rsi14.isUnder(35) && count >= 3) {
            log.info("{} 해당 코인 매수 신호 발생, KST 캔들 시각 : {}, rsi : {}, 음봉갯수 : {}",
                    candles.get(0).getMarket(), candles.get(0).getCandleDateTimeKst(), rsi14.getRsi().get(0), count);
            return true;
        }
        return false;
    }

    /**
     * 5이평 10이평 골든크로스
     * @param candles
     * @return
     */
    public static boolean strategy_6(BollingerBands bollingerBands, List<FiveMinutesCandle> candles) {
        List<Double> prices = candles.stream().limit(11).map(FiveMinutesCandle::getTradePrice).collect(Collectors.toList());
        BigDecimal before_price_5 = Indicator.getSMAList(5, prices.subList(0, 5)).get(0);
        BigDecimal after_price_5 = Indicator.getSMAList(5, prices.subList(1, 6)).get(0);

        BigDecimal before_price_10 = Indicator.getSMAList(10, prices.subList(0, 10)).get(0);
        BigDecimal after_price_10 = Indicator.getSMAList(10, prices.subList(1, 11)).get(0);

        if (before_price_10.doubleValue() > before_price_5.doubleValue() &&
                after_price_10.doubleValue() < after_price_5.doubleValue() &&
                candles.get(0).getTradePrice() >= bollingerBands.getMdd().get(0).doubleValue()) {
            log.info("{} 해당 코인 매수 신호 발생, KST 캔들 시각 : {}, 5 이평 / 10 이평 골든크로스 발생",
                    candles.get(0).getMarket(), candles.get(0).getCandleDateTimeKst());
            return true;
        }
        return false;
    }
}
