package com.example.democoin.upbit.enums;

import com.example.democoin.EnumInterfaceConverter;
import com.example.democoin.configuration.enums.EnumInterface;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@Getter
@AllArgsConstructor
public enum MarketType implements EnumInterface {

    KRW_BTC("KRW-BTC", "원화 비트코인", 0.4),
    KRW_ETH("KRW-ETH", "원화 이더리움", 0.3),
    KRW_XRP("KRW-XRP", "원화 리플", 0.3);

    private String type;
    private String name;
    private double percent; // 운용 금액 비중

    public static final List<MarketType> marketTypeList = List.of(KRW_BTC, KRW_ETH, KRW_XRP);

    public static MarketType find(String type) {
        return EnumInterface.find(type, values());
    }

    @JsonCreator
    public static MarketType findToNull(String type) {
        return EnumInterface.findToNull(type, values());
    }

    public boolean contains(MarketUnit marketUnit) {
        return StringUtils.contains(this.type, marketUnit.getType());
    }

    @javax.persistence.Converter(autoApply = true)
    public static class Converter extends EnumInterfaceConverter<MarketType> {
        public Converter() {
            super(MarketType.class);
        }
    }
}
