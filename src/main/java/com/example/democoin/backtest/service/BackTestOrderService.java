package com.example.democoin.backtest.service;

import com.example.democoin.backtest.common.AccountCoinWallet;
import com.example.democoin.backtest.common.BackTestOrders;
import com.example.democoin.upbit.db.entity.FiveMinutesCandle;

public interface BackTestOrderService {

    BackTestOrders bid(FiveMinutesCandle targetCandle, AccountCoinWallet wallet);

    BackTestOrders ask(FiveMinutesCandle targetCandle, AccountCoinWallet wallet);

}
