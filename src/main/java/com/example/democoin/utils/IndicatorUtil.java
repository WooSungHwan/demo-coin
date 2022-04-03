package com.example.democoin.utils;

import com.example.democoin.indicator.raw.RSI;
import com.example.democoin.indicator.result.BollingerBands;
import com.example.democoin.indicator.result.RSIs;
import com.example.democoin.upbit.db.entity.FiveMinutesCandle;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

import static java.math.RoundingMode.HALF_EVEN;

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
            prices20.add(new BigDecimal(average.getAsDouble()).setScale(4, HALF_EVEN));
        }
        return prices20;
    }

    /**
     * <pre>
     *     n일 동안의 표준편차 리스트
     * </pre>
     * @param day
     * @param values
     * @return
     */
    public static List<Double> stdevList(int day, List<Double> values) {
        List<Double> stdevList = new ArrayList<>();
        for (int i = 0; i < values.size(); i++) {
            int fromIndex = i;
            int toIndex = i + day;
            if (toIndex > values.size()) {
                break;
            }
            stdevList.add(stdev(values.subList(fromIndex, toIndex)));
        }
        return stdevList;
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

    public static double getCCI(List<FiveMinutesCandle> candles, int day) {
        /*
        M = ( H + L + C ) / 3
        H : 고가, L : 저가, C : 종가, M : 평균가격(mean price)

        SM = M의 n일 합계 / n
        단, N은 일반적으로 20일을 기본값으로 제공함.

        D = ( M – SM )의 N일 합계 / N
        M : 평균가격, SM : n기간 단순 이동평균, D : 평균편차(mean deviation)

        CCI = ( M – SM ) / (0.015 * D )
         */
        double nowM = getMeanPrice(candles.get(0));
        double nowSM = getSimpleMean(candles.subList(0, day));
        List<Double> dList = new ArrayList<>();
        for (int i = 0; i < day; i++) {
            double m = getMeanPrice(candles.get(i));
            int fromIndex = i;
            int toIndex = i + day;
            double sm = getSimpleMean(candles.subList(fromIndex, toIndex));
            dList.add(Math.abs(m - sm));
        }
        double nowD = getSMAList(20, dList).get(0).doubleValue();
        return (nowM - nowSM) / (0.015 * nowD);
    }

    private static double getSimpleMean(List<FiveMinutesCandle> candles) {
        return candles.stream()
                .mapToDouble(IndicatorUtil::getMeanPrice)
                .average()
                .getAsDouble();
    }

    private static double getMeanPrice(double highPrice, double lowPrice, double tradePrice) {
        return (highPrice + lowPrice + tradePrice) / 3;
    }

    private static double getMeanPrice(FiveMinutesCandle candle) {
        return getMeanPrice(candle.getHighPrice(), candle.getLowPrice(), candle.getTradePrice()) / 3;
    }

    public static double fee(double price) {
        return price * 0.0005;
    }
}
