package com.example.democoin.backtest;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum OrderType {

    ALL_ASK("전액매도"),
    NORMAL_ASK("일반매도"),
    NORMAL_BID("일반매수");

    private String desc;

}
