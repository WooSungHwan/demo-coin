package com.example.democoin.upbit.enums;

import com.example.democoin.configuration.enums.EnumInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrdType implements EnumInterface {
    PRICE("price", "시장가매수"),
    MARKET("market", "시장가매도"), // 매도용
    LIMIT("limit", "지정가주문"); // 매수용

    private String type;
    private String name;

}
