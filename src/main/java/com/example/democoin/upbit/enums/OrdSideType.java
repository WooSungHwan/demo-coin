package com.example.democoin.upbit.enums;

import com.example.democoin.EnumInterfaceConverter;
import com.example.democoin.configuration.enums.EnumInterface;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

@Getter
@AllArgsConstructor
public enum OrdSideType implements EnumInterface {

    ASK("ask", "매도"),
    BID("bid", "매수");

    private String type;
    private String name;

    public static OrdSideType find(String type) {
        return EnumInterface.find(type, values());
    }

    @JsonCreator
    public static OrdSideType findToNull(String type) {
        return EnumInterface.findToNull(type, values());
    }

    @javax.persistence.Converter(autoApply = true)
    public static class Converter extends EnumInterfaceConverter<OrdSideType> {
        public Converter() {
            super(OrdSideType.class);
        }
    }
}
