package com.example.democoin.backtest.service;

import com.example.democoin.backtest.entity.AccountCoinWallet;
import com.example.democoin.backtest.repository.AccountCoinWalletRepository;
import com.example.democoin.backtest.entity.BackTestOrders;
import com.example.democoin.upbit.db.entity.FiveMinutesCandle;
import com.example.democoin.upbit.enums.MarketType;
import com.example.democoin.upbit.service.FiveMinutesCandleService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@Transactional
@SpringBootTest
public class TestBackTestService {

    @Autowired
    private AccountCoinWalletService accountCoinWalletService;

    @Autowired
    private BackTestOrderService backTestOrderService;

    @Autowired
    private FiveMinutesCandleService fiveMinutesCandleService;

    @Autowired
    AccountCoinWalletRepository accountCoinWalletRepository;

    @Test
    void backTestService() {
        int balance = 1000000;
        MarketType marketType = MarketType.KRW_BTC;

        List<FiveMinutesCandle> candles = fiveMinutesCandleService.findFiveMinutesCandlesLimitOffset(
                marketType.getType(),
                LocalDateTime.of(2020, 1, 1, 0, 0, 0),
                5,
                0);

        AccountCoinWallet wallet = accountCoinWalletRepository.save(AccountCoinWallet.of(marketType, balance * marketType.getPercent()));
        Double walletBalance = wallet.getBalance();

        // 첫째 캔들 매수
        BackTestOrders order = backTestOrderService.bid(candles.get(0), wallet);
        AccountCoinWallet wallet1 = accountCoinWalletService.fetchWallet(marketType, candles.get(0).getTradePrice());

        double value = order.getVolume() * candles.get(0).getOpeningPrice();
        double fetchValue = wallet1.getVolume() * candles.get(0).getTradePrice();
        assertThat(fetchValue - value).isEqualTo(wallet1.getProceeds());

        double orderAmount = order.getPrice() * order.getVolume();
        assertThat(walletBalance).isEqualTo(order.getFee() + orderAmount);
        assertThat(walletBalance).isEqualTo(wallet1.getValAmount() + order.getFee());

        // 둘째 캔들 매도
        BackTestOrders order2 = backTestOrderService.ask(candles.get(1), wallet1);
        AccountCoinWallet wallet2 = accountCoinWalletService.fetchWallet(marketType, candles.get(1).getTradePrice());
        double orderAmount2 = order2.getPrice() * order2.getVolume();

        assertThat(orderAmount2).isEqualTo(orderAmount + order2.getProceeds());
        assertThat(wallet2.getBalance()).isEqualTo(orderAmount2 - order2.getFee());

        walletBalance = wallet2.getBalance();

        // 셋째 캔들 매수
        BackTestOrders order3 = backTestOrderService.bid(candles.get(2), wallet2);
        AccountCoinWallet wallet3 = accountCoinWalletService.fetchWallet(marketType, candles.get(2).getTradePrice());

        orderAmount = order3.getPrice() * order3.getVolume();
        assertThat(walletBalance).isEqualTo(order3.getFee() + orderAmount);
        assertThat(walletBalance).isEqualTo(wallet3.getValAmount() + order3.getFee());
        assertThat(wallet3.getValAmount()).isEqualTo(walletBalance + wallet3.getProceeds() - order3.getFee());

        Double beforeProceeds = wallet3.getProceeds();
        Double beforeValAmount = wallet3.getValAmount();
        // 넷째 캔들 패치
        AccountCoinWallet wallet4 = accountCoinWalletService.fetchWallet(marketType, candles.get(3).getTradePrice());

        if (candles.get(2).getTradePrice() < candles.get(3).getTradePrice()) {
            assertThat(wallet4.getProceeds()).isGreaterThan(beforeProceeds);
            assertThat(wallet4.getValAmount()).isGreaterThan(beforeValAmount);
        } else {
            assertThat(wallet4.getProceeds()).isLessThanOrEqualTo(beforeProceeds);
            assertThat(wallet4.getValAmount()).isLessThanOrEqualTo(beforeValAmount);
        }
        assertThat(wallet4.getValAmount()).isEqualTo(candles.get(3).getTradePrice() * wallet4.getVolume());
        assertThat(wallet4.getAllPrice()).isEqualTo(wallet4.getValAmount() - wallet4.getProceeds());


        // 닷째 캔들 패치
        accountCoinWalletService.fetchWallet(marketType, candles.get(4).getTradePrice());

        // 엿째 캔들 패치
        accountCoinWalletService.fetchWallet(marketType, candles.get(5).getTradePrice());

        // 일곱째 캔들 매도
    }

}
