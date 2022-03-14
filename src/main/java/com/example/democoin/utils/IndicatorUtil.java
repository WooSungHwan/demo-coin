package com.example.democoin.utils;

import com.example.democoin.indicator.raw.RSI;
import com.example.democoin.indicator.result.BollingerBands;
import com.example.democoin.indicator.result.RSIs;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

import static java.math.RoundingMode.HALF_UP;

public class IndicatorUtil {

    /**
     * 볼린져밴드
     * @param prices
     * @return
     */
    public static BollingerBands getBollingerBands(List<Double> prices) {
        List<BigDecimal> mdd = getSMAList(20, prices);

        List<BigDecimal> udd = new ArrayList<>();
        List<BigDecimal> ldd = new ArrayList<>();
        for (int i = 0; i < mdd.size(); i++) {
            List<Double> priceSubList = prices.subList(i, i + 20);
            if (priceSubList.size() != 20) {
                break;
            }
            double stdev = stdev(priceSubList);
            udd.add(mdd.get(i).add(BigDecimal.valueOf((stdev * 2))));
            ldd.add(mdd.get(i).subtract(BigDecimal.valueOf((stdev * 2))));
        }
        return BollingerBands.of(udd, mdd, ldd);
    }

    /**
     * 이동평균선
     * @param day
     * @param prices
     * @return
     */
    public static List<BigDecimal> getSMAList(int day, List<Double> prices) {
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

    public static double fee(double price) {
        return price * 0.0005;
    }
}
