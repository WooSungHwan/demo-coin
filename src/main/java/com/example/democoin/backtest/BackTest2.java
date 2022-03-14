package com.example.democoin.backtest;

import com.example.democoin.backtest.service.AccountCoinWalletService;
import com.example.democoin.indicator.result.BollingerBands;
import com.example.democoin.upbit.service.FiveMinutesCandleService;
import com.example.democoin.utils.IndicatorUtil;
import com.example.democoin.indicator.result.RSIs;
import com.example.democoin.backtest.common.AccountCoinWallet;
import com.example.democoin.backtest.common.AccountCoinWalletRepository;
import com.example.democoin.backtest.common.BackTestOrdersRepository;
import com.example.democoin.backtest.service.BackTestOrderService;
import com.example.democoin.backtest.strategy.ask.AskStrategy;
import com.example.democoin.backtest.strategy.ask.BackTestAskSignal;
import com.example.democoin.backtest.strategy.bid.BackTestBidSignal;
import com.example.democoin.backtest.strategy.bid.BidStrategy;
import com.example.democoin.upbit.db.entity.FiveMinutesCandle;
import com.example.democoin.upbit.enums.MarketType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class BackTest2 {

    private final FiveMinutesCandleService fiveMinutesCandleService;
    private final AccountCoinWalletService accountCoinWalletService;
    private final BackTestOrderService orderService;

    private final AccountCoinWalletRepository accountCoinWalletRepository;
    private final BackTestOrdersRepository backTestOrdersRepository;

    double balance = 1000000.0; // 잔고

    public void start() {
        int page = 1;
        backTestOrdersRepository.deleteAll();
        accountCoinWalletRepository.deleteAll();

        for (MarketType marketType : MarketType.marketTypeList) {
            accountCoinWalletRepository.save(AccountCoinWallet.of(marketType, balance * marketType.getPercent()));
        }

        boolean over = false;
        while (!over) {
            int limit = 1;
            int offset = (page - 1) * limit;

            for (MarketType market : MarketType.marketTypeList) {
                List<FiveMinutesCandle> fiveMinutesCandles = fiveMinutesCandleService.findFiveMinutesCandlesLimitOffset(market.getType(), LocalDateTime.of(2022, 1, 1, 0, 0, 0), limit, offset);
                if (market == MarketType.KRW_BTC) {
                    log.info("날짜 : {}", fiveMinutesCandles.get(0).getCandleDateTimeKst());
                }
                if (CollectionUtils.isEmpty(fiveMinutesCandles)) {
                    continue;
                }

                FiveMinutesCandle baseCandle = fiveMinutesCandles.get(0);
                if (Objects.isNull(baseCandle.getTimestamp())) {
                    continue;
                }

                List<FiveMinutesCandle> candles = fiveMinutesCandleService.findFiveMinutesCandlesUnderByTimestamp(market.getType(), baseCandle.getTimestamp());
                if (candles.size() < 200) {
                    log.info("해당 시간대는 캔들 200개 미만이므로 테스트할 수 없습니다. -- {}", baseCandle.getCandleDateTimeKst());
                    continue;
                }

                // targetCandle의 봉에서 매수, 매도
                FiveMinutesCandle targetCandle = fiveMinutesCandleService.nextCandle(baseCandle.getTimestamp(), baseCandle.getMarket().getType());

                if (Objects.isNull(targetCandle)) {
                    log.info("{} 해당 캔들에서 종료됨", baseCandle.getCandleDateTimeKst());
                    return;

                }
                AccountCoinWallet wallet = accountCoinWalletRepository.findByMarket(market);

                boolean isAskable = accountCoinWalletService.isAskable(wallet);
                boolean isBidable = accountCoinWalletService.isBidable(wallet);

                if (!isAskable && !isBidable) {
                    continue;
                }

                List<Double> prices = candles.stream().map(FiveMinutesCandle::getTradePrice).collect(Collectors.toUnmodifiableList());
                BollingerBands bollingerBands = IndicatorUtil.getBollingerBands(prices);
                RSIs rsi14 = IndicatorUtil.getRSI14(prices);

                boolean isAsk = isAskable && 매도신호(AskStrategy.STRATEGY_8, bollingerBands, rsi14, candles, wallet, targetCandle);

                if (isAsk) { // 매도
                    log.info("{} 현재 캔들", targetCandle.getCandleDateTimeKst());
                    orderService.ask(targetCandle, wallet);
                    log.info("{}% \r\n", targetCandle.getCandlePercent());
                }

                boolean isBid = isBidable && 매수신호(BidStrategy.STRATEGY_7, bollingerBands, rsi14, candles, targetCandle);
                if (isBid) { // 매수
                    log.info("{} 현재 캔들", targetCandle.getCandleDateTimeKst());
                    orderService.bid(targetCandle, wallet);
                    log.info("{}% \r\n", targetCandle.getCandlePercent());
                }

                printWalletInfo(accountCoinWalletService.fetchWallet(market, targetCandle.getTradePrice()));

                if (LocalDateTime.of(2022, 3, 13, 0, 0, 0).isBefore(targetCandle.getCandleDateTimeKst())) {
                    return;
                }
            }
            page++;
        }
    }

    private void printWalletInfo(AccountCoinWallet fetchWallet) {
        if (!fetchWallet.isEmpty()) {
            log.info(fetchWallet.getWalletInfo());
        }
    }

    private boolean 매수신호(BidStrategy bidStrategy, BollingerBands bollingerBands, RSIs rsi14, List<FiveMinutesCandle> candles, FiveMinutesCandle targetCandle) {
        switch (bidStrategy) {
            case STRATEGY_1: // 볼린저밴드 하단 돌파 / RSI14 35 이하
                return BackTestBidSignal.strategy_1(rsi14, bollingerBands, targetCandle);
            case STRATEGY_2:
                return BackTestBidSignal.strategy_2(bollingerBands, targetCandle);
            case STRATEGY_3:
                return BackTestBidSignal.strategy_3(candles, targetCandle);
            case STRATEGY_4:
                return BackTestBidSignal.strategy_4(bollingerBands, candles.get(0)); // 이전 캔들 필요
            case STRATEGY_5:
                return BackTestBidSignal.strategy_5(rsi14, candles, targetCandle);
            case STRATEGY_6: // 5이평 10이평 골든크로스
                return BackTestBidSignal.strategy_6(bollingerBands, candles, targetCandle);
            case STRATEGY_7: // rsi14 30 이하 / 볼린저 밴드 8개봉 수축 / 20 이평 이상
                return BackTestBidSignal.strategy_7(bollingerBands, rsi14, candles, targetCandle);
            default:
                return false;
        }
    }

    private boolean 매도신호(AskStrategy askStrategy, BollingerBands bollingerBands, RSIs rsi14, List<FiveMinutesCandle> candles, AccountCoinWallet wallet, FiveMinutesCandle targetCandle) {
        switch (askStrategy) {
            case STRATEGY_1:
                return BackTestAskSignal.strategy_1(wallet, rsi14, bollingerBands, candles.get(0), targetCandle);
            case STRATEGY_2:
                return BackTestAskSignal.strategy_2(wallet, bollingerBands, targetCandle);
            case STRATEGY_3:
                return BackTestAskSignal.strategy_3(wallet, candles);
            case STRATEGY_4:
                return BackTestAskSignal.strategy_4(wallet, rsi14, bollingerBands, targetCandle);
            case STRATEGY_5:
                return BackTestAskSignal.strategy_5(wallet, rsi14, targetCandle);
            case STRATEGY_6:
                return BackTestAskSignal.strategy_6(wallet, bollingerBands, targetCandle);
            case STRATEGY_7:
                return BackTestAskSignal.strategy_7(candles, targetCandle);
            case STRATEGY_8:
                return BackTestAskSignal.strategy_8(wallet, bollingerBands, rsi14, targetCandle);
            default:
                return false;
        }
    }
}
