package com.example.democoin.utils;

import java.util.Objects;

public class NumberUtils {

    private NumberUtils() {}

    public static Double max(Double value1, Double value2) {
        if (Objects.isNull(value1) && Objects.isNull(value2)) {
            return null;
        }
        if (Objects.nonNull(value1) && Objects.nonNull(value2)) {
            return org.apache.commons.lang3.math.NumberUtils.max(value1, value2);
        }
        if (Objects.isNull(value1)) {
            return value2;
        } else {
            return value1;
        }
    }

    public static boolean between(Double area1, Double area2, Double value) {
        if (area1 == area2 && area2 == value) {
            return true;
        } else if (area1 > area2) {
            return area1 >= value && value >= area2;
        } else {
            return area2 >= value && value >= area1;
        }
    }

}
