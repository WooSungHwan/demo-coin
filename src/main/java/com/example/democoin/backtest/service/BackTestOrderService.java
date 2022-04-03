package com.example.democoin.backtest.service;

import com.example.democoin.backtest.WalletList;
import com.example.democoin.backtest.entity.AccountCoinWallet;
import com.example.democoin.backtest.entity.BackTestOrders;
import com.example.democoin.configuration.enums.Reason;
import com.example.democoin.upbit.db.entity.FiveMinutesCandle;

public interface BackTestOrderService {

    BackTestOrders bid(FiveMinutesCandle targetCandle, AccountCoinWallet wallet, Reason reason);

    BackTestOrders ask(FiveMinutesCandle targetCandle, AccountCoinWallet wallet, Reason reason);

    BackTestOrders ask(FiveMinutesCandle targetCandle, WalletList walletList, Reason reason);

}
