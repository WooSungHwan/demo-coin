package com.example.democoin.upbit.enums;

import com.example.democoin.configuration.enums.EnumInterface;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderStateType implements EnumInterface {

    WAIT("wait", "체결 대기"), // default
    WATCH("watch", "예약주문 대기"),
    DONE("done", "전체 체결 완료"),
    CANCEL("cancel", "주문 취소");

    private String type;
    private String name;

    public static OrderStateType find(String type) {
        return EnumInterface.find(type, values());
    }

    @JsonCreator
    public static OrderStateType findToNull(String type) {
        return EnumInterface.findToNull(type, values());
    }
}
