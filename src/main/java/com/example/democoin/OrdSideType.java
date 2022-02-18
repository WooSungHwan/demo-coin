package com.example.democoin;

import com.example.democoin.configuration.enums.EnumInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrdSideType implements EnumInterface {

    ASK("ask", "매도"),
    BID("bid", "매수");

    private String type;
    private String name;
}
