package com.example.democoin.backtest.service;

import com.example.democoin.backtest.entity.AccountCoinWallet;
import com.example.democoin.backtest.repository.AccountCoinWalletRepository;
import com.example.democoin.backtest.entity.BackTestOrders;
import com.example.democoin.backtest.repository.BackTestOrdersRepository;
import com.example.democoin.upbit.db.entity.FiveMinutesCandle;
import com.example.democoin.utils.NumberUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.democoin.DemoCoinApplication.df;
import static com.example.democoin.utils.IndicatorUtil.fee;
import static com.example.democoin.upbit.enums.OrdSideType.ASK;
import static com.example.democoin.upbit.enums.OrdSideType.BID;

@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
@Service
public class BackTestOrderServiceImpl implements BackTestOrderService {

    private final BackTestOrdersRepository backTestOrdersRepository;
    private final AccountCoinWalletRepository accountCoinWalletRepository;

    @Transactional
    @Override
    public BackTestOrders bid(FiveMinutesCandle targetCandle, AccountCoinWallet wallet) { // 매수
        double openingPrice = targetCandle.getOpeningPrice();
        double fee = fee(wallet.getBalance());
        double bidAmount = wallet.getBalance();
        double volume = bidAmount / openingPrice; // 매수량

        // 다음 캔들 시가에 매수
        BackTestOrders order = backTestOrdersRepository.save(BackTestOrders.of(targetCandle.getMarket(), BID, openingPrice, volume, fee, targetCandle.getTimestamp()));

        wallet.allBid(openingPrice, bidAmount, volume, fee);
        accountCoinWalletRepository.save(wallet);

        log.info("{} 매수 발생 !! ---- 매수가 {}원 / 매수 볼륨 {}", targetCandle.getMarket(), df.format(openingPrice), volume);

        return order;
    }

    @Transactional
    @Override
    public BackTestOrders ask(FiveMinutesCandle targetCandle, AccountCoinWallet wallet) { // 매도
        if (wallet.isEmpty()) {
            log.info("---------- 가진 것도 없는데 뭘 매도해 ----------");
            return null;
        }
        double volume = wallet.getVolume();
//        double valAmount = BigDecimal.valueOf(targetCandle.getTradePrice() * volume).setScale(2, HALF_UP).doubleValue();
        double valAmount = targetCandle.getTradePrice() * volume;
        double fee = fee(valAmount);
//        double proceeds = BigDecimal.valueOf(valAmount - wallet.getAllPrice()).setScale(2, HALF_UP).doubleValue();
//        double proceedRate = BigDecimal.valueOf(proceeds / wallet.getAllPrice() * 100).setScale(2, HALF_UP).doubleValue();
        double proceeds = valAmount - wallet.getAllPrice();
        double proceedRate = proceeds / wallet.getAllPrice() * 100;
        double maxProceedRate = NumberUtils.max(wallet.getMaxProceedRate(), proceedRate);

        log.info("{} 매도 발생 !! ---- 수익률 {}% 매도가 : {} / 매도 볼륨 {}, "
                , wallet.getMarket()
                , proceedRate
                , targetCandle.getTradePrice()
                , volume);

        BackTestOrders order = backTestOrdersRepository.save(BackTestOrders.of(
                wallet.getMarket(),
                ASK,
                targetCandle.getOpeningPrice(),
                volume, fee, targetCandle.getTimestamp(),
                proceeds,
                proceedRate,
                maxProceedRate));

        // 매도 -> 다음 캔들 시가에 매도
        wallet.allAsk(valAmount, fee);
        accountCoinWalletRepository.save(wallet);

        return order;
    }
}
