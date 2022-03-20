package com.example.democoin.backtest.repository;

import com.example.democoin.backtest.entity.AccountCoinWallet;
import com.example.democoin.upbit.enums.MarketType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface AccountCoinWalletRepository extends JpaRepository<AccountCoinWallet, Long> {

    AccountCoinWallet findByMarket(@Param("market") MarketType market);

}
