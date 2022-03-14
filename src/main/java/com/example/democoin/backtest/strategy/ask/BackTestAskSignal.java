package com.example.democoin.backtest.strategy.ask;

import com.example.democoin.indicator.result.BollingerBands;
import com.example.democoin.utils.IndicatorUtil;
import com.example.democoin.indicator.result.RSIs;
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
    public static boolean strategy_1(AccountCoinWallet wallet, RSIs rsi14, BollingerBands bollingerBands, FiveMinutesCandle candle, FiveMinutesCandle targetCandle) {
        if (isWalletPercent(wallet, targetCandle)) return true;

        if (rsi14.isOver(65) && bollingerBands.isBollingerBandUddTouch(candle)) {
            log.info("{} 해당 코인 매도 신호 발생, KST 캔들 시각 : {}, rsi : {}, 볼밴 상단 : {}",
                    targetCandle.getMarket(), targetCandle.getCandleDateTimeKst(), rsi14.getRsi().get(0), bollingerBands.getLdd().get(0));
            return true;
        }
        return false;
    }

    public static boolean strategy_2(AccountCoinWallet wallet, BollingerBands bollingerBands, FiveMinutesCandle targetCandle) {
        if (isWalletPercent(wallet, targetCandle)) return true;

        if (bollingerBands.isBollingerBandWidthMax()) {
            log.info("{} 해당 코인 매도 신호 발생, KST 캔들 시각 : {}",
                    targetCandle.getMarket(), targetCandle.getCandleDateTimeKst());
            return true;
        }
        return false;
    }

    public static boolean strategy_3(AccountCoinWallet wallet, List<FiveMinutesCandle> candles) {
        return false;
    }

    public static boolean strategy_4(AccountCoinWallet wallet, RSIs rsi14, BollingerBands bollingerBands, FiveMinutesCandle targetCandle) {
        if (isWalletPercent(wallet, targetCandle)) return true;

        if (bollingerBands.isBollingerBandWidthMax() || rsi14.isOver(65)) {
            log.info("{} 해당 코인 매도 신호 발생, KST 캔들 시각 : {}",
                    targetCandle.getMarket(), targetCandle.getCandleDateTimeKst());
            return true;
        }
        return false;
    }

    public static boolean strategy_5(AccountCoinWallet wallet, RSIs rsi14, FiveMinutesCandle targetCandle) {
        if (isWalletPercent(wallet, targetCandle)) return true;

        if (rsi14.isOver(65)) {
            log.info("{} 해당 코인 매도 신호 발생, KST 캔들 시각 : {}",
                    targetCandle.getMarket(), targetCandle.getCandleDateTimeKst());
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
    public static boolean strategy_7(List<FiveMinutesCandle> candles, FiveMinutesCandle targetCandle) {
        List<Double> prices = candles.stream().limit(11).map(FiveMinutesCandle::getTradePrice).collect(Collectors.toList());
        BigDecimal before_price_5 = IndicatorUtil.getSMAList(5, prices.subList(0, 5)).get(0);
        BigDecimal after_price_5 = IndicatorUtil.getSMAList(5, prices.subList(1, 6)).get(0);

        BigDecimal before_price_10 = IndicatorUtil.getSMAList(10, prices.subList(0, 10)).get(0);
        BigDecimal after_price_10 = IndicatorUtil.getSMAList(10, prices.subList(1, 11)).get(0);

        if (before_price_10.doubleValue() < before_price_5.doubleValue() &&
                after_price_10.doubleValue() > after_price_5.doubleValue()) {
            log.info("{} 해당 코인 매도 신호 발생, KST 캔들 시각 : {}, 5 이평 / 10 이평 데드크로스 발생",
                    targetCandle.getMarket(), targetCandle.getCandleDateTimeKst());
            return true;
        }
        return false;
    }

    /**
     * 볼린저밴드 하단선 확장 감소 추세 또는 rsi 70 이상
     * @param wallet
     * @param bollingerBands
     * @param rsi14
     * @return
     */
    public static boolean strategy_8(AccountCoinWallet wallet, BollingerBands bollingerBands, RSIs rsi14, FiveMinutesCandle targetCandle) {
        if (isWalletPercent(wallet, targetCandle)) return true;

        if (bollingerBands.isLddChangeUp()) {
            log.info("{} 해당 코인 매도 신호 발생, KST 캔들 시각 : {} / 볼린저밴드 하단선 확장 감소세 포착!!",
                    wallet.getMarket(), targetCandle.getCandleDateTimeKst());
            return true;
        }

        if (rsi14.isOver(70)) {
            log.info("{} 해당 코인 매도 신호 발생, KST 캔들 시각 : {}, rsi 70 이상 포착!! rsi : {}",
                    wallet.getMarket(), targetCandle.getCandleDateTimeKst(), rsi14.getRsi().get(0));
            return true;
        }
        return false;
    }

    private static boolean isWalletPercent(AccountCoinWallet wallet, FiveMinutesCandle targetCandle) {
        if (wallet.isMaxProceedRateFall()) {
            log.info("{} 해당 코인 매도 신호 발생, KST 캔들 시각 : {}, 최대 수익률 대비 하락 매도!! 수익률 : {}",
                    targetCandle.getMarket(), targetCandle.getCandleDateTimeKst(), wallet.getProceedRate());
            return true;
        }

        if (wallet.getProceedRate() < -2) {
            log.info("{} 해당 코인 매도 신호 발생, KST 캔들 시각 : {}, -2% 손절매!! 수익률 : {}",
                    targetCandle.getMarket(), targetCandle.getCandleDateTimeKst(), wallet.getProceedRate());
            return true;
        }
        return false;
    }
}
