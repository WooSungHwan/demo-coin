package com.example.democoin.backtest.service;

import com.example.democoin.backtest.common.AccountCoinWallet;
import com.example.democoin.upbit.db.entity.FiveMinutesCandle;
import com.example.democoin.upbit.enums.MarketType;

public interface AccountCoinWalletService {
    boolean isAskable(AccountCoinWallet wallet);

    boolean isBidable(AccountCoinWallet wallet);

    AccountCoinWallet fetchWallet(MarketType market, Double tradePrice);
}
