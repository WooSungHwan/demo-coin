package com.example.democoin.backtest;

import com.example.democoin.BollingerBands;
import com.example.democoin.Indicator;
import com.example.democoin.RSIs;
import com.example.democoin.backtest.common.AccountCoinWallet;
import com.example.democoin.backtest.common.AccountCoinWalletRepository;
import com.example.democoin.backtest.common.BackTestOrders;
import com.example.democoin.backtest.common.BackTestOrdersRepository;
import com.example.democoin.backtest.strategy.ask.AskStrategy;
import com.example.democoin.backtest.strategy.ask.BackTestAskSignal;
import com.example.democoin.backtest.strategy.bid.BackTestBidSignal;
import com.example.democoin.backtest.strategy.bid.BidStrategy;
import com.example.democoin.upbit.db.entity.FiveMinutesCandle;
import com.example.democoin.upbit.db.repository.FiveMinutesCandleRepository;
import com.example.democoin.upbit.enums.MarketType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.example.democoin.upbit.enums.OrdSideType.ASK;
import static com.example.democoin.upbit.enums.OrdSideType.BID;

@Slf4j
@RequiredArgsConstructor
@Component
public class BackTest2 {

    private final FiveMinutesCandleRepository fiveMinutesCandleRepository;
    private final AccountCoinWalletRepository accountCoinWalletRepository;
    private final BackTestOrdersRepository backTestOrdersRepository;

    DecimalFormat df = new DecimalFormat("###,###"); // 출력 숫자 포맷

    double balance = 1000000.0; // 잔고
    List<MarketType> marketTypeList = MarketType.marketTypeList;

    public void start() {
        int page = 1;
        backTestOrdersRepository.deleteAll();
        accountCoinWalletRepository.deleteAll();

        for (MarketType marketType : marketTypeList) {
            accountCoinWalletRepository.save(AccountCoinWallet.of(marketType, balance * marketType.getPercent()));
        }

        boolean over = false;
        while (!over) {
            int limit = 1;
            int offset = (page - 1) * limit;

            for (MarketType market : marketTypeList) {
                List<FiveMinutesCandle> fiveMinutesCandles = fiveMinutesCandleRepository.findFiveMinutesCandlesLimitOffset(
                        market.getType(), LocalDateTime.of(2019, 1, 1, 0, 0, 0), limit, offset);
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

                List<FiveMinutesCandle> candles = fiveMinutesCandleRepository.findFiveMinutesCandlesUnderByTimestamp(market.getType(), baseCandle.getTimestamp());
                if (candles.size() < 200) {
                    log.info("해당 시간대는 캔들 200개 미만이므로 테스트할 수 없습니다. -- {}", baseCandle.getCandleDateTimeKst());
                    continue;
                }

                // targetCandle의 봉에서 매수, 매도
                FiveMinutesCandle targetCandle = fiveMinutesCandleRepository.nextCandle(
                        baseCandle.getTimestamp(),
                        baseCandle.getMarket().getType());
                if (Objects.isNull(targetCandle)) {
                    log.info("{} 해당 캔들에서 종료됨", baseCandle.getCandleDateTimeKst());
                    return;

                }
                AccountCoinWallet wallet = accountCoinWalletRepository.findByMarket(market);

                boolean isAskable = 매도가능(wallet);
                boolean isBidable = 매수가능(wallet);

                if (!isAskable && !isBidable) {
                    continue;
                }

                List<Double> prices = candles.stream().map(FiveMinutesCandle::getTradePrice).collect(Collectors.toUnmodifiableList());
                BollingerBands bollingerBands = Indicator.getBollingerBands(prices);
                RSIs rsi14 = Indicator.getRSI14(prices);

                boolean isAsk = isAskable && 매도신호(AskStrategy.STRATEGY_6, bollingerBands, rsi14, candles, wallet);

                if (isAsk) { // 매도
                    log.info("{} 현재 캔들", targetCandle.getCandleDateTimeKst());
                    매도(targetCandle, wallet);
                    log.info("{}% \r\n", targetCandle.getCandlePercent());
                }

                boolean isBid = isBidable && 매수신호(BidStrategy.STRATEGY_1, bollingerBands, rsi14, candles);
                if (isBid) { // 매수
                    log.info("{} 현재 캔들", targetCandle.getCandleDateTimeKst());
                    매수(targetCandle, wallet);
                    log.info("{}% \r\n", targetCandle.getCandlePercent());
                }

                printWalletBalance(fetchWallet(market, baseCandle), market);

                if (LocalDateTime.of(2020, 11, 1, 0, 0, 0).isBefore(targetCandle.getCandleDateTimeKst())) {
                    return;
                }
            }
            page++;
        }
    }

    @NotNull
    private AccountCoinWallet fetchWallet(MarketType market, FiveMinutesCandle baseCandle) {
        AccountCoinWallet wallet = accountCoinWalletRepository.findByMarket(market);
        if (!wallet.isEmpty()) {
            wallet.fetch(baseCandle.getTradePrice());
            accountCoinWalletRepository.save(wallet);
        }
        return wallet;
    }

