package com.example.democoin.backtest.strategy.bid;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum BidStrategy {
    STRATEGY_1("볼린저밴드 하단 돌파 / RSI14 35 이하"),
    STRATEGY_2("볼린저밴드 5개봉 수축"),
    STRATEGY_3("5분봉3틱하락"),
    STRATEGY_4("볼린저밴드 5개봉 수축 / 20 이평 이상");

    private String strategy;
}
