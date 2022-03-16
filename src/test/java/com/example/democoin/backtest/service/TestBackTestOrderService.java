package com.example.democoin.backtest.service;

import com.example.democoin.backtest.common.AccountCoinWallet;
import com.example.democoin.backtest.common.AccountCoinWalletRepository;
import com.example.democoin.backtest.common.BackTestOrders;
import com.example.democoin.backtest.common.BackTestOrdersRepository;
import com.example.democoin.upbit.db.entity.FiveMinutesCandle;
import com.example.democoin.utils.NumberUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static com.example.democoin.backtest.service.fixture.AccountCoinWalletFixture.askWallet;
import static com.example.democoin.backtest.service.fixture.AccountCoinWalletFixture.bidWallet;
import static com.example.democoin.backtest.service.fixture.BackTestOrdersFixture.askBackTestOrders;
import static com.example.democoin.backtest.service.fixture.BackTestOrdersFixture.bidBackTestOrders;
import static com.example.democoin.backtest.service.fixture.FiveMinutesCandleFixture.standardFiveMinutesCandle;
import static com.example.democoin.utils.IndicatorUtil.fee;
import static java.math.RoundingMode.HALF_UP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
class TestBackTestOrderService {

    @Mock
    private BackTestOrdersRepository backTestOrdersRepository;

    @Mock
    private AccountCoinWalletRepository accountCoinWalletRepository;

    private BackTestOrderService backTestOrderService;

    @BeforeEach
    void setUp() {
        backTestOrderService = new BackTestOrderServiceImpl(backTestOrdersRepository, accountCoinWalletRepository);
    }

    @Test
    void bidTest() {
        // given
        AccountCoinWallet wallet = bidWallet();
        FiveMinutesCandle targetCandle = standardFiveMinutesCandle();
        double openingPrice = targetCandle.getTradePrice();
        double fee = fee(wallet.getBalance());
        double bidAmountWithFee = wallet.getBalance() - fee;
        double volume = bidAmountWithFee / openingPrice;

        BackTestOrders backTestOrders = bidBackTestOrders(targetCandle, fee, volume);

        // when
        when(backTestOrdersRepository.save(any())).thenReturn(backTestOrders);

        // then
        BackTestOrders result = backTestOrderService.bid(targetCandle, wallet);

        assertThat(result).isEqualTo(backTestOrders);
    }

    @Test
    void askTest() {
        // given
        AccountCoinWallet wallet = askWallet();
        FiveMinutesCandle targetCandle = standardFiveMinutesCandle();

        double volume = wallet.getVolume();
        double valAmount = BigDecimal.valueOf(targetCandle.getTradePrice() * volume).setScale(2, HALF_UP).doubleValue();
        double fee = fee(valAmount);
        double proceeds = BigDecimal.valueOf(valAmount - wallet.getAllPrice()).setScale(2, HALF_UP).doubleValue();
        double proceedRate = BigDecimal.valueOf(proceeds / wallet.getAllPrice() * 100).setScale(2, HALF_UP).doubleValue();
        double maxProceedRate = NumberUtils.max(wallet.getMaxProceedRate(), proceedRate);

        BackTestOrders backTestOrders = askBackTestOrders(targetCandle, fee, volume, proceeds, proceedRate, maxProceedRate);

        // when
        when(backTestOrdersRepository.save(any())).thenReturn(backTestOrders);

        // then
        BackTestOrders result = backTestOrderService.ask(targetCandle, wallet);

        assertThat(result).isEqualTo(backTestOrders);
    }

}