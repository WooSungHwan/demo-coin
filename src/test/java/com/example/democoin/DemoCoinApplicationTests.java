package com.example.democoin;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.democoin.configuration.properties.UpbitProperties;
import com.example.democoin.slack.SlackMessageService;
import com.example.democoin.task.service.ScheduleService;
import com.example.democoin.upbit.client.UpbitAllMarketClient;
import com.example.democoin.upbit.client.UpbitAssetClient;
import com.example.democoin.upbit.client.UpbitCandleClient;
import com.example.democoin.upbit.client.UpbitOrderClient;
import com.example.democoin.upbit.db.entity.FiveMinutesCandle;
import com.example.democoin.upbit.db.repository.FiveMinutesCandleRepository;
import com.example.democoin.upbit.enums.OrdSideType;
import com.example.democoin.upbit.enums.OrderStateType;
import com.example.democoin.upbit.request.MarketOrderableRequest;
import com.example.democoin.upbit.request.OrderCancelRequest;
import com.example.democoin.upbit.request.OrderListRequest;
import com.example.democoin.upbit.result.accounts.AccountsResult;
import com.example.democoin.upbit.result.candles.MinuteCandle;
import com.example.democoin.upbit.result.market.MarketResult;
import com.example.democoin.upbit.result.orders.MarketOrderableResult;
import com.example.democoin.upbit.result.orders.OrderCancelResult;
import com.example.democoin.upbit.result.orders.OrderResult;
import com.example.democoin.utils.JsonUtil;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.democoin.upbit.enums.MarketUnit.KRW;
import static com.example.democoin.upbit.enums.OrdSideType.ASK;
import static com.example.democoin.upbit.enums.OrdSideType.BID;
import static java.math.RoundingMode.HALF_UP;

@SpringBootTest
class DemoCoinApplicationTests {

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private UpbitProperties upbitProperties;

    @Autowired
    private UpbitAssetClient upbitAssetClient;

    @Autowired
    private UpbitOrderClient upbitOrderClient;

    @Autowired
    private UpbitAllMarketClient upbitAllMarketClient;

    @Autowired
    private UpbitCandleClient upbitCandleClient;

    @Autowired
    private FiveMinutesCandleRepository fiveMinutesCandleRepository;

    String accessKey;
    String secretKey;
    String serverUrl;

    @BeforeEach
    void setup() {
        accessKey = upbitProperties.getAccessKey();
        secretKey = upbitProperties.getSecretKey();
        serverUrl = upbitProperties.getServerUrl();
    }

    @Autowired
    private SlackMessageService slackMessageService;

//    @Transactional
    @Test
    void contextLoads() throws Exception {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
//        List<AccountsResult> accountsResults = 전체계좌조회();
//        MarketOrderableResult marketOrderableResult = 주문가능정보();
//        System.out.println(JsonUtil.toJson(marketOrderableResult));
//        개별주문조회();
//        전체종목조회();
//        주문예제();
//        List<OrderResult> orderList = 주문목록조회();
//        주문취소(orderList.get(0).getUuid());
        오늘_가장최근수집된일자_수집();
//        오늘_최초캔들생성일자_수집();
/*
        double[] bitcoins = {4600000}; //fiveMinutesCandleRepository.findFiveMinutesCandlesByLimit(500);
        RSI rsi = new RSI(14);
        double[] count = rsi.count(bitcoins); //
        System.out.println();
*/
/*
    5분봉 수집 스케줄
        - 매 0분, 5분 마다 5분봉 하나를 수집한다.
    매수 스케줄 조건
        - 한 종목을 전체 금액의 20% 이상 매수 금지
        - RSI14, 볼린져밴드, 5분봉 3틱 하락
    매도 스케줄
        - -2% 손실의 경우 매도, 3% 수익 매도 -> 분할매수, 분할매도 고려는 나중에. 로직에 고려는 할것.
        - RSI14, 볼린져밴드

    각 스케쥴마다 슬랙 알림톡 생성 후 알림 발송.
    1. 2022-01-01 12:00:00 5분봉 수집완료
    2. 비트코인 매수 : 5,000 KRW (수수료 : n KRW)
    3. 비트코인 매도 : 5,100 KRW (수수료 : n KRW)
*/

//        List<MinuteCandle> minuteCandles = upbitCandleClient.getMinuteCandles(5, "KRW-BTC", 200, LocalDateTime.now().minusMinutes(5));
//        List<Double> prices = minuteCandles.stream().map(MinuteCandle::getTradePrice).collect(Collectors.toList());
//        BollingerBands bollingerBands = getBollingerBands(prices);
    }

