package com.example.democoin.backtest.service;

import com.example.democoin.backtest.WalletList;
import com.example.democoin.backtest.entity.AccountCoinWallet;
import com.example.democoin.backtest.repository.AccountCoinWalletRepository;
import com.example.democoin.backtest.entity.BackTestOrders;
import com.example.democoin.backtest.repository.BackTestOrdersRepository;
import com.example.democoin.backtest.strategy.BidSignalParams;
import com.example.democoin.configuration.enums.Reason;
import com.example.democoin.indicator.result.RSIs;
import com.example.democoin.slack.SlackMessageService;
import com.example.democoin.upbit.db.entity.FiveMinutesCandle;
import com.example.democoin.utils.NumberUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    private final SlackMessageService slackMessageService;

    @Transactional
    @Override
    public BackTestOrders bid(FiveMinutesCandle targetCandle, AccountCoinWallet wallet, Reason reason, RSIs rsIs) { // 매수
        double openingPrice = targetCandle.getOpeningPrice();
        Double bidBalance = wallet.getBalance();
        double fee = fee(bidBalance);
        double bidAmount = bidBalance;
        double volume = bidAmount / openingPrice; // 매수량

        // 다음 캔들 시가에 매수
        BackTestOrders order = backTestOrdersRepository.save(BackTestOrders.bidOf(targetCandle.getMarket(), reason, openingPrice, volume, fee, targetCandle.getTimestamp(), rsIs));

        wallet.allBid(openingPrice, bidAmount, volume, fee, targetCandle.getCandleDateTimeUtc());
        accountCoinWalletRepository.save(wallet);

        log.info("{} 매수 발생 !! ---- 매수가 {}원 / 매수 볼륨 {}", targetCandle.getMarket(), df.format(openingPrice), volume);
        return order;
    }

    @Transactional
    @Override
    public BackTestOrders ask(FiveMinutesCandle targetCandle, AccountCoinWallet wallet, Reason reason, RSIs rsIs) { // 매도
        if (wallet.isEmpty()) {
            return null;
        }
        double volume = wallet.getVolume();
        double valAmount = targetCandle.getTradePrice() * volume;
        double fee = fee(valAmount);
        double proceeds = valAmount - wallet.getAllPrice();
        double proceedRate = proceeds / wallet.getAllPrice() * 100;
        double maxProceedRate = NumberUtils.max(wallet.getMaxProceedRate(), proceedRate);

        log.info("{} 매도 발생 !! ---- 수익률 {}% 매도가 : {} / 매도 볼륨 {}, "
                , wallet.getMarket()
                , proceedRate
                , targetCandle.getTradePrice()
                , volume);

        BackTestOrders order = backTestOrdersRepository.save(BackTestOrders.askOf(
                wallet.getMarket(),
                reason,
                targetCandle.getOpeningPrice(),
                volume, fee, targetCandle.getTimestamp(),
                proceeds,
                proceedRate,
                maxProceedRate,
                rsIs));

        // 매도 -> 다음 캔들 시가에 매도
        wallet.allAsk(valAmount, fee);
        accountCoinWalletRepository.save(wallet);
        return order;
    }

    @Transactional
    @Override
    public BackTestOrders ask(FiveMinutesCandle targetCandle, WalletList walletList, Reason reason, RSIs rsIs) {
        if (walletList.isEmpty()) {
            return null;
        }
        double volume = walletList.getVolume();
        double valAmount = targetCandle.getTradePrice() * volume;
        double fee = fee(valAmount);
        Double allPrice = walletList.getAllPrice();
        double proceeds = valAmount - allPrice;
        double proceedRate = proceeds / allPrice * 100;
        double maxProceedRate = walletList.getProceedRate(); // 구할 수 없다.

        log.info("{} 매도 발생 !! ---- 수익률 {}% 매도가 : {} / 매도 볼륨 {}, "
                , walletList.getMarket()
                , proceedRate
                , targetCandle.getTradePrice()
                , volume);

        BackTestOrders order = backTestOrdersRepository.save(BackTestOrders.askOf(
                walletList.getMarket(),
                reason,
                targetCandle.getOpeningPrice(),
                volume, fee, targetCandle.getTimestamp(),
                proceeds,
                proceedRate,
                maxProceedRate,
                rsIs));

        // 매도 -> 다음 캔들 시가에 매도
        List<AccountCoinWallet> bidWallets = walletList.getBidWallets();
        bidWallets.forEach(wallet -> {
            wallet.allAsk(valAmount, fee);
        });
        accountCoinWalletRepository.saveAll(bidWallets);
        return order;
    }

    @Override
    public Integer getOrderCount() {
        return (int) backTestOrdersRepository.count();
    }
}
