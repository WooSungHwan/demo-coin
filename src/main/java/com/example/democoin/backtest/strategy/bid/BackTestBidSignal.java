package com.example.democoin.backtest.strategy.bid;

import com.example.democoin.indicator.result.BollingerBands;
import com.example.democoin.utils.IndicatorUtil;
import com.example.democoin.indicator.result.RSIs;
import com.example.democoin.upbit.db.entity.FiveMinutesCandle;
import com.example.democoin.utils.NumberUtils;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.democoin.backtest.strategy.bid.BidReason.*;

@Slf4j
public class BackTestBidSignal {

    public static BidReason strategy_1(RSIs rsi14, BollingerBands bollingerBands, FiveMinutesCandle targetCandle) {
        if (rsi14.isUnder(35) && bollingerBands.isBollingerBandLddTouch(targetCandle)) {
            log.info("{} 해당 코인 매수 신호 발생, KST 캔들 시각 : {}, rsi : {}, 볼밴하단 : {}",
                    targetCandle.getMarket(), targetCandle.getCandleDateTimeKst(), rsi14.getRsi().get(0), bollingerBands.getLdd().get(0));
            return RSI_UNDER_AND_BB_LDD_TOUCH;
        }
        return NO_BID;
    }

    public static BidReason strategy_2(BollingerBands bollingerBands, FiveMinutesCandle targetCandle) {
        if (bollingerBands.isReduceFiveCandle()) {
            log.info("{} 해당 코인 매수 신호 발생, KST 캔들 시각 : {}",
                    targetCandle.getMarket(), targetCandle.getCandleDateTimeKst());
            return BB_REDUCE_FIVE_CANDLES;
        }
        return NO_BID;
    }

    // 단순 음봉 3개 발생시 매수
    public static BidReason strategy_3(List<FiveMinutesCandle> candles, FiveMinutesCandle targetCandle) {
        if (candles.stream().limit(3).allMatch(FiveMinutesCandle::isNegative)) {
            log.info("{} 해당 코인 매수 신호 발생, KST 캔들 시각 : {}, 5분봉 음봉 3개 발생!!",
                    targetCandle.getMarket(), targetCandle.getCandleDateTimeKst());
            return THREE_NEGATIVE_CANDLE_APPEAR;
        }
        return NO_BID;
    }

    /**
     * 볼린저밴드 간격 5봉 연속 줄어들고 캔들 종가가 20이평보다 클때
     *
     * @param bollingerBands
     * @param targetCandle
     * @return
     */
    public static BidReason strategy_4(BollingerBands bollingerBands, FiveMinutesCandle targetCandle) {
        if (bollingerBands.isReduceFiveCandle() && targetCandle.getTradePrice() >= bollingerBands.getMdd().get(0).doubleValue()) {
            log.info("{} 해당 코인 매수 신호 발생, KST 캔들 시각 : {}",
                    targetCandle.getMarket(), targetCandle.getCandleDateTimeKst());
            return BB_REDUCE_FIVE_CANDLES_AND_OVER_SMA20;
        }
        return NO_BID;
    }

    public static BidReason strategy_5(RSIs rsi14, List<FiveMinutesCandle> candles, FiveMinutesCandle targetCandle) {
        // 최근 0.1 퍼센트 이하의 음봉 갯수
        long count = candles.stream().limit(10).filter(candle -> candle.getCandlePercent() < -0.1).count();

        if (rsi14.isUnder(35) && count >= 3) {
            log.info("{} 해당 코인 매수 신호 발생, KST 캔들 시각 : {}, rsi : {}, 음봉갯수 : {}",
                    targetCandle.getMarket(), targetCandle.getCandleDateTimeKst(), rsi14.getRsi().get(0), count);
            return RSI_UNDER_AND_RECENT_NEGATIVE_CANDLE_COUNT_OVER_THREE;
        }
        return NO_BID;
    }

    /**
     * 5이평 10이평 골든크로스
     *
     * @param candles
     * @return
     */
    public static BidReason strategy_6(BollingerBands bollingerBands, List<FiveMinutesCandle> candles, FiveMinutesCandle targetCandle) {
        List<Double> prices = candles.stream().limit(11).map(FiveMinutesCandle::getTradePrice).toList();
        BigDecimal before_price_5 = IndicatorUtil.getSMAList(5, prices.subList(0, 5)).get(0);
        BigDecimal after_price_5 = IndicatorUtil.getSMAList(5, prices.subList(1, 6)).get(0);

        BigDecimal before_price_10 = IndicatorUtil.getSMAList(10, prices.subList(0, 10)).get(0);
        BigDecimal after_price_10 = IndicatorUtil.getSMAList(10, prices.subList(1, 11)).get(0);

        if (before_price_10.doubleValue() > before_price_5.doubleValue() &&
                after_price_10.doubleValue() < after_price_5.doubleValue() &&
                candles.get(0).getTradePrice() >= bollingerBands.getMdd().get(0).doubleValue()) {
            log.info("{} 해당 코인 매수 신호 발생, KST 캔들 시각 : {}, 5 이평 / 10 이평 골든크로스 발생",
                    candles.get(0).getMarket(), candles.get(0).getCandleDateTimeKst());
            return SMA5_SMA10_GOLDEN_CROSS;
        }
        return NO_BID;
    }

