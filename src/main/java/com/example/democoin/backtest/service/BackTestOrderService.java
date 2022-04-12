package com.example.democoin.backtest.service;

import com.example.democoin.backtest.WalletList;
import com.example.democoin.backtest.entity.AccountCoinWallet;
import com.example.democoin.backtest.entity.BackTestOrders;
import com.example.democoin.backtest.strategy.BidSignalParams;
import com.example.democoin.configuration.enums.Reason;
import com.example.democoin.indicator.result.RSIs;
import com.example.democoin.upbit.db.entity.FiveMinutesCandle;

public interface BackTestOrderService {

    BackTestOrders bid(FiveMinutesCandle targetCandle, AccountCoinWallet wallet, Reason reason, RSIs rsIs);

    BackTestOrders ask(FiveMinutesCandle targetCandle, AccountCoinWallet wallet, Reason reason, RSIs rsIs);

    BackTestOrders ask(FiveMinutesCandle targetCandle, WalletList walletList, Reason reason, RSIs rsIs);

    Integer getOrderCount();
}
