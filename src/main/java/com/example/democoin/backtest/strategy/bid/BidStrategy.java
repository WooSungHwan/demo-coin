package com.example.democoin.backtest.strategy.bid;

import lombok.AllArgsConstructor;

// 매수 전략
@AllArgsConstructor
public enum BidStrategy {
    STRATEGY_1("볼린저밴드 하단 돌파 / RSI14 35 이하"),
    STRATEGY_2("볼린저밴드 5개봉 수축"),
    STRATEGY_3("5분봉3틱하락"),
    STRATEGY_4("볼린저밴드 5개봉 수축 / 20 이평 이상"),
    STRATEGY_5("rsi14 35 이하 / 최근 10개봉 1퍼센트 이상 음봉 3개 발생"),
    STRATEGY_6("5일 이동평균선 10일 이동평균선 골든크로스 / 20 이평 이상"),
    STRATEGY_7("rsi14 30 이하 / 볼린저 밴드 7개봉 수축 / 20 이평 이상"),
    STRATEGY_8("볼린저밴드 하단선 상향돌파 / 200 이평선 돌파 또는 rsi 30 상향 돌파"),
    STRATEGY_9("볼린저밴드 하단선 상향돌파 / 200 이평선 돌파"),
    STRATEGY_10("볼린저밴드 7개봉 수축 / 200 이평 이상 or 볼린저밴드 하단선 상향돌파 / 200 이평선 돌파"),
    STRATEGY_11("5분봉 3틱 하락, rsi 35 이하"),
    STRATEGY_12("5분봉 3틱 하락, rsi 40 이하, 볼린저 밴드 하단선 아래"),
    STRATEGY_13("5분봉 3틱 하락(개선1), rsi 30 이상 50 이하, 볼린저 밴드 하단선 아래"),
    STRATEGY_14("5분봉 3틱 하락(개선2), 볼린저 밴드 하단선 아래, 15분봉 rsi 40 이하"),
    STRATEGY_15("5분봉 3틱 하락(개선1)"),
    STRATEGY_16("5분봉 3틱 하락(개선2)"),
    STRATEGY_17("5분봉 3틱 하락(개선3)")
    ;

    private String strategy;
}
