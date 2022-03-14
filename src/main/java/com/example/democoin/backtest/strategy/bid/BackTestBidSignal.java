package com.example.democoin.backtest.strategy.bid;

import com.example.democoin.indicator.result.BollingerBands;
import com.example.democoin.utils.IndicatorUtil;
import com.example.democoin.indicator.result.RSIs;
import com.example.democoin.upbit.db.entity.FiveMinutesCandle;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class BackTestBidSignal {

    public static boolean strategy_1(RSIs rsi14, BollingerBands bollingerBands, FiveMinutesCandle targetCandle) {
        if (rsi14.isUnder(35) && bollingerBands.isBollingerBandLddTouch(targetCandle)) {
            log.info("{} 해당 코인 매수 신호 발생, KST 캔들 시각 : {}, rsi : {}, 볼밴하단 : {}",
                    targetCandle.getMarket(), targetCandle.getCandleDateTimeKst(), rsi14.getRsi().get(0), bollingerBands.getLdd().get(0));
            return true;
        }
        return false;
    }

    public static boolean strategy_2(BollingerBands bollingerBands, FiveMinutesCandle targetCandle) {
        if (bollingerBands.isReduceFiveCandle()) {
            log.info("{} 해당 코인 매수 신호 발생, KST 캔들 시각 : {}",
                    targetCandle.getMarket(), targetCandle.getCandleDateTimeKst());
            return true;
        }
        return false;
    }

    public static boolean strategy_3(List<FiveMinutesCandle> candles, FiveMinutesCandle targetCandle) {
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
            FiveMinutesCandle baseCandle = candles.get(i);
            FiveMinutesCandle beforeTargetCandle = candles.get(i + 1);

            if (!baseCandle.isPositive()) {
                // 현재 타겟 캔들이 음봉
            }
        }

        return false;
    }

    /**
     * 볼린저밴드 간격 5봉 연속 줄어들고 캔들 종가가 20이평보다 클때
     * @param bollingerBands
     * @param targetCandle
     * @return
     */
    public static boolean strategy_4(BollingerBands bollingerBands, FiveMinutesCandle targetCandle) {
        if (bollingerBands.isReduceFiveCandle() && targetCandle.getTradePrice() >= bollingerBands.getMdd().get(0).doubleValue()) {
            log.info("{} 해당 코인 매수 신호 발생, KST 캔들 시각 : {}",
                    targetCandle.getMarket(), targetCandle.getCandleDateTimeKst());
            return true;
        }
        return false;
    }

    public static boolean strategy_5(RSIs rsi14, List<FiveMinutesCandle> candles, FiveMinutesCandle targetCandle) {
        // 최근 0.1 퍼센트 이하의 음봉 갯수
        long count = candles.stream().limit(10).filter(candle -> candle.getCandlePercent() < -0.1).count();

        if (rsi14.isUnder(35) && count >= 3) {
            log.info("{} 해당 코인 매수 신호 발생, KST 캔들 시각 : {}, rsi : {}, 음봉갯수 : {}",
                    targetCandle.getMarket(), targetCandle.getCandleDateTimeKst(), rsi14.getRsi().get(0), count);
            return true;
        }
        return false;
    }

    /**
     * 5이평 10이평 골든크로스
     * @param candles
     * @return
     */
    public static boolean strategy_6(BollingerBands bollingerBands, List<FiveMinutesCandle> candles, FiveMinutesCandle targetCandle) {
        List<Double> prices = candles.stream().limit(11).map(FiveMinutesCandle::getTradePrice).collect(Collectors.toList());
        BigDecimal before_price_5 = IndicatorUtil.getSMAList(5, prices.subList(0, 5)).get(0);
        BigDecimal after_price_5 = IndicatorUtil.getSMAList(5, prices.subList(1, 6)).get(0);

        BigDecimal before_price_10 = IndicatorUtil.getSMAList(10, prices.subList(0, 10)).get(0);
        BigDecimal after_price_10 = IndicatorUtil.getSMAList(10, prices.subList(1, 11)).get(0);

        if (before_price_10.doubleValue() > before_price_5.doubleValue() &&
                after_price_10.doubleValue() < after_price_5.doubleValue() &&
                candles.get(0).getTradePrice() >= bollingerBands.getMdd().get(0).doubleValue()) {
            log.info("{} 해당 코인 매수 신호 발생, KST 캔들 시각 : {}, 5 이평 / 10 이평 골든크로스 발생",
                    candles.get(0).getMarket(), candles.get(0).getCandleDateTimeKst());
            return true;
        }
        return false;
    }

    /**
     * rsi14 30 이하 / 볼린저 밴드 7개봉 수축 / 20 이평 이상
     * @param bollingerBands
     * @param rsi14
     * @param candles
     * @return
     */
    public static boolean strategy_7(BollingerBands bollingerBands, RSIs rsi14, List<FiveMinutesCandle> candles, FiveMinutesCandle targetCandle) {
        if (rsi14.isUnder(31)) {
            log.info("{} 해당 코인 매수 신호 발생, KST 캔들 시각 : {}, rsi 30 이하 포착!! rsi : {}",
                    candles.get(0).getMarket(), candles.get(0).getCandleDateTimeKst(), rsi14.getRsi().get(0));
            return true;
        }

        if (bollingerBands.isReduceSevenCandle() && candles.get(0).getTradePrice() >= bollingerBands.getMdd().get(0).doubleValue()) {
            log.info("{} 해당 코인 매수 신호 발생, KST 캔들 시각 : {}, 20이평 이상, 볼린저밴드 7개봉 수축 발생!!",
                    candles.get(0).getMarket(), candles.get(0).getCandleDateTimeKst());
            return true;
        }

        return false;
    }
}
