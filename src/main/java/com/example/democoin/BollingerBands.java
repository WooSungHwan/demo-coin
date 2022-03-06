package com.example.democoin;

import com.example.democoin.upbit.db.entity.FiveMinutesCandle;
import lombok.Value;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Value(staticConstructor = "of")
public class BollingerBands {

    private List<BigDecimal> udd;

    private List<BigDecimal> mdd;

    private List<BigDecimal> ldd;

    public boolean isBollingerBandUddTouch(FiveMinutesCandle candle) {
        if (Objects.isNull(candle)) {
            return false;
        }
        Double highPrice = candle.getHighPrice();
        double uddValue = udd.get(0).doubleValue();
        return highPrice >= uddValue;
    }

    public boolean isBollingerBandLddTouch(FiveMinutesCandle candle) {
        if (Objects.isNull(candle)) {
            return false;
        }
        Double lowPrice = candle.getLowPrice();
        double uddValue = ldd.get(0).doubleValue();
        return uddValue >= lowPrice;
    }

}
