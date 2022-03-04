package com.example.democoin.backtest.common;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface AccountCoinWalletRepository extends JpaRepository<AccountCoinWallet, Long> {

    AccountCoinWallet findByMarket(@Param("market") String market);

}
