package com.example.democoin.backtest.service.fixture;

import com.example.democoin.backtest.entity.AccountCoinWallet;
import com.example.democoin.upbit.enums.MarketType;

public class AccountCoinWalletFixture {

    public static AccountCoinWallet standardWallet() {
        return AccountCoinWallet.builder()
                .id(1L)
                .allPrice(1000d)
                .balance(0d)
                .market(MarketType.KRW_BTC)
                .avgPrice(10000d)
                .maxProceedRate(1.2d)
                .proceeds(10d)
                .valAmount(1010d)
                .proceedRate(1.2d)
                .volume(10d)
                .build();
    }

    public static AccountCoinWallet bidWallet() {
        return AccountCoinWallet.builder()
                .id(1L)
                .balance(1000d)
                .market(MarketType.KRW_BTC)
                .build();
    }

    public static AccountCoinWallet askWallet() {
        return AccountCoinWallet.builder()
                .id(1L)
                .allPrice(1000d)
                .balance(0d)
                .market(MarketType.KRW_BTC)
                .avgPrice(10000d)
                .maxProceedRate(1.2d)
                .proceeds(10d)
                .valAmount(1010d)
                .proceedRate(1.2d)
                .volume(10d)
                .build();
    }

    public static AccountCoinWallet emptyWallet() {
        return AccountCoinWallet.builder()
                .id(1L)
                .balance(1000d)
                .market(MarketType.KRW_BTC)
                .build();
    }

}
