package com.example.democoin.backtest;

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

import java.text.DecimalFormat;
import java.util.List;
import java.util.Objects;

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
        boolean over = false;
        while (!over) {
            int limit = 200;
            int offset = (page - 1) * limit;
            List<FiveMinutesCandle> fiveMinutesCandles = fiveMinutesCandleRepository.findFiveMinutesCandlesLimitOffset(limit, offset);

            for (int i = 0; i < fiveMinutesCandles.size(); i++) {
                if (i < 3) {
                    continue;
                }
                FiveMinutesCandle target = fiveMinutesCandles.get(i);
                List<FiveMinutesCandle> candles = fiveMinutesCandleRepository.findFiveMinutesCandlesUnderByTimestamp(target.getTimestamp());

                FiveMinutesCandle nextCandle = fiveMinutesCandleRepository.nextCandle(target.getTimestamp(), target.getMarket());
                if (Objects.isNull(nextCandle)) {
                    log.info("{} 해당 캔들에서 종료됨", target.getCandleDateTimeKst());
                    return;
                }
                Double openingPrice = nextCandle.getOpeningPrice(); // 다음캔들 시가
                final double bidVolume = bidPrice / openingPrice; // 시가로 매수할 거래량 계산
                final double askVolume = askPrice / openingPrice; // 시가로 매도할 거래량 계산
                final double fee = bidPrice * 0.0005; // 수수료 계산

                AccountCoinWallet wallet = accountCoinWalletRepository.findByMarket(targetMarket);
                if (매도가능(wallet) && 매도신호(wallet, candles)) {
                    매도(nextCandle, openingPrice, askVolume, fee, wallet);
                }

                // 타겟 포함하는 캔들에서 매수신호가 떨어지면 다음 캔들의 시가에서 매수한다.
                if (매수가능() && 매수신호(candles)) {
                    매수(nextCandle, openingPrice, bidPrice, fee, bidVolume);
                }

                // 잔고 출력
                printWalletBalance(nextCandle, wallet);
                return ;
            }
            page++;
        }
    }

    private void printWalletBalance(FiveMinutesCandle nextCandle, AccountCoinWallet wallet) {
        log.info("{} 평단가 : {}, 수익률 : {}%, KRW : {}원", wallet.getMarket(), df.format(wallet.getAvgPrice()), wallet.수익률(nextCandle.getTradePrice()), df.format(wallet.getAllPrice()));
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
        wallet.addAsk(askVolume, askPrice, fee);
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
            accountCoinWalletRepository.save(AccountCoinWallet.of(nextCandle.getMarket(), openingPrice, volume, bidPrice));
        } else {
            wallet.addBid(volume, bidPrice, fee);
            accountCoinWalletRepository.save(wallet);
        }
        return wallet;
    }

    /**
     * 매도 신호 포착
     * @param coinWallet 현재 보유 코인
     * @param candles 백테스팅 시각의 캔들(500개)
     * @return
     */
    private boolean 매도신호(AccountCoinWallet coinWallet, List<FiveMinutesCandle> candles) {
        if (CollectionUtils.isEmpty(candles)) {
            return false;
        }
        String market = coinWallet.getMarket(); // 해당 마켓의 코드


        return false;
    }

    /**
     * 매수 신호 포착
     * @param candles 백테스팅 시각의 캔들(500개)
     * @return
     */
    private boolean 매수신호(List<FiveMinutesCandle> candles) {

        return false;
    }
}
