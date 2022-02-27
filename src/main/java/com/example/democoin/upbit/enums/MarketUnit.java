package com.example.democoin.upbit.enums;

import com.example.democoin.configuration.enums.EnumInterface;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.codec.binary.StringUtils;

@Getter
@AllArgsConstructor
public enum MarketUnit implements EnumInterface {

    KRW("KRW", "원화"),
    BTC("BTC", "비트코인"),
    USDT("USDT", "USDT");

    private String type;
    private String name;

    public boolean matched(String market) {
        return StringUtils.equals(type, market);
    }

    public static MarketUnit find(String type) {
        return EnumInterface.find(type, values());
    }

    @JsonCreator
    public static MarketUnit findToNull(String type) {
        return EnumInterface.findToNull(type, values());
    }
}
