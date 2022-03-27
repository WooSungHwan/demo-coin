package com.example.democoin.backtest.strategy.bid;

import com.example.democoin.configuration.enums.Reason;
import com.example.democoin.configuration.enums.EnumInterface;
import com.example.democoin.configuration.enums.EnumInterfaceConverter;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public enum BidReason implements Reason {

    NO_BID("NO_BID", "매수안함"),
    RSI_UNDER_AND_BB_LDD_TOUCH("RSI_UNDER_AND_BB_LDD_TOUCH", "RSI 지수 이하, 볼린저 밴드 하단선 터치"),
    BB_REDUCE_FIVE_CANDLES("BB_REDUCE_FIVE_CANDLES", "볼린저 밴드 5개봉 연속 수축"),
    BB_REDUCE_SEVEN_CANDLES("BB_REDUCE_FIVE_CANDLES", "볼린저 밴드 7개봉 연속 수축"),
    THREE_NEGATIVE_CANDLE_APPEAR("THREE_NEGATIVE_CANDLE_APPEAR", "음봉 3개 연속 출현"),
    BB_REDUCE_FIVE_CANDLES_AND_OVER_SMA20("BB_REDUCE_FIVE_CANDLES_AND_OVER_SMA20", "볼린저 밴드 5개봉 연속, 20이평 이상"),
    BB_REDUCE_SEVEN_CANDLES_AND_OVER_SMA20("BB_REDUCE_SEVEN_CANDLES_AND_OVER_SMA20", "볼린저 밴드 7개봉 연속, 20이평 이상"),
    BB_REDUCE_SEVEN_CANDLES_AND_OVER_SMA200("BB_REDUCE_SEVEN_CANDLES_AND_OVER_SMA200", "볼린저 밴드 7개봉 연속, 200이평 이상"),
    RSI_UNDER_AND_RECENT_NEGATIVE_CANDLE_COUNT_OVER_THREE("RSI_UNDER_AND_RECENT_NEGATIVE_CANDLE_COUNT_OVER_THREE", "RSI 지수 이하, 최근 음봉 캔들 3개 이상"),
    SMA5_SMA10_GOLDEN_CROSS("SMA5_SMA10_GOLDEN_CROSS", "5 이평선과 10 이평선의 골든크로스"),
    RSI_UNDER("RSI_UNDER", "RSI 지수 이하"),
    SMA200_OVERING_AND_BB_LDD_OVERING("SMA200_OVERING_AND_BB_LDD_OVERING", "200 이평선 상향돌파, 볼린저 밴드 하단선 상향돌파"),
    RSI_OVERING("RSI_OVERING", "RSI 지수 상향돌파"),
    THREE_NEGATIVE_CANDLE_APPEAR_AND_RSI_UNDER("THREE_NEGATIVE_CANDLE_APPEAR_AND_RSI_UNDER", "음봉 연속 3회 출현, RSI 지수 이하"),
    THREE_NEGATIVE_CANDLE_APPEAR_AND_RSI_UNDER_AND_BB_LDD_TOUCH("THREE_NEGATIVE_CANDLE_APPEAR_AND_RSI_UNDER_AND_BB_LDD_TOUCH", "음봉 3개 연속출현, RSI 지수 이하, 볼린저 밴드 하단 이하"),
    FIVE_THREE_TICK_BB_LDD_TOUCH_FIF_RSI_UNDER("FIVE_THREE_TICK_BB_LDD_TOUCH_FIF_RSI_UNDER", "5분봉 3틱룰(개선2), 볼밴 하단선 터치, 15분봉 rsi 지수 이하")
    ;

    private String type;
    private String name;

    public static final List<BidReason> NO_BID_GROUP = List.of(NO_BID);

    public boolean isBid() {
        return !NO_BID_GROUP.contains(this);
    }

    public static BidReason find(String type) {
        return EnumInterface.find(type, values());
    }

    @JsonCreator
    public static BidReason findToNull(String type) {
        return EnumInterface.findToNull(type, values());
    }

    @javax.persistence.Converter(autoApply = true)
    public static class Converter extends EnumInterfaceConverter<BidReason> {
        public Converter() {
            super(BidReason.class);
        }
    }
}
