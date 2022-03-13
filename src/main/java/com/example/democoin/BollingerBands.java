package com.example.democoin;

import com.example.democoin.upbit.db.entity.FiveMinutesCandle;
import lombok.Value;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Value(staticConstructor = "of")
public class BollingerBands {

    private List<BigDecimal> udd; // 상단

    private List<BigDecimal> mdd; // 20 이평

    private List<BigDecimal> ldd; // 하단

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

    /**
     * 볼린저 밴드 간격 줄어듬(표준편차 감소)
     * @return
     */
    public boolean isReduceFiveCandle() {
        double udd1 = udd.get(0).doubleValue();
        double udd2 = udd.get(1).doubleValue();
        double udd3 = udd.get(2).doubleValue();
        double udd4 = udd.get(3).doubleValue();
        double udd5 = udd.get(4).doubleValue();

        double ldd1 = ldd.get(0).doubleValue();
        double ldd2 = ldd.get(1).doubleValue();
        double ldd3 = ldd.get(2).doubleValue();
        double ldd4 = ldd.get(3).doubleValue();
        double ldd5 = ldd.get(4).doubleValue();

        double interval1 = udd1 - ldd1;
        double interval2 = udd2 - ldd2;
        double interval3 = udd3 - ldd3;
        double interval4 = udd4 - ldd4;
        double interval5 = udd5 - ldd5;

        if (interval5 >= interval4 && interval4 >= interval3 && interval3 >= interval2 && interval2 < interval1) {
            return true;
        }

        return false;
    }

    /**
     * 볼린저 밴드 넓이 최대인가 -> 줄어드는 순간 매도할거임.
     * @return
     */
    public boolean isBollingerBandWidthMax() {
        double udd1 = udd.get(0).doubleValue();
        double udd2 = udd.get(1).doubleValue();
        double udd3 = udd.get(2).doubleValue();
        double udd4 = udd.get(3).doubleValue();
        double udd5 = udd.get(4).doubleValue();

        double ldd1 = ldd.get(0).doubleValue();
        double ldd2 = ldd.get(1).doubleValue();
        double ldd3 = ldd.get(2).doubleValue();
        double ldd4 = ldd.get(3).doubleValue();
        double ldd5 = ldd.get(4).doubleValue();

        double interval1 = udd1 - ldd1;
        double interval2 = udd2 - ldd2;
        double interval3 = udd3 - ldd3;
        double interval4 = udd4 - ldd4;
        double interval5 = udd5 - ldd5;

        if (interval5 < interval4 && interval4 < interval3 && interval3 < interval2 && interval2 >= interval1) {
            return true;
        }
        return false;
    }

    public boolean isLddChangeUp() {
        double ldd1 = ldd.get(0).doubleValue();
        double ldd2 = ldd.get(1).doubleValue();
        double ldd3 = ldd.get(2).doubleValue();
        double ldd4 = ldd.get(3).doubleValue();
        double ldd5 = ldd.get(4).doubleValue();

        if (ldd5 >= ldd4 && ldd4 >= ldd3 && ldd3 >= ldd2 && ldd2 < ldd1) {
            return true;
        }
        return false;
    }
}
