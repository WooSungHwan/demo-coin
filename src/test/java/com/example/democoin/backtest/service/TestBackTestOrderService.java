package com.example.democoin.backtest.service;

import com.example.democoin.backtest.common.AccountCoinWallet;
import com.example.democoin.backtest.common.AccountCoinWalletRepository;
import com.example.democoin.backtest.common.BackTestOrders;
import com.example.democoin.backtest.common.BackTestOrdersRepository;
import com.example.democoin.backtest.service.fixture.AccountCoinWalletFixture;
import com.example.democoin.backtest.service.fixture.BackTestOrdersFixture;
import com.example.democoin.backtest.service.fixture.FiveMinutesCandleFixture;
import com.example.democoin.upbit.db.entity.FiveMinutesCandle;
import com.example.democoin.upbit.enums.OrdSideType;
import com.example.democoin.upbit.service.FiveMinutesCandleService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import static com.example.democoin.backtest.service.fixture.AccountCoinWalletFixture.bidWallet;
import static com.example.democoin.backtest.service.fixture.AccountCoinWalletFixture.standardWallet;
import static com.example.democoin.backtest.service.fixture.BackTestOrdersFixture.bidBackTestOrders;
import static com.example.democoin.backtest.service.fixture.BackTestOrdersFixture.standardBackTestOrders;
import static com.example.democoin.backtest.service.fixture.FiveMinutesCandleFixture.standardFiveMinutesCandle;
import static com.example.democoin.upbit.enums.OrdSideType.BID;
import static com.example.democoin.utils.IndicatorUtil.fee;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
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

}