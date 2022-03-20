package com.example.democoin.backtest.service;

import com.example.democoin.backtest.entity.AccountCoinWallet;
import com.example.democoin.backtest.repository.AccountCoinWalletRepository;
import com.example.democoin.upbit.enums.MarketType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
@Service
public class AccountCoinWalletServiceImpl implements AccountCoinWalletService {

    private final AccountCoinWalletRepository accountCoinWalletRepository;

    @Override
    public boolean isAskable(AccountCoinWallet wallet) {
        if (Objects.nonNull(wallet.getVolume())) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isBidable(AccountCoinWallet wallet) {
        if (wallet.isEmpty()) {
            return true;
        }
        return false;
    }

    @Transactional
    @Override
    public AccountCoinWallet fetchWallet(MarketType market, Double tradePrice) {
        AccountCoinWallet wallet = accountCoinWalletRepository.findByMarket(market);
        if (!wallet.isEmpty()) {
            wallet.fetch(tradePrice);
            return accountCoinWalletRepository.save(wallet);
        }
        return wallet;
    }

    @Override
    public AccountCoinWallet getWalletByMarket(MarketType market) {
        return accountCoinWalletRepository.findByMarket(market);
    }
}
