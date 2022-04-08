package com.example.democoin.backtest;

import com.example.democoin.backtest.entity.ResultInfo;
import com.example.democoin.backtest.repository.ResultInfoJdbcTemplate;
import com.example.democoin.backtest.repository.ResultInfoRepository;
import com.example.democoin.backtest.service.AccountCoinWalletService;
import com.example.democoin.backtest.strategy.BidSignalParams;
import com.example.democoin.backtest.strategy.ask.AskReason;
import com.example.democoin.backtest.strategy.bid.BidReason;
import com.example.democoin.indicator.result.BollingerBands;
import com.example.democoin.slack.SlackMessageService;
import com.example.democoin.upbit.enums.MarketFlowType;
import com.example.democoin.upbit.service.CandleService;
import com.example.democoin.utils.IndicatorUtil;
import com.example.democoin.indicator.result.RSIs;
import com.example.democoin.backtest.entity.AccountCoinWallet;
import com.example.democoin.backtest.repository.AccountCoinWalletRepository;
import com.example.democoin.backtest.repository.BackTestOrdersRepository;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static com.example.democoin.backtest.strategy.ask.AskReason.BEAR_MARKET;
import static com.example.democoin.backtest.strategy.ask.AskReason.NO_ASK;
import static com.example.democoin.backtest.strategy.bid.BidReason.NO_BID;