    /**
     * rsi14 30 이하 / 볼린저 밴드 7개봉 수축 / 20 이평 이상
     *
     * @param bollingerBands
     * @param rsi14
     * @param candles
     * @return
     */
    public static BidReason strategy_7(BollingerBands bollingerBands, RSIs rsi14, List<FiveMinutesCandle> candles, FiveMinutesCandle targetCandle) {
        if (rsi14.isUnder(31)) {
            log.info("{} 해당 코인 매수 신호 발생, KST 캔들 시각 : {}, rsi 30 이하 포착!! rsi : {}",
                    candles.get(0).getMarket(), candles.get(0).getCandleDateTimeKst(), rsi14.getRsi().get(0));
            return RSI_UNDER;
        }

        if (bollingerBands.isReduceSevenCandle() && candles.get(0).getTradePrice() >= bollingerBands.getMdd().get(0).doubleValue()) {
            log.info("{} 해당 코인 매수 신호 발생, KST 캔들 시각 : {}, 20이평 이상, 볼린저밴드 7개봉 수축 발생!!",
                    candles.get(0).getMarket(), candles.get(0).getCandleDateTimeKst());
            return BB_REDUCE_SEVEN_CANDLES_AND_OVER_SMA20;
        }

        return NO_BID;
    }

    // 볼린저밴드 하단선 상향돌파 / 200 이평선 돌파 또는 rsi 30 상향 돌파
    public static BidReason strategy_8(BollingerBands bollingerBands,
                                       RSIs rsi14,
                                       List<FiveMinutesCandle> candles,
                                       FiveMinutesCandle targetCandle) {
        List<BigDecimal> sma200 = IndicatorUtil.getSMAList(199, candles.stream().map(FiveMinutesCandle::getTradePrice).toList());

        BidReason bidReason = bbAndsma200Overing(sma200, candles, bollingerBands);
        if (bidReason.isBid()) return bidReason;

        // rsi14 30 상향돌파
        if (rsi14.isOvering(30)) {
            log.info("{} 해당 코인 매수 신호 발생, KST 캔들 시각 : {}, rsi 30 상향돌파 포착!! rsi : {}",
                    candles.get(0).getMarket(), candles.get(0).getCandleDateTimeKst(), rsi14.getRsi().get(0));
            return RSI_OVERING;
        }

        return NO_BID;
    }

    public static BidReason strategy_9(BollingerBands bollingerBands,
                                       List<FiveMinutesCandle> candles) {
        List<BigDecimal> sma200 = IndicatorUtil.getSMAList(199, candles.stream().map(FiveMinutesCandle::getTradePrice).toList());
        BidReason bidReason = bbAndsma200Overing(sma200, candles, bollingerBands);
        if (bidReason.isBid()) return bidReason;

        return NO_BID;

    }

    // 볼린저밴드 7개봉 수축 / 200 이평 이상 or 볼린저밴드 하단선 상향돌파 / 200 이평선 돌파
    public static BidReason strategy_10(BollingerBands bollingerBands, List<FiveMinutesCandle> candles) {
        List<BigDecimal> sma200 = IndicatorUtil.getSMAList(199, candles.stream().map(FiveMinutesCandle::getTradePrice).toList());

        // 볼린저밴드 하단선 상향돌파 / 200 이평선 돌파
        BidReason bidReason = bbAndsma200Overing(sma200, candles, bollingerBands);
        if (bidReason.isBid()) return bidReason;

        // 볼린저밴드 7개봉 수축 / 200 이평 이상
        if (bollingerBands.isReduceSevenCandle() && sma200.get(0).doubleValue() <= candles.get(0).getTradePrice()) {
            log.info("{} 해당 코인 매수 신호 발생, KST 캔들 시각 : {}, 볼밴 7개봉 수축, 200 이평 이상!!",
                    candles.get(0).getMarket(), candles.get(0).getCandleDateTimeKst());
            return BB_REDUCE_SEVEN_CANDLES_AND_OVER_SMA200;
        }
        return NO_BID;
    }

    // 5분봉 3틱 하락, rsi 50 이하
    public static BidReason strategy_11(RSIs rsi14, List<FiveMinutesCandle> candles) {
        if (candles.stream().limit(3).allMatch(FiveMinutesCandle::isNegative) && rsi14.isUnder(50)) {
            log.info("{} 해당 코인 매수 신호 발생, KST 캔들 시각 : {}, rsi 50 이하, 5분봉 음봉 3개 발생!!",
                    candles.get(0).getMarket(), candles.get(0).getCandleDateTimeKst());
            return THREE_NEGATIVE_CANDLE_APPEAR_AND_RSI_UNDER;
        }
        return NO_BID;
    }

