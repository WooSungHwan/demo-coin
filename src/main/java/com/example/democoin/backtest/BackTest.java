package com.example.democoin.backtest;

import com.example.democoin.BollingerBands;
import com.example.democoin.Indicator;
import com.example.democoin.RSIs;
import com.example.democoin.backtest.common.AccountCoinWallet;
import com.example.democoin.backtest.common.AccountCoinWalletRepository;
import com.example.democoin.backtest.common.BackTestOrders;
import com.example.democoin.backtest.common.BackTestOrdersRepository;
import com.example.democoin.backtest.strategy.ask.AskSignal;
import com.example.democoin.backtest.strategy.ask.BackTestAskSignal;
import com.example.democoin.backtest.strategy.bid.BackTestBidSignal;
import com.example.democoin.backtest.strategy.bid.BidSignal;
import com.example.democoin.upbit.db.entity.FiveMinutesCandle;
import com.example.democoin.upbit.db.repository.FiveMinutesCandleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
public class BackTest {

    // strategy_4
    // strategy_2 매수 10000, 매도 6000, 수익률 10 이상 전액매도 -> 망함.
    // -40.401830130892534 strategy_2 매수 10000, 매도 6000
    // -39.43989381755899 strategy_2 매수 10000, 매도 6000
    // -26.48654437695176 strategy_1

    private final FiveMinutesCandleRepository fiveMinutesCandleRepository;
    private final AccountCoinWalletRepository accountCoinWalletRepository;
    private final BackTestOrdersRepository backTestOrdersRepository;

    double balance = 100000.0; // 잔고
    double checkPercent = 0.3;
    double bidPrice = 10000.0; // 매수 고정 금액
    final double askPrice = 10000.0; // 매도 고정 금액
    double bidFee = bidPrice * 0.0005;
    double askFee = askPrice * 0.0005;
    String targetMarket = "KRW-BTC";

    DecimalFormat df = new DecimalFormat("###,###"); // 출력 숫자 포맷

