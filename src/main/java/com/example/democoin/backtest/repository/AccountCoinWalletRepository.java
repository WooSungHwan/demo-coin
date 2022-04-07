package com.example.democoin.backtest.repository;

import com.example.democoin.backtest.entity.AccountCoinWallet;
import com.example.democoin.upbit.enums.MarketType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AccountCoinWalletRepository extends CrudRepository<AccountCoinWallet, Long> {

    List<AccountCoinWallet> findByMarket(@Param("market") MarketType market);

}