    /**
     * 볼린져밴드
     * @param prices
     * @return
     */
    private BollingerBands getBollingerBands(List<Double> prices) {
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
    private List<BigDecimal> getSMAList(int day, List<Double> prices) {
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
    private double stdev(List<Double> values) {
        SummaryStatistics statistics = new SummaryStatistics();
        for (Double value : values) {
            statistics.addValue(value);
        }
        return statistics.getStandardDeviation();
    }

    private void 오늘_가장최근수집된일자_수집() throws Exception {
        scheduleService.collectGetCoinFiveMinutesCandles();
    }

    private void 오늘_최초캔들생성일자_수집() throws IOException, InterruptedException {
        int size = 0;
        LocalDateTime nextTo = LocalDateTime.now().minusMinutes(5);;
        while (true) {
            long start = System.currentTimeMillis();
            List<MinuteCandle> minuteCandles = 분봉(5, "KRW-BTC", 200, nextTo);
            size += minuteCandles.size();
            nextTo = minuteCandles.get(minuteCandles.size() - 1).getCandleDateTimeUtc();
            fiveMinutesCandleRepository.saveAll(minuteCandles.stream().map(FiveMinutesCandle::of).collect(Collectors.toUnmodifiableList()));
            long end = System.currentTimeMillis();
            System.out.printf("========== %s초 =========== 사이즈 : %s\r\n", (end - start) / 1000.0, size);
            Thread.sleep(100);
            if (minuteCandles.size() < 200) {
//            if (size >= 10000) {
                break;
            }
        }
    }

    private List<MinuteCandle> 분봉(int minutes, String market, int count, LocalDateTime to) throws IOException {
        return upbitCandleClient.getMinuteCandles(minutes, market, count, to);
    }

    private void 전체종목조회() {
        List<MarketResult> allMarketInfo = upbitAllMarketClient.getAllMarketInfo(KRW);
        System.out.println(JsonUtil.toJson(allMarketInfo));
    }

    private void 주문예제() throws NoSuchAlgorithmException, UnsupportedEncodingException {
        주문하기("KRW-BTC", null, "6000", BID, 1d); // 6000KRW 매수 후

        List<AccountsResult> accountsResults = 전체계좌조회();
        Optional<String> btcBalance = accountsResults.stream()
                .filter(result -> result.getCurrency().equals("BTC"))
                .map(AccountsResult::getBalance)
                .findFirst();

        if (btcBalance.isPresent()) {
            주문하기("KRW-BTC", btcBalance.get(), null, ASK, 1d); // 전량 매도
        }
    }

    private void 주문하기(String market, String volume, String price, OrdSideType ordSideType, Double percent) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        upbitOrderClient.order(market, ordSideType, volume, price);
    }

    private List<AccountsResult> 전체계좌조회() {
        List<AccountsResult> results = upbitAssetClient.getAllAssets();
        System.out.println(JsonUtil.toJson(results));
        return results;
    }

    private MarketOrderableResult 주문가능정보() {
        MarketOrderableRequest request = MarketOrderableRequest.builder()
                .market("KRW-BTC")
                .build();
        return upbitOrderClient.getMargetOrderableInfo(request);
    }

    private List<OrderResult> 주문목록조회() {
        OrderListRequest orderRequest = OrderListRequest.builder()
                .state(OrderStateType.WAIT)
                .build();

        List<OrderResult> orderList = upbitOrderClient.getOrderListInfo(orderRequest);
        return orderList;
    }

    private void 주문취소(String uuid) throws Exception {
        OrderCancelRequest orderCancelRequest = OrderCancelRequest.builder()
                .uuid(uuid)
                .build();
        OrderCancelResult orderCancelResult = upbitOrderClient.orderCancel(orderCancelRequest);
        System.out.println(orderCancelResult);
    }

    private void 개별주문조회() throws NoSuchAlgorithmException, UnsupportedEncodingException {
        HashMap<String, String> params = new HashMap<>();
        params.put("uuid", "9facd1fe-bc1a-4fa8-af47-f8d8d1197d33");

        ArrayList<String> queryElements = new ArrayList<>();
        for(Map.Entry<String, String> entity : params.entrySet()) {
            queryElements.add(entity.getKey() + "=" + entity.getValue());
        }

        String queryString = String.join("&", queryElements.toArray(new String[0]));

        MessageDigest md = MessageDigest.getInstance("SHA-512");
        md.update(queryString.getBytes("UTF-8"));

        String queryHash = String.format("%0128x", new BigInteger(1, md.digest()));

        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        String jwtToken = JWT.create()
                .withClaim("access_key", accessKey)
                .withClaim("nonce", UUID.randomUUID().toString())
                .withClaim("query_hash", queryHash)
                .withClaim("query_hash_alg", "SHA512")
                .sign(algorithm);

        String authenticationToken = "Bearer " + jwtToken;

        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet(serverUrl + "/v1/order?" + queryString);
            request.setHeader("Content-Type", "application/json");
            request.addHeader("Authorization", authenticationToken);

            HttpResponse response = client.execute(request);
            HttpEntity entity = response.getEntity();

            System.out.println(EntityUtils.toString(entity, "UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
