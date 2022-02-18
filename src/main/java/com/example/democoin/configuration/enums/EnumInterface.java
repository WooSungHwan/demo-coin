package com.example.democoin.configuration.enums;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Optional;

public interface EnumInterface {

    String getType();

    String getName();

    static <T extends EnumInterface> T find(String type, T[] values) {
        T findValue = findToNull(type, values);

        Optional.ofNullable(findValue)
                .orElseThrow(() -> new RuntimeException(String.format("지원 하지 않는 형식 입니다.(형식 : %s)", type)));

        return findValue;
    }

    static <T extends EnumInterface> T findToNull(String type, T[] values) {
        if (StringUtils.isBlank(type)) {
            return null;
        }

        return Arrays.stream(values)
                .filter(value -> value.getType().equals(type))
                .findFirst()
                .orElse(null);
    }

}
