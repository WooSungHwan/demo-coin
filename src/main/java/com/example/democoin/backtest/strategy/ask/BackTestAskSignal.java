package com.example.democoin.backtest.strategy.ask;

import com.example.democoin.backtest.BackTest2;
import com.example.democoin.indicator.result.BollingerBands;
import com.example.democoin.utils.IndicatorUtil;
import com.example.democoin.indicator.result.RSIs;
import com.example.democoin.backtest.entity.AccountCoinWallet;
import com.example.democoin.upbit.db.entity.FiveMinutesCandle;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.democoin.backtest.strategy.ask.AskReason.*;
import static com.example.democoin.utils.IndicatorUtil.fee;

@Slf4j
public class BackTestAskSignal {

    /**
     * 볼린저 밴드 상단 터치 , rsi14 65 이상
     * @param rsi14
     * @param bollingerBands
     * @return
     */
    public static AskReason strategy_1(AccountCoinWallet wallet, RSIs rsi14, BollingerBands bollingerBands, FiveMinutesCandle targetCandle) {
        AskReason walletPercentReason = isWalletPercent(wallet, targetCandle);
        if (walletPercentReason != NO_ASK) return walletPercentReason;

        if (rsi14.isOver(65) && bollingerBands.isBollingerBandUddTouch(targetCandle)) {
            log.info("{} 해당 코인 매도 신호 발생, KST 캔들 시각 : {}, rsi : {}, 볼밴 상단 : {}",
                    targetCandle.getMarket(), targetCandle.getCandleDateTimeKst(), rsi14.getRsi().get(0), bollingerBands.getLdd().get(0));
            return RSI_OVER_AND_BB_UDD_TOUCH;
        }
        return NO_ASK;
    }

    public static AskReason strategy_2(AccountCoinWallet wallet, BollingerBands bollingerBands, FiveMinutesCandle targetCandle) {
        AskReason walletPercentReason = isWalletPercent(wallet, targetCandle);
        if (walletPercentReason != NO_ASK) return walletPercentReason;

        if (bollingerBands.isBollingerBandWidthMax()) {
            log.info("{} 해당 코인 매도 신호 발생, KST 캔들 시각 : {}",
                    targetCandle.getMarket(), targetCandle.getCandleDateTimeKst());
            return BB_WIDTH_MAX;
        }
        return NO_ASK;
    }

    public static AskReason strategy_3(AccountCoinWallet wallet, List<FiveMinutesCandle> candles) {
        AskReason walletPercentReason = isWalletPercent(wallet, candles.get(0));
        if (walletPercentReason != NO_ASK) return walletPercentReason;
        return NO_ASK;
    }

    public static AskReason strategy_4(AccountCoinWallet wallet, RSIs rsi14, BollingerBands bollingerBands, FiveMinutesCandle targetCandle) {
        AskReason walletPercentReason = isWalletPercent(wallet, targetCandle);
        if (walletPercentReason != NO_ASK) return walletPercentReason;

        if (bollingerBands.isBollingerBandWidthMax()) {
            log.info("{} 해당 코인 매도 신호 발생, KST 캔들 시각 : {}",
                    targetCandle.getMarket(), targetCandle.getCandleDateTimeKst());
            return BB_WIDTH_MAX;
        }

        if (rsi14.isOver(65)) {
            log.info("{} 해당 코인 매도 신호 발생, KST 캔들 시각 : {}",
                    targetCandle.getMarket(), targetCandle.getCandleDateTimeKst());
            return RSI_OVER;
        }
        return NO_ASK;
    }

    public static AskReason strategy_5(AccountCoinWallet wallet, RSIs rsi14, FiveMinutesCandle targetCandle) {
        AskReason walletPercentReason = isWalletPercent(wallet, targetCandle);
        if (walletPercentReason != NO_ASK) return walletPercentReason;

        if (rsi14.isOver(65)) {
            log.info("{} 해당 코인 매도 신호 발생, KST 캔들 시각 : {}",
                    targetCandle.getMarket(), targetCandle.getCandleDateTimeKst());
            return RSI_OVER;
        }
        return NO_ASK;
    }

    public static AskReason strategy_6(AccountCoinWallet wallet, BollingerBands bollingerBands, FiveMinutesCandle candle) {
        if (-2 < wallet.getProceedRate() && wallet.getProceedRate() <= 3) {
            return NO_ASK;
        }

        AskReason walletPercentReason = isWalletPercent(wallet, candle);
        if (walletPercentReason != NO_ASK) return walletPercentReason;

        if (bollingerBands.isLddChangeUp()) {
            log.info("{} 해당 코인 매도 신호 발생, KST 캔들 시각 : {}",
                    candle.getMarket(), candle.getCandleDateTimeKst());
            return LDD_CHANGE_UP;
        }
        return NO_ASK;
    }

    /**
     * 5이평 10이평 데드크로스
     * @param candles
     * @return
     */
    public static AskReason strategy_7(List<FiveMinutesCandle> candles, FiveMinutesCandle targetCandle) {
        List<Double> prices = candles.stream().limit(11).map(FiveMinutesCandle::getTradePrice).toList();
        BigDecimal before_price_5 = IndicatorUtil.getSMAList(5, prices.subList(0, 5)).get(0);
        BigDecimal after_price_5 = IndicatorUtil.getSMAList(5, prices.subList(1, 6)).get(0);

        BigDecimal before_price_10 = IndicatorUtil.getSMAList(10, prices.subList(0, 10)).get(0);
        BigDecimal after_price_10 = IndicatorUtil.getSMAList(10, prices.subList(1, 11)).get(0);

        if (before_price_10.doubleValue() < before_price_5.doubleValue() &&
                after_price_10.doubleValue() > after_price_5.doubleValue()) {
            log.info("{} 해당 코인 매도 신호 발생, KST 캔들 시각 : {}, 5 이평 / 10 이평 데드크로스 발생",
                    targetCandle.getMarket(), targetCandle.getCandleDateTimeKst());
            return SMA5_SMA10_DEAD_CROSS;
        }
        return NO_ASK;
    }

