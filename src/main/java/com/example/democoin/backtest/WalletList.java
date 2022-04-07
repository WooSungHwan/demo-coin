package com.example.democoin.backtest;

import com.example.democoin.backtest.entity.AccountCoinWallet;
import com.example.democoin.upbit.enums.MarketType;
import lombok.Value;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.example.democoin.DemoCoinApplication.df;
import static java.math.RoundingMode.HALF_EVEN;

@Value(staticConstructor = "of")
public class WalletList {

    private List<AccountCoinWallet> wallets;

    /**
     * 해당 wallet 목록이 비었는가?
     * @return
     */
    public boolean isEmpty() {
        AtomicInteger count = new AtomicInteger();
        wallets.forEach(wallet -> {
            if (wallet.isEmpty()) {
                count.getAndIncrement();
            }
        });
        return count.get() == wallets.size();
    }

    /**
     * 해당 wallet 목록이 있는가?
     * @return
     */
    public boolean isNotEmpty() {
        return !isEmpty();
    }

    private int slotCount() {
        return (int) wallets.stream()
                .filter(AccountCoinWallet::isEmpty)
                .count();
    }

    /**
     * 슬록 남아있는가?
     * @return
     */
    public boolean hasSlot() {
        return slotCount() > 0;
    }

    /**
     * 현재 wallet의 마켓정보
     * @return
     */
    public MarketType getMarket() {
        return wallets.get(0).getMarket();
    }

    /**
     * 매수평균가
     * @return
     */
    public Double getBidAvgPrice() {
        List<AccountCoinWallet> bidWallets = getBidWallets();
        if (CollectionUtils.isEmpty(bidWallets)) { // 매수한게 없으면 null
            return null;
        }
        // 총 매수가
        double bidKrwSum = bidWallets.stream()
                .mapToDouble(AccountCoinWallet::getAllPrice)
                .sum();

        // 보유수량
        double volumeSum = bidWallets.stream()
                .mapToDouble(AccountCoinWallet::getVolume)
                .sum();

        return BigDecimal.valueOf(bidKrwSum)
                .divide(BigDecimal.valueOf(volumeSum), 8, HALF_EVEN)
                .setScale(2, HALF_EVEN)
                .doubleValue();
    }

    /**
     * 평가금액 합계
     * @return
     */
    public Double getValAmount() {
        List<AccountCoinWallet> bidWallets = getBidWallets();
        return bidWallets.stream()
                .mapToDouble(AccountCoinWallet::getValAmount)
                .sum();
    }

    /**
     * 수익금
     * @return
     */
    public Double getProceeds() {
        return getBidWallets().stream()
                .mapToDouble(AccountCoinWallet::getProceeds)
                .sum();
    }

    /**
     * 총 매수 금액
     * @return
     */
    public Double getAllPrice() {
        return getBidWallets().stream()
                .mapToDouble(AccountCoinWallet::getAllPrice)
                .sum();
    }

    /**
     * 수익률
     * @return
     */
    public Double getProceedRate() {
        return getProceeds() / getAllPrice() * 100;
    }

    /**
     * 요약정보 출력
     * @return
     */
    public String getWalletSummaryInfo() {
        return String.format("[%s] 평단가 : %s, 수익률 : %s%%, 수익금 : %s, 평가금액 : %s"
                , this.getMarket().getName() // 코인 시장
                , df.format(getBidAvgPrice()) // 매수평균가
                , this.getProceedRate() // 수익률
                , df.format(this.getProceeds())
                , df.format(this.getValAmount())); // 잔고
    }

    public AccountCoinWallet getBidableWallet() {
        return wallets.stream()
                .filter(AccountCoinWallet::isEmpty)
                .findFirst()
                .orElse(null);
    }

    /**
     * 매도할때 호출하는 매도할 수 있는 지갑목록
     * @return
     */
    public List<AccountCoinWallet> getAskableWallets() {
        return getBidWallets();
    }

    public List<AccountCoinWallet> getBidWallets() {
        return wallets.stream()
                .filter(AccountCoinWallet::isNotEmpty)
                .toList();
    }

    /**
     * 매수 수량
     * @return
     */
    public double getVolume() {
        return getBidWallets().stream()
                .mapToDouble(AccountCoinWallet::getVolume)
                .sum();
    }
}
