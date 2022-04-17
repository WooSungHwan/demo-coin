package com.example.democoin.backtest.service;

import com.example.democoin.backtest.WalletList;
import com.example.democoin.backtest.entity.AccountCoinWallet;
import com.example.democoin.upbit.enums.MarketType;

import java.util.List;

public interface AccountCoinWalletService {
    boolean isAskable(WalletList walletResult);

    boolean isBidable(WalletList walletResult);

    List<AccountCoinWallet> fetchWallet(MarketType market, Double tradePrice);

    List<AccountCoinWallet> getWalletByMarket(MarketType market);

    void rebalancing(MarketType market);

    void addMonthlyAmount(double monthlyAddAmount);
}
