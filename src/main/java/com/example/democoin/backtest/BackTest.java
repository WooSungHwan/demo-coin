package com.example.democoin.backtest;

import com.example.democoin.BollingerBands;
import com.example.democoin.Indicator;
import com.example.democoin.RSIs;
import com.example.democoin.backtest.common.AccountCoinWallet;
import com.example.democoin.backtest.common.AccountCoinWalletRepository;
import com.example.democoin.backtest.common.BackTestOrders;
import com.example.democoin.backtest.common.BackTestOrdersRepository;
import com.example.democoin.upbit.db.entity.FiveMinutesCandle;
import com.example.democoin.upbit.db.repository.FiveMinutesCandleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalField;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.example.democoin.upbit.enums.OrdSideType.ASK;
import static com.example.democoin.upbit.enums.OrdSideType.BID;

@Slf4j
@RequiredArgsConstructor
@Component
public class BackTest {

    private final FiveMinutesCandleRepository fiveMinutesCandleRepository;
    private final AccountCoinWalletRepository accountCoinWalletRepository;
    private final BackTestOrdersRepository backTestOrdersRepository;

    double 시작잔고 = 100000.0;
    double bidPrice = 10000.0; // 매수 고정 금액
    final double askPrice = 6000.0; // 매도 고정 금액

    String targetMarket = "KRW-BTC";

    DecimalFormat df = new DecimalFormat("###,###"); // 출력 숫자 포맷

    public void start() {
        int page = 1;
        backTestOrdersRepository.deleteAll();
        accountCoinWalletRepository.deleteAll();

        boolean over = false;
        while (!over) {
            int limit = 200;
            int offset = (page - 1) * limit;
            List<FiveMinutesCandle> fiveMinutesCandles = fiveMinutesCandleRepository.findFiveMinutesCandlesLimitOffset(
                    LocalDateTime.of(2017, 11, 1, 0, 0, 0), limit, offset);

            for (int i = 0; i < fiveMinutesCandles.size(); i++) {
                if (i < 3) {
                    continue;
                }
                FiveMinutesCandle baseCandle = fiveMinutesCandles.get(i);
                if (Objects.isNull(baseCandle)) {
                    System.out.println();
                }
//                log.info("KST {} 캔들 백테스팅 시작", target.getCandleDateTimeKst());
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
                if (Objects.isNull(targetCandle)) {
                    log.info("{} 해당 캔들에서 종료됨", baseCandle.getCandleDateTimeKst());
                    return;
                }

                Double openingPrice = targetCandle.getOpeningPrice(); // 다음캔들 시가
                final double bidVolume = bidPrice / openingPrice; // 시가로 매수할 거래량 계산
                final double askVolume = askPrice / openingPrice; // 시가로 매도할 거래량 계산
                final double fee = bidPrice * 0.0005; // 수수료 계산

                AccountCoinWallet wallet = accountCoinWalletRepository.findByMarket(targetMarket);
                boolean isAskable = 매도가능(wallet);
                boolean isBidable = 매수가능();

                if (!isAskable && !isBidable) {
                    continue;
                }

                List<Double> prices = candles.stream().map(FiveMinutesCandle::getTradePrice).collect(Collectors.toUnmodifiableList());
                BollingerBands bollingerBands = Indicator.getBollingerBands(prices);
                RSIs rsi14 = Indicator.getRSI14(prices);

                boolean isAsk = isAskable && 매도신호(wallet, bollingerBands, rsi14, baseCandle);
                if (isAsk) {
                    log.info("{} 현재 캔들", targetCandle.getCandleDateTimeKst());
                    printWalletBalance("매도 전", targetMarket);
                    매도(targetCandle, openingPrice, askVolume, fee, wallet);
                    printWalletBalance("매도 후", targetMarket);
                    log.info("");
                }

                // 타겟 포함하는 캔들에서 매수신호가 떨어지면 다음 캔들의 시가에서 매수한다.
                boolean isBid = isBidable && 매수신호(targetMarket, bollingerBands, rsi14, baseCandle);
                if (isBid) {
                    log.info("{} 현재 캔들", targetCandle.getCandleDateTimeKst());
                    printWalletBalance("매수 전", targetMarket);
                    매수(targetCandle, openingPrice, bidPrice, fee, bidVolume);
                    printWalletBalance("매수 후", targetMarket);
                    log.info("");
                }

                if (LocalDateTime.of(2018, 1, 1, 0, 0, 0).isBefore(targetCandle.getCandleDateTimeKst())) {
                    return;
                }
            }
            page++;
        }
    }

