package com.example.democoin.upbit.db.entity;

import com.example.democoin.utils.NumberUtils;

public interface Candle {

    Double getTradePrice();

    Double getOpeningPrice();

    Double getLowPrice();

    Double getHighPrice();

    default boolean isBetween(Candle before) {
        return NumberUtils.between(before.getTradePrice(), before.getOpeningPrice(), this.getTradePrice());
    }
}