    // 5분봉 3틱 하락, rsi 40 이하, 볼린저 밴드 하단선 아래
    public static BidReason strategy_12(RSIs rsi14, BollingerBands bollingerBands, List<FiveMinutesCandle> candles) {
        if (candles.stream().limit(3).allMatch(FiveMinutesCandle::isNegative) &&
                rsi14.isUnder(40) &&
                bollingerBands.isBollingerBandLddTouch(candles.get(0))) {

            return THREE_NEGATIVE_CANDLE_APPEAR_AND_RSI_UNDER_AND_BB_LDD_TOUCH;
        }
        return NO_BID;
    }

    /**
     * 5분봉 3틱 하락(개선1), rsi 30 이상 50 이하, 볼린저 밴드 하단선 아래
     *
     * @param rsi14
     * @param bollingerBands
     * @param candles
     * @return
     */
    public static BidReason strategy_13(RSIs rsi14, BollingerBands bollingerBands, List<FiveMinutesCandle> candles) {
        boolean fiveThreeTick = candles.stream().limit(3).allMatch(FiveMinutesCandle::isNegative);
        if (!fiveThreeTick) {
            return NO_BID;
        }
        int tick = countTickProcess1(candles);

        if (tick == 3 &&
                rsi14.isUnder(50) &&
                rsi14.isOver(30) &&
                bollingerBands.isBollingerBandLddTouch(candles.get(0))) {

            return THREE_NEGATIVE_CANDLE_APPEAR_AND_RSI_UNDER_AND_BB_LDD_TOUCH;
        }

        return NO_BID;
    }

    private static int countTickProcess1(List<FiveMinutesCandle> candles) {
        int tick = 0;
        for (int i = 0; i < 3; i++) {
            FiveMinutesCandle now = candles.get(i);
            FiveMinutesCandle before = candles.get(i + 1);
            if (before.getLowPrice() > now.getMedian()) {
                tick++;
            }
        }
        return tick;
    }

    // 5분봉 3틱 하락(개선2), 볼린저 밴드 하단선 아래, 15분봉 rsi 40 이하
    public static BidReason strategy_14(BollingerBands bollingerBands,
                                        List<FiveMinutesCandle> candles,
                                        RSIs fifRsi14) {
        Integer tick = countTickProcess2(candles);
        if (tick == null) {
            return NO_BID;
        }

        if (fifRsi14.isUnder(40) &&
                bollingerBands.isBollingerBandLddTouch(candles.get(0)) &&
                tick >= 3) {
            return FIVE_THREE_TICK_BB_LDD_TOUCH_FIF_RSI_UNDER;
        }

        return NO_BID;
    }

    private static Integer countTickProcess2(List<FiveMinutesCandle> candles) {
        int tick = 0;
        for (int i = 0; i < candles.size(); i++) {
            FiveMinutesCandle now = candles.get(i);
            FiveMinutesCandle before = candles.get(i + 1);

            if (now.isPositive()) {
                if (before.isPositive()) {
                    return 0;
                } else {
                    if (now.isBetween(before)) {
                        continue;
                    }
                    return 0;
                }
            } else {
                if (before.isPositive()) {
                    if (before.getTradePrice() > now.getTradePrice()) {
                        continue;
                    } else {
                        return 0;
                    }
                } else {
                    tick++;
                }
            }
            if (tick >= 3) {
                break;
            }
        }
        return tick;
    }

    public static BidReason strategy_15(List<FiveMinutesCandle> candles) {
        boolean fiveThreeTick = candles.stream().limit(3).allMatch(FiveMinutesCandle::isNegative);
        if (!fiveThreeTick) {
            return NO_BID;
        }
        int tick = countTickProcess1(candles);

        if (tick == 3) {
            return THREE_NEGATIVE_CANDLE_APPEAR_REPAIR_1;
        }

        return NO_BID;
    }

    public static BidReason strategy_16(List<FiveMinutesCandle> candles) {
        Integer tick = countTickProcess2(candles);

        if (tick >= 3) {
            return THREE_NEGATIVE_CANDLE_APPEAR_REPAIR_2;
        }

        return NO_BID;
    }

    public static BidReason strategy_17(List<FiveMinutesCandle> candles, RSIs rsIs) {
        Integer tick = countTickProcess2(candles);

        if (tick >= 3 && rsIs.isOver(30)) {
            return THREE_NEGATIVE_CANDLE_APPEAR_REPAIR_2_AND_RSI_OVER_30;
        }

        return NO_BID;
    }

    private static Integer countTickProcess3(List<FiveMinutesCandle> candles) {
        return null;
    }

    private static BidReason bbAndsma200Overing(List<BigDecimal> sma200, List<FiveMinutesCandle> candles, BollingerBands bollingerBands) {
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
            return SMA200_OVERING_AND_BB_LDD_OVERING;
        }
        return NO_BID;
    }
}

