package com.example.democoin;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.democoin.configuration.properties.UpbitProperties;
import com.example.democoin.upbit.client.UpbitAllMarketClient;
import com.example.democoin.upbit.client.UpbitAssetClient;
import com.example.democoin.upbit.client.UpbitCandleClient;
import com.example.democoin.upbit.client.UpbitOrderClient;
import com.example.democoin.upbit.db.entity.FiveMinutesCandle;
import com.example.democoin.upbit.db.repository.FiveMinutesCandleRepository;
import com.example.democoin.upbit.enums.OrdSideType;
import com.example.democoin.upbit.result.AccountsResult;
import com.example.democoin.upbit.result.MarketOrderableResult;
import com.example.democoin.upbit.result.MarketResult;
import com.example.democoin.upbit.result.candles.MinuteCandle;
import com.example.democoin.utils.JsonUtil;
import com.example.democoin.utils.LocalDateTimeUtil;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
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

import javax.transaction.Transactional;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.democoin.upbit.enums.MarketUnit.*;
import static com.example.democoin.upbit.enums.OrdSideType.ASK;
import static com.example.democoin.upbit.enums.OrdSideType.BID;

@SpringBootTest
class DemoCoinApplicationTests {

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

//    @Transactional
    @Test
    void contextLoads() throws Exception {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
//        List<AccountsResult> accountsResults = 전체계좌조회();
//        주문가능정보();
//        개별주문조회();
//        주문목록조회();
//        전체종목조회();
//        주문예제();

//        오늘_가장최근수집된일자_수집();
//        오늘_최초캔들생성일자_수집();
    }

    private void 오늘_가장최근수집된일자_수집() throws IOException, InterruptedException {
        int size = 0;
        LocalDateTime nextTo = LocalDateTime.now();
        boolean flag = true;
        while (flag) {
            long start = System.currentTimeMillis();

            List<MinuteCandle> minuteCandles = 분봉(5, "KRW-BTC", 200, nextTo);
            for (MinuteCandle candle : minuteCandles) {
                if (!fiveMinutesCandleRepository.existsByTimestamp(candle.getTimestamp())) {
                    fiveMinutesCandleRepository.save(FiveMinutesCandle.of(candle));
                    size ++;
                } else {
                    flag = false;
                    break;
                }
            }
            long end = System.currentTimeMillis();
            System.out.printf("========== %s초 =========== 사이즈 : %s\r\n", (end - start) / 1000.0, size);
            Thread.sleep(100);
        }
    }

    private void 오늘_최초캔들생성일자_수집() throws IOException, InterruptedException {
        int size = 0;
        LocalDateTime nextTo = LocalDateTime.now();
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

    private void 주문가능정보() {
        MarketOrderableResult result = upbitOrderClient.getMargetOrderableInfo();
        System.out.println(JsonUtil.toJson(result));
    }

    private void 주문목록조회() throws NoSuchAlgorithmException, UnsupportedEncodingException {
        HashMap<String, String> params = new HashMap<>();
        params.put("state", "wait");

//        String[] uuids = {
//                "9ca023a5-851b-4fec-9f0a-48cd83c2eaae"
//                 ...
//        };

        ArrayList<String> queryElements = new ArrayList<>();
        for(Map.Entry<String, String> entity : params.entrySet()) {
            queryElements.add(entity.getKey() + "=" + entity.getValue());
        }
//        for(String uuid : uuids) {
//            queryElements.add("uuids[]=" + uuid);
//        }

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
            HttpGet request = new HttpGet(serverUrl + "/v1/orders?" + queryString);
            request.setHeader("Content-Type", "application/json");
            request.addHeader("Authorization", authenticationToken);

            HttpResponse response = client.execute(request);
            HttpEntity entity = response.getEntity();

            System.out.println(EntityUtils.toString(entity, "UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
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