    private void printWalletBalance(String beforeAfterTrade, String market) {
        AccountCoinWallet wallet = accountCoinWalletRepository.findByMarket(market);
        if (Objects.nonNull(wallet)) {
            log.info("[{}] {} 평단가 : {}, 수익률 : {}%, 보유코인 : {}, KRW : {}원"
                    , beforeAfterTrade
                    , wallet.getMarket() // 코인 시장
                    , df.format(wallet.getAvgPrice()) // 평단가
                    , wallet.수익률() // 수익률
                    , wallet.getVolume() // 보유코인
                    , df.format(wallet.getAllPrice())); // 총 매수가
        }
    }

    private void 매수(FiveMinutesCandle nextCandle, Double openingPrice, double bidPrice, double fee, double bidVolume) {
        // 다음 캔들 시가에 매수
        backTestOrdersRepository.save(BackTestOrders.of(nextCandle.getMarket(), BID, openingPrice, bidVolume, fee, nextCandle.getTimestamp()));

        // 지갑에 매수 반영
        setBidWallet(nextCandle, openingPrice, bidPrice, fee, bidVolume);

        log.info("{} 매수 발생 !! ---- 매수가 {}원 / 매수 볼륨 {}", nextCandle.getMarket(), df.format(openingPrice), bidVolume);
    }

    private void 매도(FiveMinutesCandle nextCandle, Double openingPrice, double askVolume, double fee, AccountCoinWallet wallet) {
        // 매도 -> 다음 캔들 시가에 매도
        backTestOrdersRepository.save(BackTestOrders.of(wallet.getMarket(), ASK, openingPrice, askVolume, fee, nextCandle.getTimestamp()));
        // 지갑에 매도 반영
        wallet.addAsk(openingPrice, askVolume, askPrice, fee);
        accountCoinWalletRepository.save(wallet);

        log.info("{} 매도 발생 !! ---- 매도가 {}원 / 매도 볼륨 {}", wallet.getMarket(), df.format(openingPrice), askVolume);
    }

    private boolean 매수가능() {
        if (시작잔고 > bidPrice) {
            return true;
        } else {
            log.info("매수 잔고 부족 - 현재 잔고 : {}원", df.format(시작잔고));
            return false;
        }
    }

    private boolean 매도가능(AccountCoinWallet wallet) {
        if (Objects.isNull(wallet)) {
            return false;
        }
        if (wallet.getAllPrice() > askPrice) {
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
     * @param coinWallet 현재 지갑 정보
     * @param bollingerBands 볼린저 밴드
     * @param rsi14 rsi
     * @param candle 최근 캔들 1개
     * @return
     */
    private boolean 매도신호(AccountCoinWallet coinWallet, BollingerBands bollingerBands, RSIs rsi14, FiveMinutesCandle candle) {
        String market = coinWallet.getMarket(); // 해당 마켓의 코드

        // 볼린저 밴드 상단 터치
        // rsi 65 이상
        if (rsi14.isOver(65) && bollingerBands.isBollingerBandUddTouch(candle)) {
            log.info("{} 해당 코인 매도 신호 발생, KST 캔들 시각 : {}, rsi : {}, 볼밴 상단 : {}",
                    market, candle.getCandleDateTimeKst(), rsi14.getRsi().get(0), bollingerBands.getLdd().get(0));
            return true;
        }
        return false;
    }

    /**
     * 매수 신호 포착
     * @param market 코인 코드
     * @param bollingerBands 볼린저 밴드
     * @param rsi14 rsi
     * @param candle 최근 캔들 1개
     * @return
     */
    private boolean 매수신호(String market, BollingerBands bollingerBands, RSIs rsi14, FiveMinutesCandle candle) {
        // 볼린저 밴드 하단 터치
        // rsi 35 미만
        if (rsi14.isUnder(35) && bollingerBands.isBollingerBandLddTouch(candle)) {
            log.info("{} 해당 코인 매수 신호 발생, KST 캔들 시각 : {}, rsi : {}, 볼밴하단 : {}",
                    market, candle.getCandleDateTimeKst(), rsi14.getRsi().get(0), bollingerBands.getLdd().get(0));
            return true;
        }

        return false;
    }
}
