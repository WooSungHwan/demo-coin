package com.example.democoin.upbit.enums;

import com.example.democoin.configuration.enums.EnumInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderByType implements EnumInterface {

    ASC("asc", "오름차순"),
    DESC("desc", "내림차순");

    private String type;
    private String name;
}
