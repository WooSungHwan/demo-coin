package com.example.democoin;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrdSideType {

    ASK("ask", "매도"),
    BID("bid", "매수");

    private String type;
    private String name;
}