    /**
     * 볼린저밴드 하단선 확장 감소 추세 또는 rsi 70 이상
     * @param wallet
     * @param bollingerBands
     * @param rsi14
     * @return
     */
    public static AskReason strategy_8(AccountCoinWallet wallet, BollingerBands bollingerBands, RSIs rsi14, FiveMinutesCandle targetCandle) {
        AskReason walletPercentReason = isWalletPercent(wallet, targetCandle);
        if (walletPercentReason != NO_ASK) return walletPercentReason;

        if (bollingerBands.isLddChangeUp()) {
            log.info("{} 해당 코인 매도 신호 발생, KST 캔들 시각 : {} / 볼린저밴드 하단선 확장 감소세 포착!!",
                    wallet.getMarket(), targetCandle.getCandleDateTimeKst());
            return LDD_CHANGE_UP;
        }

        if (rsi14.isOver(70)) {
            log.info("{} 해당 코인 매도 신호 발생, KST 캔들 시각 : {}, rsi 70 이상 포착!! rsi : {}",
                    wallet.getMarket(), targetCandle.getCandleDateTimeKst(), rsi14.getRsi().get(0));
            return RSI_OVER;
        }
        return NO_ASK;
    }

    private static AskReason isWalletPercent(AccountCoinWallet wallet, FiveMinutesCandle targetCandle) {
        // 수수료 보다 작을시 안판다.
        if (wallet.getProceeds() > 0 && wallet.getProceeds() < fee(wallet.getValAmount())) {
            return NO_ASK;
        }

        if (wallet.isMaxProceedRateFall()) {
            log.info("{} 해당 코인 매도 신호 발생, KST 캔들 시각 : {}, 최대 수익률 대비 하락 매도!! 수익률 : {}",
                    targetCandle.getMarket(), targetCandle.getCandleDateTimeKst(), wallet.getProceedRate());
            return MAX_PROCEED_RATE_FALL;
        }

        if (wallet.getProceedRate() < BackTest2.STOP_LOSS) {
            log.info("{} 해당 코인 매도 신호 발생, KST 캔들 시각 : {}, -2% 손절매!! 수익률 : {}",
                    targetCandle.getMarket(), targetCandle.getCandleDateTimeKst(), wallet.getProceedRate());
            return STOP_LOSS;
        }
        return NO_ASK;
    }

    // 볼린저 밴드 상한선 하향돌파 또는 rsi 70 하향 돌파
    public static AskReason strategy_9(AccountCoinWallet wallet,
                                     BollingerBands bollingerBands,
                                     RSIs rsi14,
                                     List<FiveMinutesCandle> candles,
                                     FiveMinutesCandle targetCandle) {
        AskReason walletPercentReason = isWalletPercent(wallet, targetCandle);
        if (walletPercentReason != NO_ASK) return walletPercentReason;

        double beforeBB = bollingerBands.getUdd().get(1).doubleValue();
        double nowBB = bollingerBands.getUdd().get(0).doubleValue();
        double beforePrice = candles.get(1).getTradePrice();
        double nowPrice = candles.get(0).getTradePrice();

        // 볼린저 밴드 상한선 하향 돌파
        if (beforeBB <= beforePrice && nowBB > nowPrice) {
            log.info("{} 해당 코인 매도 신호 발생, KST 캔들 시각 : {}, 볼밴 상단선 하향돌파 포착!!",
                    candles.get(0).getMarket(), candles.get(0).getCandleDateTimeKst());
            return BB_UDD_UNDERING;
        }

        // rsi 70 하향돌파 매도
        if (rsi14.isUndering(70)) {
            log.info("{} 해당 코인 매도 신호 발생, KST 캔들 시각 : {}, rsi 70 상향돌파 포착!! 이전 rsi : {} / 이후 rsi : {}",
                    candles.get(0).getMarket(), candles.get(0).getCandleDateTimeKst(), rsi14.getRsi().get(1), rsi14.getRsi().get(0));
            return RSI_UNDERING;
        }

        return NO_ASK;
    }

    // rsi 50 이상
    public static AskReason strategy_10(AccountCoinWallet wallet,
                                        RSIs rsi14,
                                        FiveMinutesCandle targetCandle) {
        AskReason walletPercentReason = isWalletPercent(wallet, targetCandle);
        if (walletPercentReason != NO_ASK) return walletPercentReason;

        if (wallet.getProceedRate() > 2 && rsi14.isOver(50)) {
            log.info("{} 해당 코인 매도 신호 발생, KST 캔들 시각 : {}, rsi 65 이상 출현!!",
                    targetCandle.getMarket(), targetCandle.getCandleDateTimeKst());
            return RSI_OVER;
        }

        return NO_ASK;
    }

    // 최대 수익률 부근에서 1시간 이상 딜레이
    public static AskReason strategy_11(AccountCoinWallet wallet,
                                        List<FiveMinutesCandle> candles,
                                        FiveMinutesCandle targetCandle) {
        AskReason walletPercentReason = isWalletPercent(wallet, candles.get(0));
        if (walletPercentReason != NO_ASK) return walletPercentReason;

        if (wallet.getBidTime().plusHours(1).isBefore(targetCandle.getCandleDateTimeUtc())
            && wallet.getProceeds() >= 0
            && wallet.getMaxProceedRate() - wallet.getProceedRate() >= 0) {
            return MAX_RATE_DELAY;
        }

        return NO_ASK;
    }
}
