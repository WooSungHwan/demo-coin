package com.example.democoin;

import com.example.democoin.configuration.enums.EnumInterface;
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
}
