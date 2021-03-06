package com.example.democoin.backtest.service;

import com.example.democoin.backtest.WalletList;
import com.example.democoin.backtest.entity.AccountCoinWallet;
import com.example.democoin.backtest.repository.AccountCoinWalletRepository;
import com.example.democoin.upbit.enums.MarketType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

import static com.example.democoin.backtest.BackTest2.BID_SLOT;

@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
@Service
public class AccountCoinWalletServiceImpl implements AccountCoinWalletService {

    private final AccountCoinWalletRepository accountCoinWalletRepository;

    @Override
    public boolean isAskable(WalletList walletResult) {
        if (walletResult.isNotEmpty()) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isBidable(WalletList walletResult) {
        if (walletResult.hasSlot()) {
            return true;
        }
        return false;
    }

    @Transactional
    @Override
    public List<AccountCoinWallet> fetchWallet(MarketType market, Double tradePrice) {
        List<AccountCoinWallet> wallets = accountCoinWalletRepository.findByMarket(market);
        wallets.forEach(wallet -> {
            if (!wallet.isEmpty()) {
                wallet.fetch(tradePrice);
            }
        });
        return wallets;
    }

    @Override
    public List<AccountCoinWallet> getWalletByMarket(MarketType market) {
        return accountCoinWalletRepository.findByMarket(market);
    }

    @Transactional
    @Override
    public void rebalancing(MarketType market) {
        List<AccountCoinWallet> wallets = accountCoinWalletRepository.findByMarket(market);
        List<AccountCoinWallet> rebalancingWallets = wallets.stream()
                .filter(AccountCoinWallet::isEmpty)
                .toList();

        if (CollectionUtils.isEmpty(rebalancingWallets) || rebalancingWallets.size() == 1) {
            return;
        }

        double balance = rebalancingWallets.stream().mapToDouble(AccountCoinWallet::getBalance).sum() / rebalancingWallets.size();
        rebalancingWallets.forEach(wallet -> wallet.rebalance(balance));
    }

    @Transactional
    @Override
    public void addMonthlyAmount(double monthlyAddAmount) {
        for (MarketType market : MarketType.marketTypeList) {
            List<AccountCoinWallet> wallets = accountCoinWalletRepository.findByMarket(market);
            wallets.forEach(wallet -> wallet.addAmount(monthlyAddAmount * market.getPercent() / BID_SLOT));
        }
    }
}
