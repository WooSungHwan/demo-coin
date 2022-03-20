package com.example.democoin.backtest.strategy.ask;

import lombok.AllArgsConstructor;

// 매도
@AllArgsConstructor
public enum AskStrategy {

    STRATEGY_1("볼린저밴드 상단 돌파, RSI14 65 이상"),
    STRATEGY_2("볼린저밴드 확장 꺽임"),
    STRATEGY_3("단순 5분봉3틱수익손절매(-2% 손절, +2% 익절)"),
    STRATEGY_4("볼린저밴드 확장 꺽임, RSI 65 이상"),
    STRATEGY_5("rsi 65 이상"),
    STRATEGY_6("볼린저밴드 하단선 확장 감소 추세"),
    STRATEGY_7("5일 이동평균선 10일 이동평균선 데드크로스"),
    STRATEGY_8("볼린저밴드 하단선 확장 감소 추세 또는 rsi 70 이상"),
    STRATEGY_9("볼린저 밴드 상한선 하향돌파 또는 rsi 70 하향돌파"),
    STRATEGY_10("5분봉 3틱룰 (틱계산 : 0.")
    ;

    private String strategy;

}
