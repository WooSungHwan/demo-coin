package com.example.democoin.backtest.strategy.ask;

import com.example.democoin.configuration.enums.Reason;
import com.example.democoin.configuration.enums.EnumInterface;
import com.example.democoin.configuration.enums.EnumInterfaceConverter;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public enum AskReason implements Reason {

    NO_ASK("NO_ASK", "매도안함"),
    BEAR_MARKET("BEAR_MARKET", "하락장 출현으로 인한 전량매도"),
    MAX_PROCEED_RATE_FALL("MAX_PROCEED_RATE_FALL", "최대 수익률 대비 하락 매도"),
    STOP_LOSS("STOP_LOSS", "손절매"),
    RSI_OVER("RSI_OVER", "RSI 지수 이상"),
    RSI_OVER_AND_BB_UDD_TOUCH("RSI_OVER_AND_BB_UDD_TOUCH", "RSI 지수 이상, 볼린저 밴드 상단 터치"),
    BB_WIDTH_MAX("BB_WIDTH_MAX", "볼린저 밴드 최대 확대"),
    LDD_CHANGE_UP("LDD_CHANGE_UP", "볼린저 밴드 하단선 확장 감소"),
    SMA5_SMA10_DEAD_CROSS("SMA5_SMA10_DEAD_CROSS", "5 이평선과 10 이평선의 데드크로스"),
    BB_UDD_UNDERING("BB_UDD_UNDERING", "볼린저 밴드 하단선 하향돌파"),
    RSI_UNDERING("RSI_UNDERING", "RSI 지수 하향돌파"),
    BB_UDD_TOUCH("BB_UDD_TOUCH", "볼린저 밴드 상단 터치")
    ;

    private String type;
    private String name;

    public static final List<AskReason> NO_ASK_GROUP = List.of(NO_ASK);

    public boolean isAsk() {
        return !NO_ASK_GROUP.contains(this);
    }

    public static AskReason find(String type) {
        return EnumInterface.find(type, values());
    }

    @JsonCreator
    public static AskReason findToNull(String type) {
        return EnumInterface.findToNull(type, values());
    }

    @javax.persistence.Converter(autoApply = true)
    public static class Converter extends EnumInterfaceConverter<AskReason> {
        public Converter() {
            super(AskReason.class);
        }
    }
}
