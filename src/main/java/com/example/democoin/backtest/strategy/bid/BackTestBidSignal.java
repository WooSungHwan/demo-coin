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

    // 단순 음봉 3개 발생시 매수
    public static boolean strategy_3(List<FiveMinutesCandle> candles, FiveMinutesCandle targetCandle) {
        if (candles.stream().limit(3).allMatch(FiveMinutesCandle::isNegative)) {
            log.info("{} 해당 코인 매수 신호 발생, KST 캔들 시각 : {}, 5분봉 음봉 3개 발생!!",
                    targetCandle.getMarket(), targetCandle.getCandleDateTimeKst());
            return true;
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

    // 볼린저밴드 하단선 상향돌파 / 200 이평선 돌파 또는 rsi 30 상향 돌파
    public static boolean strategy_8(BollingerBands bollingerBands,
                                     RSIs rsi14,
                                     List<FiveMinutesCandle> candles,
                                     FiveMinutesCandle targetCandle) {
        List<BigDecimal> sma200 = IndicatorUtil.getSMAList(199, candles.stream().map(FiveMinutesCandle::getTradePrice).collect(Collectors.toList()));

        if (bbAndsma200Overing(sma200, candles, bollingerBands)) return true;

        // rsi14 30 상향돌파
        if (rsi14.isOvering(30)) {
            log.info("{} 해당 코인 매수 신호 발생, KST 캔들 시각 : {}, rsi 30 상향돌파 포착!! rsi : {}",
                    candles.get(0).getMarket(), candles.get(0).getCandleDateTimeKst(), rsi14.getRsi().get(0));
            return true;
        }

        return false;
    }

    public static boolean strategy_9(BollingerBands bollingerBands,
                                     List<FiveMinutesCandle> candles) {
        List<BigDecimal> sma200 = IndicatorUtil.getSMAList(199, candles.stream().map(FiveMinutesCandle::getTradePrice).collect(Collectors.toList()));
        if (bbAndsma200Overing(sma200, candles, bollingerBands)) return true;

        return false;

    }

    // 볼린저밴드 7개봉 수축 / 200 이평 이상 or 볼린저밴드 하단선 상향돌파 / 200 이평선 돌파
    public static boolean strategy_10(BollingerBands bollingerBands, List<FiveMinutesCandle> candles) {
        List<BigDecimal> sma200 = IndicatorUtil.getSMAList(199, candles.stream().map(FiveMinutesCandle::getTradePrice).collect(Collectors.toList()));

        // 볼린저밴드 하단선 상향돌파 / 200 이평선 돌파
        if (bbAndsma200Overing(sma200, candles, bollingerBands)) return true;

        // 볼린저밴드 7개봉 수축 / 200 이평 이상
        if (bollingerBands.isReduceSevenCandle() && sma200.get(0).doubleValue() <= candles.get(0).getTradePrice()) {
            log.info("{} 해당 코인 매수 신호 발생, KST 캔들 시각 : {}, 볼밴 7개봉 수축, 200 이평 이상!!",
                    candles.get(0).getMarket(), candles.get(0).getCandleDateTimeKst());
            return true;
        }
        return false;
    }

    private static boolean bbAndsma200Overing(List<BigDecimal> sma200, List<FiveMinutesCandle> candles, BollingerBands bollingerBands) {
        double nowSma = sma200.get(0).doubleValue();
        double beforeSma = sma200.get(1).doubleValue();
        double nowPrice = candles.get(0).getTradePrice();
        double beforePrice = candles.get(1).getTradePrice();
        double beforeBB = bollingerBands.getLdd().get(1).doubleValue();
        double nowBB = bollingerBands.getLdd().get(0).doubleValue();

        // 이평선 200 상향돌파 && 볼린져 밴드 하단선 상향 돌파
        if (beforeSma > beforePrice && nowSma < nowPrice && beforeBB > beforePrice && nowBB < nowPrice) {
            log.info("{} 해당 코인 매수 신호 발생, KST 캔들 시각 : {}, 볼밴 하단선, 200이평선 상향돌파 포착!!",
                    candles.get(0).getMarket(), candles.get(0).getCandleDateTimeKst());
            return true;
        }
        return false;
    }
}
