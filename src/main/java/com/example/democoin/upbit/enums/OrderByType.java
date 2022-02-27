package com.example.democoin.upbit.enums;

import com.example.democoin.configuration.enums.EnumInterface;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderByType implements EnumInterface {

    ASC("asc", "오름차순"),
    DESC("desc", "내림차순");

    private String type;
    private String name;

    public static OrderByType find(String type) {
        return EnumInterface.find(type, values());
    }

    @JsonCreator
    public static OrderByType findToNull(String type) {
        return EnumInterface.findToNull(type, values());
    }
}