    public void start() {
        int page = 1;
        backTestOrdersRepository.deleteAll();
        accountCoinWalletRepository.deleteAll();

        double myAsset = balance;
        double myCheckLine = balance * checkPercent;

        boolean over = false;
        while (!over) {
            int limit = 200;
            int offset = (page - 1) * limit;
            List<FiveMinutesCandle> fiveMinutesCandles = fiveMinutesCandleRepository.findFiveMinutesCandlesLimitOffset(
                    LocalDateTime.of(2017, 12, 1, 0, 0, 0), limit, offset);

            for (int i = 0; i < fiveMinutesCandles.size(); i++) {
                if (i < 3) {
                    continue;
                }
                FiveMinutesCandle baseCandle = fiveMinutesCandles.get(i);
                if (baseCandle.getTimestamp() == null) {
                    continue;
                }
                List<FiveMinutesCandle> candles = fiveMinutesCandleRepository.findFiveMinutesCandlesUnderByTimestamp(baseCandle.getTimestamp());

                if (candles.size() < 200) {
                    log.info("해당 시간대는 캔들 200개 미만이므로 테스트할 수 없습니다. -- {}", baseCandle.getCandleDateTimeKst());
                    continue;
                }

                // targetCandle의 봉에서 매수, 매도, 잔액 찍을것임.
                FiveMinutesCandle targetCandle = fiveMinutesCandleRepository.nextCandle(baseCandle.getTimestamp(), baseCandle.getMarket());
                double candlePercent = (targetCandle.getTradePrice() / targetCandle.getOpeningPrice() * 100) - 100;

                if (Objects.isNull(targetCandle)) {
                    log.info("{} 해당 캔들에서 종료됨", baseCandle.getCandleDateTimeKst());
                    return;
                }

                Double openingPrice = targetCandle.getOpeningPrice(); // 다음캔들 시가
                final double bidVolume = bidPrice / openingPrice; // 시가로 매수할 거래량 계산
                final double askVolume = askPrice / openingPrice; // 시가로 매도할 거래량 계산

                AccountCoinWallet wallet = accountCoinWalletRepository.findByMarket(targetMarket);

                if (Objects.nonNull(wallet)) {
                    myAsset = wallet.getValAmount() + balance;
                    myCheckLine = myAsset * checkPercent;
                }

                boolean isAskable = 매도가능(wallet);
                boolean isBidable = 매수가능(myCheckLine);

                if (!isAskable && !isBidable) {
                    continue;
                }

                List<Double> prices = candles.stream().map(FiveMinutesCandle::getTradePrice).collect(Collectors.toUnmodifiableList());
                BollingerBands bollingerBands = Indicator.getBollingerBands(prices);
                RSIs rsi14 = Indicator.getRSI14(prices);

                AskSignal askSignal = AskSignal.STRATEGY_2; // TODO 추후 그래프 하락 / 횡보 / 상승 판단하여 전략 다변화 구사
                boolean isAsk = isAskable && 매도신호(askSignal, bollingerBands, rsi14, candles);

                if (isAskable && (wallet.수익률() < -3)) {
                    double volume = wallet.getVolume();
                    double price = openingPrice * volume;
                    double fee = askPrice * 0.0005;
                    log.info("--------- 전액매도 ----------");
                    매도(targetCandle, openingPrice, volume, price, fee, wallet);
                    isAsk = false;
                }

                if (isAsk) {
                    log.info("{} 현재 캔들", targetCandle.getCandleDateTimeKst());
                    매도(targetCandle, openingPrice, askVolume, askPrice, askFee, wallet);
                    printWalletBalance("매도 후", targetMarket);
                    log.info("{}% \r\n", BigDecimal.valueOf(candlePercent).setScale(2, RoundingMode.HALF_UP).doubleValue());
                }

                // 타겟 포함하는 캔들에서 매수신호가 떨어지면 다음 캔들의 시가에서 매수한다.
                BidSignal bidSignal = BidSignal.STRATEGY_2;
                boolean isBid = isBidable && 매수신호(bidSignal, bollingerBands, rsi14, candles);

                if (isBid && candlePercent >= -3) { // 전 캔들이 -3% 이상 음봉이면 안산다.
                    log.info("{} 현재 캔들", targetCandle.getCandleDateTimeKst());
                    매수(targetCandle, openingPrice, bidPrice, bidFee, bidVolume);
                    printWalletBalance("매수 후", targetMarket);
                    log.info("{}% \r\n", BigDecimal.valueOf(candlePercent).setScale(2, RoundingMode.HALF_UP).doubleValue());
                }

                if (LocalDateTime.of(2018, 3, 1, 0, 0, 0).isBefore(targetCandle.getCandleDateTimeKst())) {
                    return;
                }
            }
            page++;
        }
    }

    private void printWalletBalance(String beforeAfterTrade, String market) {
        AccountCoinWallet wallet = accountCoinWalletRepository.findByMarket(market);
        if (Objects.nonNull(wallet)) {
            log.info("[{}] {} 평단가 : {}, 수익률 : {}%, 수익금 : {}, KRW : {}원, 잔고 : {}"
                    , beforeAfterTrade
                    , wallet.getMarket() // 코인 시장
                    , df.format(wallet.getAvgPrice()) // 평단가
                    , wallet.수익률() // 수익률
                    , df.format(wallet.getProceeds())
                    , df.format(wallet.getAllPrice()) // 총 매수가
                    , df.format(balance)); // 잔고
        }
    }

    private void 매수(FiveMinutesCandle nextCandle, Double openingPrice, double bidPrice, double fee, double bidVolume) {
        // 다음 캔들 시가에 매수
        backTestOrdersRepository.save(BackTestOrders.of(nextCandle.getMarket(), BID, openingPrice, bidVolume, fee, nextCandle.getTimestamp()));

        // 지갑에 매수 반영
        setBidWallet(nextCandle, openingPrice, bidPrice, fee, bidVolume);

        balance -= (openingPrice * bidVolume) + fee;

        log.info("{} 매수 발생 !! ---- 매수가 {}원 / 매수 볼륨 {}", nextCandle.getMarket(), df.format(openingPrice), bidVolume);
    }

