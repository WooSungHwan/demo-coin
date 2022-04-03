package com.example.democoin.backtest.service;

import com.example.democoin.backtest.WalletList;
import com.example.democoin.backtest.entity.AccountCoinWallet;
import com.example.democoin.backtest.repository.AccountCoinWalletRepository;
import com.example.democoin.upbit.service.CandleService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.example.democoin.backtest.service.fixture.AccountCoinWalletFixture.emptyWallet;
import static com.example.democoin.backtest.service.fixture.AccountCoinWalletFixture.standardWallet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;

@Slf4j
@ExtendWith(MockitoExtension.class)
class TestAccountCoinWalletService {

    @Mock
    private AccountCoinWalletRepository accountCoinWalletRepository;

    private AccountCoinWalletService accountCoinWalletService;

    @Autowired
    private CandleService fiveMinutesCandleService;

    @BeforeEach
    void setUp() {
        accountCoinWalletService = new AccountCoinWalletServiceImpl(accountCoinWalletRepository);
    }

    @Test
    void isAskableTest() {
        // given
        AccountCoinWallet wallet = standardWallet();

        // when
        boolean result = accountCoinWalletService.isAskable(WalletList.of(List.of(wallet)));

        // then
        assertThat(result).isTrue();
    }

    @Test
    void isBidableTest() {
        // given
        AccountCoinWallet wallet = emptyWallet();

        // when
//        boolean result = accountCoinWalletService.isBidable(WalletList.of());

        // then
//        assertThat(result).isTrue();
    }
/*
    @Test
    void fetchWalletTest() {
        // given
        AccountCoinWallet wallet = standardWallet();
        Double tradePrice = 1001d;
        wallet.fetch(tradePrice);

        // when
        when(accountCoinWalletRepository.findByMarket(eq(wallet.getMarket())))
                .thenReturn(List.of(standardWallet()));
        when(accountCoinWalletRepository.save(eq(wallet)))
                .thenReturn(wallet);

        // then
        AccountCoinWallet fetchWallet = accountCoinWalletService.fetchWallet(wallet.getMarket(), tradePrice);
        assertThat(wallet).isEqualTo(fetchWallet);
    }

    @Test
    void fetchEmptyWalletTest() {
        // given
        AccountCoinWallet wallet = emptyWallet();
        Double tradePrice = 1001d;
        wallet.fetch(tradePrice);

        // when
        when(accountCoinWalletRepository.findByMarket(eq(wallet.getMarket())))
                .thenReturn(List.of(wallet));

        // then
        AccountCoinWallet fetchWallet = accountCoinWalletService.fetchWallet(wallet.getMarket(), tradePrice);
        assertThat(wallet).isEqualTo(fetchWallet);
    }*/
}