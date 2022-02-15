package com.example.democoin;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrdType {
    PRICE("price", "시장가매수"),
    MARKET("market", "시장가매도"), // 매도용
    LIMIT("limit", "지정가주문"); // 매수용

    private String type;
    private String name;

}