    private void printWalletBalance(AccountCoinWallet wallet, MarketType market) {
        if (!wallet.isEmpty()) {
            log.info("[{}] 평단가 : {}, 최대수익률 : {}%, 수익률 : {}%, 수익금 : {}, 평가금액 : {}"
                    , wallet.getMarket() // 코인 시장
                    , df.format(wallet.getAvgPrice()) // 평단가
                    , wallet.getMaxProceedRate() // 최대 수익률
                    , wallet.getProceedRate() // 수익률
                    , df.format(wallet.getProceeds())
//                    , df.format(wallet.getAllPrice()) // 총 매수가
                    , df.format(wallet.getValAmount())); // 잔고
        }
    }

    private void 매도(FiveMinutesCandle targetCandle, AccountCoinWallet wallet) {
        if (wallet.getAllPrice() == 0 || wallet.getVolume() == 0) {
            log.info("---------- 가진 것도 없는데 뭘 매도해 ----------");
            return;
        }
        log.info("{} 매도 발생 !! ---- 수익률 {}%", wallet.getMarket(), wallet.getProceedRate());

        // 매도 -> 다음 캔들 시가에 매도
        backTestOrdersRepository.save(BackTestOrders.of(wallet.getMarket(), ASK, targetCandle.getOpeningPrice(), wallet.getVolume(), wallet.getValAmount() * 0.0005, targetCandle.getTimestamp(), wallet.getProceeds(), wallet.getProceedRate(), wallet.getMaxProceedRate()));
        wallet.allAsk(targetCandle.getOpeningPrice());
        accountCoinWalletRepository.save(wallet);
    }

    private void 매수(FiveMinutesCandle targetCandle, AccountCoinWallet wallet) {
        double openingPrice = targetCandle.getOpeningPrice();
        double fee = wallet.getBalance() * 0.0005;
        double price = wallet.getBalance() - fee;
        double volume = price / openingPrice;

        // 다음 캔들 시가에 매수
        backTestOrdersRepository.save(BackTestOrders.of(targetCandle.getMarket(), BID, targetCandle.getOpeningPrice(), volume, fee, targetCandle.getTimestamp()));
        wallet.allBid(openingPrice, price, volume, fee);
        accountCoinWalletRepository.save(wallet);
        log.info("{} 매수 발생 !! ---- 매수가 {}원 / 매수 볼륨 {}", targetCandle.getMarket(), df.format(openingPrice), volume);
    }

    private boolean 매수신호(BidStrategy bidStrategy, BollingerBands bollingerBands, RSIs rsi14, List<FiveMinutesCandle> candles) {
        /*try {
            double volumeAvg = candles.stream().limit(20).mapToDouble(FiveMinutesCandle::getCandleAccTradeVolume).average().getAsDouble();
            double candleAccTradeVolume = candles.get(0).getCandleAccTradeVolume();
            if (!(volumeAvg < candleAccTradeVolume)) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }*/
        switch (bidStrategy) {
            case STRATEGY_1: // 볼린저밴드 하단 돌파 / RSI14 35 이하
                return BackTestBidSignal.strategy_1(rsi14, bollingerBands, candles.get(0));
            case STRATEGY_2:
                return BackTestBidSignal.strategy_2(bollingerBands, candles.get(0));
            case STRATEGY_3:
                return BackTestBidSignal.strategy_3(candles);
            case STRATEGY_4:
                return BackTestBidSignal.strategy_4(bollingerBands, candles.get(0));
            case STRATEGY_5:
                return BackTestBidSignal.strategy_5(rsi14, candles);
            case STRATEGY_6: // 5이평 10이평 골든크로스
                return BackTestBidSignal.strategy_6(bollingerBands, candles);
            default:
                return false;
        }
    }

    private boolean 매도신호(AskStrategy askStrategy, BollingerBands bollingerBands, RSIs rsi14, List<FiveMinutesCandle> candles, AccountCoinWallet wallet) {
        switch (askStrategy) {
            case STRATEGY_1:
                return wallet.isMaxProceedRateFall() || wallet.getProceedRate() < -2 || BackTestAskSignal.strategy_1(rsi14, bollingerBands, candles.get(0));
            case STRATEGY_2:
                return wallet.isMaxProceedRateFall() || wallet.getProceedRate() < -2;  // || BackTestAskSignal.strategy_2(bollingerBands, candles.get(0));
            case STRATEGY_3:
                return wallet.isMaxProceedRateFall() || wallet.getProceedRate() < -2 || BackTestAskSignal.strategy_3(candles);
            case STRATEGY_4:
                return wallet.isMaxProceedRateFall() || wallet.getProceedRate() < -2 || BackTestAskSignal.strategy_4(rsi14, bollingerBands, candles.get(0));
            case STRATEGY_5:
                return wallet.isMaxProceedRateFall() || wallet.getProceedRate() < -2 || BackTestAskSignal.strategy_5(rsi14, candles.get(0));
            case STRATEGY_6:
                return BackTestAskSignal.strategy_6(wallet, bollingerBands, candles.get(0));
            case STRATEGY_7:
                return BackTestAskSignal.strategy_7(candles);
            default:
                return false;
        }
    }

    private boolean 매도가능(AccountCoinWallet wallet) {
        if (Objects.nonNull(wallet.getVolume())) {
            return true;
        }
        return false;
    }

    private boolean 매수가능(AccountCoinWallet wallet) {
        if (wallet.isEmpty()) {
            return true;
        }
        return false;
    }
}
