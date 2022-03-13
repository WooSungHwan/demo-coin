package com.example.democoin.backtest.strategy.ask;

import com.example.democoin.BollingerBands;
import com.example.democoin.Indicator;
import com.example.democoin.RSIs;
import com.example.democoin.backtest.common.AccountCoinWallet;
import com.example.democoin.upbit.db.entity.FiveMinutesCandle;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class BackTestAskSignal {

    /**
     * 볼린저 밴드 상단 터치 , rsi14 65 이상
     * @param rsi14
     * @param bollingerBands
     * @param candle
     * @return
     */
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
        return false;
    }

    public static boolean strategy_4(RSIs rsi14, BollingerBands bollingerBands, FiveMinutesCandle candle) {
        if (bollingerBands.isBollingerBandWidthMax() || rsi14.isOver(65)) {
            log.info("{} 해당 코인 매도 신호 발생, KST 캔들 시각 : {}",
                    candle.getMarket(), candle.getCandleDateTimeKst());
            return true;
        }
        return false;
    }

    public static boolean strategy_5(RSIs rsi14, FiveMinutesCandle candle) {
        if (rsi14.isOver(65)) {
            log.info("{} 해당 코인 매도 신호 발생, KST 캔들 시각 : {}",
                    candle.getMarket(), candle.getCandleDateTimeKst());
            return true;
        }
        return false;
    }

    public static boolean strategy_6(AccountCoinWallet wallet, BollingerBands bollingerBands, FiveMinutesCandle candle) {
        if (-2 < wallet.getProceedRate() && wallet.getProceedRate() <= 3) {
            return false;
        }
        // 수익률 5프로 넘었을 때
        if (wallet.isMaxProceedRateFall() || wallet.getProceedRate() < -2 || bollingerBands.isLddChangeUp()) {
            log.info("{} 해당 코인 매도 신호 발생, KST 캔들 시각 : {}",
                    candle.getMarket(), candle.getCandleDateTimeKst());
            return true;
        }
        return false;
    }

    /**
     * 5이평 10이평 데드크로스
     * @param candles
     * @return
     */
    public static boolean strategy_7(List<FiveMinutesCandle> candles) {
        List<Double> prices = candles.stream().limit(11).map(FiveMinutesCandle::getTradePrice).collect(Collectors.toList());
        BigDecimal before_price_5 = Indicator.getSMAList(5, prices.subList(0, 5)).get(0);
        BigDecimal after_price_5 = Indicator.getSMAList(5, prices.subList(1, 6)).get(0);

        BigDecimal before_price_10 = Indicator.getSMAList(10, prices.subList(0, 10)).get(0);
        BigDecimal after_price_10 = Indicator.getSMAList(10, prices.subList(1, 11)).get(0);

        if (before_price_10.doubleValue() < before_price_5.doubleValue() &&
                after_price_10.doubleValue() > after_price_5.doubleValue()) {
            log.info("{} 해당 코인 매수 신호 발생, KST 캔들 시각 : {}, 5 이평 / 10 이평 데드크로스 발생",
                    candles.get(0).getMarket(), candles.get(0).getCandleDateTimeKst());
            return true;
        }
        return false;
    }
}