    private void 매도(FiveMinutesCandle nextCandle, Double openingPrice, double askVolume, double askPrice, double fee, AccountCoinWallet wallet) {
        if (wallet.getAllPrice() == 0 || wallet.getVolume() == 0) {
            log.info("---------- 가진 것도 없는데 뭘 매도해 ----------");
            return;
        }
        // 매도 -> 다음 캔들 시가에 매도
        backTestOrdersRepository.save(BackTestOrders.of(wallet.getMarket(), ASK, openingPrice, askVolume, fee, nextCandle.getTimestamp()));
        if (wallet.getVolume() == askVolume) { // 전액매도라면 TODO ENUM만들어야겠다.
            accountCoinWalletRepository.delete(wallet);
        } else { //전액매도가 아니라면
            // 지갑에 매도 반영
            wallet.addAsk(openingPrice, askVolume, askPrice, fee);
            accountCoinWalletRepository.save(wallet);
        }

        balance += (openingPrice * askVolume) - fee;

        log.info("{} 매도 발생 !! ---- 매도가 {}원 / 매도 볼륨 {}", wallet.getMarket(), df.format(openingPrice), askVolume);
    }

    private boolean 매수가능(double myCheckLine) {
        double bidAmount = bidPrice + bidFee;
        if (balance - bidAmount > myCheckLine && balance > bidAmount) {
            return true;
        } else {
            log.info("매수 잔고 부족 - 현재 잔고 : {}원", df.format(balance));
            return false;
        }
    }

    private boolean 매도가능(AccountCoinWallet wallet) {
        if (Objects.isNull(wallet)) {
            return false;
        }

        if (wallet.getValAmount() >= 5000) {
            return true;
        } else {
            log.info("{} : 매도 잔고 부족 - 현재 금액 : {}원", wallet.getMarket(), df.format(wallet.getAllPrice()));
            return false;
        }
    }

    private AccountCoinWallet setBidWallet(FiveMinutesCandle nextCandle, Double openingPrice, double bidPrice, double fee, double volume) {
        AccountCoinWallet wallet = accountCoinWalletRepository.findByMarket(nextCandle.getMarket());
        if (Objects.isNull(wallet)) {
            accountCoinWalletRepository.save(AccountCoinWallet.of(nextCandle.getMarket(), openingPrice, volume, bidPrice + fee));
        } else {
            wallet.addBid(openingPrice, volume, bidPrice, fee);
            accountCoinWalletRepository.save(wallet);
        }
        return wallet;
    }

    /**
     * 매도 신호 포착
     * @param bollingerBands 볼린저 밴드
     * @param rsi14 rsi
     * @Param candles
     * @return
     */
    private boolean 매도신호(AskSignal askSignal, BollingerBands bollingerBands, RSIs rsi14, List<FiveMinutesCandle> candles) {
        switch (askSignal) {
            case STRATEGY_1: // 볼린저밴드 상단 돌파, RSI14 65 이상
                return BackTestAskSignal.strategy_1(rsi14, bollingerBands, candles.get(0));
            case STRATEGY_2:
                return BackTestAskSignal.strategy_2(bollingerBands, candles.get(0));
            case STRATEGY_3:
                return BackTestAskSignal.strategy_3(candles);
            case STRATEGY_4:
                return BackTestAskSignal.strategy_4(rsi14, bollingerBands, candles.get(0));
            default:
                return false;
        }
    }

    /**
     * 매수 신호 포착
     * @param bollingerBands 볼린저 밴드
     * @param rsi14 rsi
     * @param candles 최근 캔들
     * @return
     */
    private boolean 매수신호(BidSignal bidSignal, BollingerBands bollingerBands, RSIs rsi14, List<FiveMinutesCandle> candles) {
        switch (bidSignal) {
            case STRATEGY_1: // 볼린저밴드 하단 돌파 / RSI14 35 이하
                return BackTestBidSignal.strategy_1(rsi14, bollingerBands, candles.get(0));
            case STRATEGY_2:
                return BackTestBidSignal.strategy_2(bollingerBands, candles.get(0));
            case STRATEGY_3:
                return BackTestBidSignal.strategy_3(candles);
            case STRATEGY_4:
                return BackTestBidSignal.strategy_4(bollingerBands, candles.get(0));
            default:
                return false;
        }
    }
}
