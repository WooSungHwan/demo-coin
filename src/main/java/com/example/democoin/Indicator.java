package com.example.democoin;

import com.example.democoin.indicator.RSI;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

import static java.math.RoundingMode.HALF_UP;

public class Indicator {

    /**
     * 볼린져밴드
     * @param prices
     * @return
     */
    public static BollingerBands getBollingerBands(List<Double> prices) {
        List<BigDecimal> mdd = getSMAList(20, prices);
        double stdev = stdev(prices.subList(0, 20));
        List<BigDecimal> udd = mdd.stream().map(value -> BigDecimal.valueOf(value.doubleValue() + (stdev * 2))).collect(Collectors.toList());
        List<BigDecimal> ldd = mdd.stream().map(value -> BigDecimal.valueOf(value.doubleValue() - (stdev * 2))).collect(Collectors.toList());

        return BollingerBands.of(udd, mdd, ldd);
    }

    /**
     * 이동평균선
     * @param day
     * @param prices
     * @return
     */
    private static List<BigDecimal> getSMAList(int day, List<Double> prices) {
        List<BigDecimal> prices20 = new ArrayList<>();
        for (int i = 0; i < prices.size(); i++) {
            int fromIndex = i;
            int toIndex = i + day;
            if (toIndex > prices.size()) {
                break;
            }
            ArrayList<Double> priceList = new ArrayList<>(prices.subList(fromIndex, toIndex));
            OptionalDouble average = priceList.stream().mapToDouble(Double::doubleValue).average();
            prices20.add(new BigDecimal(average.getAsDouble()).setScale(4, HALF_UP));
        }
        return prices20;
    }

    /**
     * 표준편차
     * @param values
     * @return
     */
    public static double stdev(List<Double> values) {
        SummaryStatistics statistics = new SummaryStatistics();
        for (Double value : values) {
            statistics.addValue(value);
        }
        return statistics.getStandardDeviation();
    }

    /**
     * RSI 14
     * @param prices
     * @return
     */
    public static RSIs getRSI14(List<Double> prices) {
        RSI rsi = new RSI(14);
        double[] rawDoubles = prices.stream().mapToDouble(p -> p).toArray();

        return RSIs.of(Arrays.stream(rsi.count(rawDoubles))
                .boxed()
                .collect(Collectors.toUnmodifiableList()));
    }
}