/**
 * 분할매수 도입 안한 버전
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class BackTest2 {

    private final CandleService candleService;
    private final AccountCoinWalletService accountCoinWalletService;
    private final BackTestOrderService orderService;
    private final SlackMessageService slackMessageService;

    private final AccountCoinWalletRepository accountCoinWalletRepository;
    private final BackTestOrdersRepository backTestOrdersRepository;
    private final ResultInfoJdbcTemplate resultInfoJdbcTemplate;
    private final ResultInfoRepository resultInfoRepository;

    double balance = 1000000.0; // 잔고
    public static final int BID_SLOT = 4;
    public static int STOP_LOSS = -2;

    public void start() {
        BidStrategy bidStrategy = BidStrategy.STRATEGY_16;
        AskStrategy askStrategy = AskStrategy.STRATEGY_3;

        LocalDateTime startDate = LocalDateTime.of(2017, 10, 1, 0, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2022, 4, 7, 0, 0, 0);

        backTestOrdersRepository.deleteAll();
        accountCoinWalletRepository.deleteAll();

        for (MarketType marketType : MarketType.marketTypeList) {
            for(int i = 0; i < BID_SLOT; i++) {
                AccountCoinWallet wallet = AccountCoinWallet.of(marketType, balance * marketType.getPercent() / BID_SLOT);
                accountCoinWalletRepository.save(wallet);
            }
        }

        boolean over = false;
        LocalDateTime targetDate = LocalDateTime.of(startDate.getYear(), startDate.getMonthValue(), startDate.getDayOfMonth(), 0, 0, 0);
        while (!over) {

            for (MarketType market : MarketType.marketTypeList) {
                FiveMinutesCandle baseCandle = candleService.findFiveMinutesCandleByKst(market.getType(), targetDate);
                if (Objects.isNull(baseCandle) || Objects.isNull(baseCandle.getTimestamp())) {
                    continue;
                }

                List<FiveMinutesCandle> candles = candleService.findFiveMinutesCandlesUnderByTimestamp(market.getType(), baseCandle.getTimestamp());

                if (candles.size() < 200) {
                    log.info("해당 시간대는 캔들 200개 미만이므로 테스트할 수 없습니다. -- {}", baseCandle.getCandleDateTimeKst());
                    continue;
                }

                // targetCandle의 봉에서 매수, 매도
                FiveMinutesCandle targetCandle = candleService.nextCandle(baseCandle.getTimestamp(), baseCandle.getMarket().getType());

                List<AccountCoinWallet> wallets = accountCoinWalletRepository.findByMarket(market);
                WalletList walletList = WalletList.of(wallets);

                Double MA50 = candleService.getFiveMinuteCandlesMA(candles.get(50), 50);
                Double MA100 = candleService.getFiveMinuteCandlesMA(candles.get(100), 100);
                Double MA150 = candleService.getFiveMinuteCandlesMA(candles.get(150), 150);

                List<Double> prices = candles.stream().map(FiveMinutesCandle::getTradePrice).toList();
                RSIs rsi14 = IndicatorUtil.getRSI14(prices);

                switch(judgeMarketFlowType(MA50, MA100, MA150)) {
                    case BEAR_MARKET -> { // 베어마켓에서는 거래 안한다.
//                        orderService.ask(targetCandle, walletList, BEAR_MARKET); // TODO 버그있음. 찾아야함.
                        wallets.forEach(wallet -> orderService.ask(targetCandle, wallet, BEAR_MARKET, rsi14));
                        log.info("====== {} 베어마켓 진행중 전량 매도 / 거래 중지 ======", market.getName());

                        // 리밸런싱
                        accountCoinWalletService.rebalancing(market);
                        continue;
                    }
                    case BULL_MARKET -> {
                        bidStrategy = BidStrategy.STRATEGY_3;
                        STOP_LOSS = -4;
                    }
                    case SIDEWAYS -> bidStrategy = BidStrategy.STRATEGY_16;
                }

                boolean isAskable = accountCoinWalletService.isAskable(walletList);
                boolean isBidable = accountCoinWalletService.isBidable(walletList);

                if (!isAskable && !isBidable) {
                    continue;
                }

                BollingerBands bollingerBands = IndicatorUtil.getBollingerBands(prices);

                if (isAskable) {
                    // 지갑들  매도
                    askProcess(askStrategy, candles, targetCandle, walletList, bollingerBands, rsi14);
                }

                if (isBidable) {
                    BidSignalParams params = getBidSignalParams(bidStrategy, candles, targetCandle, bollingerBands, rsi14);
                    BidReason bidReason = bidSignal(params);
                    if (bidReason.isBid()) {
                        log.info("{} 현재 캔들", targetCandle.getCandleDateTimeKst());
                        orderService.bid(targetCandle, walletList.getBidableWallet(), bidReason, rsi14);
                        log.info("{}% \r\n", targetCandle.getCandlePercent());
                    }
                }

                List<AccountCoinWallet> fetchWallets = accountCoinWalletService.fetchWallet(market, targetCandle.getTradePrice());
                WalletList result = WalletList.of(fetchWallets);
                printWalletInfo(result);

                // 기간 종료
                if (endDate.isBefore(targetCandle.getCandleDateTimeKst())) {
                    over = true;
                }
            }
            targetDate = targetDate.plusMinutes(5);
        }
        saveResultInfo(bidStrategy, askStrategy, startDate, endDate);
        log.info("===== 거래종료 =====");
    }

    private void askProcess(AskStrategy askStrategy, List<FiveMinutesCandle> candles, FiveMinutesCandle targetCandle, WalletList walletList, BollingerBands bollingerBands, RSIs rsi14) {
        for (AccountCoinWallet wallet : walletList.getAskableWallets()) {
            AskReason askReason = askSignal(askStrategy, bollingerBands, rsi14, candles, wallet);
            if (askReason.isAsk()) { // 매도
                log.info("{} 현재 캔들", targetCandle.getCandleDateTimeKst());
                orderService.ask(targetCandle, wallet, askReason, rsi14);
                log.info("{}% \r\n", targetCandle.getCandlePercent());
            }
        }

        // 리밸런싱
        accountCoinWalletService.rebalancing(walletList.getMarket());
    }

    private BidSignalParams getBidSignalParams(BidStrategy bidStrategy,
                                               List<FiveMinutesCandle> candles,
                                               FiveMinutesCandle targetCandle,
                                               BollingerBands bollingerBands,
                                               RSIs rsi14) {
        return BidSignalParams.builder()
                .bidStrategy(bidStrategy)
                .bollingerBands(bollingerBands)
                .rsi14(rsi14)
                .candles(candles)
                .targetCandle(targetCandle)
                .build();
    }

    private void printWalletInfo(WalletList result) {
        if (result.isNotEmpty()) {
            log.info(result.getWalletSummaryInfo());
        }
    }

    private BidReason bidSignal(BidSignalParams params) {
        BollingerBands bollingerBands = params.getBollingerBands();
        RSIs rsi14 = params.getRsi14();
        List<FiveMinutesCandle> candles = params.getCandles();

        return switch (params.getBidStrategy()) {
            case STRATEGY_1 -> // 볼린저밴드 하단 돌파 / RSI14 35 이하
                    BackTestBidSignal.strategy_1(rsi14, bollingerBands, candles.get(0));
            case STRATEGY_2 -> BackTestBidSignal.strategy_2(bollingerBands, candles.get(0));
            case STRATEGY_3 -> // 단순 5분봉 음봉 3개시 매수
                    BackTestBidSignal.strategy_3(candles, candles.get(0));
            case STRATEGY_4 -> BackTestBidSignal.strategy_4(bollingerBands, candles.get(0)); // 이전 캔들 필요
            case STRATEGY_5 -> BackTestBidSignal.strategy_5(rsi14, candles, candles.get(0));
            case STRATEGY_6 -> // 5이평 10이평 골든크로스
                    BackTestBidSignal.strategy_6(bollingerBands, candles, candles.get(0));
            case STRATEGY_7 -> // rsi14 30 이하 / 볼린저 밴드 8개봉 수축 / 20 이평 이상
                    BackTestBidSignal.strategy_7(bollingerBands, rsi14, candles, candles.get(0));
            case STRATEGY_8 -> // 볼린저밴드 하단선 상향돌파 / 200 이평선 이상 또는 rsi 30 상향 돌파
                    BackTestBidSignal.strategy_8(bollingerBands, rsi14, candles, candles.get(0));
            case STRATEGY_9 -> // 볼린저밴드 하단선 상향돌파 / 200 이평선
                    BackTestBidSignal.strategy_9(bollingerBands, candles);
            case STRATEGY_10 -> // 볼린저밴드 7개봉 수축 / 200 이평 이상 or 볼린저밴드 하단선 상향돌파 / 200 이평선 돌파
                    BackTestBidSignal.strategy_10(bollingerBands, candles);
            case STRATEGY_11 -> // 5분봉 3틱 하락, rsi 50 이하
                    BackTestBidSignal.strategy_11(rsi14, candles);
            case STRATEGY_12 -> // 5분봉 3틱 하락, rsi 40 이하, 볼린저 밴드 하단선 아래
                    BackTestBidSignal.strategy_12(rsi14, bollingerBands, candles);
            case STRATEGY_13 -> // 5분봉 3틱 하락(개선1), rsi 30 이상 50 이하, 볼린저 밴드 하단선 아래
                    BackTestBidSignal.strategy_13(rsi14, bollingerBands, candles);
            case STRATEGY_14 -> // 5분봉 3틱 하락(개선2), 볼린저 밴드 하단선 아래, 15분봉 rsi 40 이하
                throw new RuntimeException("사용하지 않는 전략입니다.");
//                    BackTestBidSignal.strategy_14(bollingerBands, candles, fifRsi14);
            case STRATEGY_15 -> // 5분봉 3틱 하락(개선1)
                    BackTestBidSignal.strategy_15(candles);
            case STRATEGY_16 -> // 5분봉 3틱 하락(개선2)
                    BackTestBidSignal.strategy_16(candles);
            case STRATEGY_17 -> // 5분봉 3틱 하락(개선3)
                    BackTestBidSignal.strategy_17(candles);
            default -> NO_BID;
        };
    }

    private AskReason askSignal(AskStrategy askStrategy, BollingerBands bollingerBands, RSIs rsi14, List<FiveMinutesCandle> candles, AccountCoinWallet wallet) {
        return switch (askStrategy) {
            case STRATEGY_1 -> BackTestAskSignal.strategy_1(wallet, rsi14, bollingerBands, candles.get(0));
            case STRATEGY_2 -> BackTestAskSignal.strategy_2(wallet, bollingerBands, candles.get(0));
            case STRATEGY_3 -> // 단순 5분봉3틱수익손절매(-2% 손절, +2% 익절)
                    BackTestAskSignal.strategy_3(wallet, candles);
            case STRATEGY_4 -> BackTestAskSignal.strategy_4(wallet, rsi14, bollingerBands, candles.get(0));
            case STRATEGY_5 -> BackTestAskSignal.strategy_5(wallet, rsi14, candles.get(0));
            case STRATEGY_6 -> BackTestAskSignal.strategy_6(wallet, bollingerBands, candles.get(0));
            case STRATEGY_7 -> BackTestAskSignal.strategy_7(candles, candles.get(0));
            case STRATEGY_8 -> BackTestAskSignal.strategy_8(wallet, bollingerBands, rsi14, candles.get(0));
            case STRATEGY_9 -> // 볼린저 밴드 상한선 하향돌파 또는 rsi 70 하향돌
                    BackTestAskSignal.strategy_9(wallet, bollingerBands, rsi14, candles, candles.get(0));
            case STRATEGY_10 -> // rsi 50 이상
                    BackTestAskSignal.strategy_10(wallet, rsi14, candles.get(0));
//            case STRATEGY_11: //
//                return BackTestAskSignal.strategy_11();
            default -> NO_ASK;
        };
    }

    private void saveResultInfo(BidStrategy bidStrategy, AskStrategy askStrategy, LocalDateTime startDate, LocalDateTime endDate) {
        ResultInfo resultInfo = ResultInfo.builder()
                .askStrategy(askStrategy)
                .bidStrategy(bidStrategy)
                .coinResult(resultInfoJdbcTemplate.getResultInfo())
                .positivePercent(resultInfoJdbcTemplate.getPositivePercent())
                .startDate(startDate)
                .endDate(endDate)
                .build();

        resultInfoRepository.save(resultInfo);

        // 요약정보 슬랙으로 전송
        slackMessageService.backtestMessage(resultInfo.toString());
    }

    private static MarketFlowType judgeMarketFlowType(Double point50, Double point100, Double point150) {
        if (point50 > point100 && point100 > point150) {
            return MarketFlowType.BULL_MARKET;
        } else if (point50 < point100 && point100 < point150) {
            return MarketFlowType.BEAR_MARKET;
        } else {
            return MarketFlowType.SIDEWAYS;
        }
    }
}
