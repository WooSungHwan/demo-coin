package com.example.democoin.upbit.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MinuteType {
    ONE(1),
    THREE(3),
    FIVE(5),
    FIFTEEN(15),
    THIRTEEN(30),
    HOUR(60);

    private int minute;
}
