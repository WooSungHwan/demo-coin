package com.example.democoin.backtest.strategy.ask;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum AskStrategy {

    STRATEGY_1("볼린저밴드 상단 돌파, RSI14 65 이상"),
    STRATEGY_2("볼린저밴드 확장 꺽임"),
    STRATEGY_3("5분봉3틱수익손절매(-2% 손절, +2% 익절)"),
    STRATEGY_4("볼린저밴드 확장 꺽임, RSI 65 이상");

    private String strategy;

}
