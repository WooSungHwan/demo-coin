package com.example.democoin.upbit.enums;

import com.example.democoin.configuration.enums.EnumInterface;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SiseType implements EnumInterface {

    TICKER("ticker", "현재가"),
    TRADE("trade", "체결"),
    ORDERBOOK("orderbook", "호가");

    private String type;
    private String name;

    public static SiseType find(String type) {
        return EnumInterface.find(type, values());
    }

    @JsonCreator
    public static SiseType findToNull(String type) {
        return EnumInterface.findToNull(type, values());
    }
}
