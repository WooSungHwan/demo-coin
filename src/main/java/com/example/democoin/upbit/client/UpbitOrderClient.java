package com.example.democoin.upbit.client;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.democoin.upbit.request.OrderCancelRequest;
import com.example.democoin.upbit.result.orders.OrderCancelResult;
import com.example.democoin.upbit.request.OrderListRequest;
import com.example.democoin.upbit.result.orders.OrderResult;
import com.example.democoin.upbit.enums.OrdSideType;
import com.example.democoin.upbit.enums.OrdType;
import com.example.democoin.configuration.properties.UpbitProperties;
import com.example.democoin.upbit.result.orders.MarketOrderableResult;
import com.example.democoin.utils.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

@Slf4j
@Component
@RequiredArgsConstructor
public class UpbitOrderClient {

    private final UpbitProperties properties;
    private final RestTemplate restTemplate;

    /**
     * 마켓별 주문 가능 정보를 확인한다.
     */
    public MarketOrderableResult getMargetOrderableInfo() {
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("market", "KRW-BTC");

            ArrayList<String> queryElements = new ArrayList<>();
            for(Map.Entry<String, String> entity : params.entrySet()) {
                queryElements.add(entity.getKey() + "=" + entity.getValue());
            }

            String queryString = String.join("&", queryElements.toArray(new String[0]));
            MessageDigest md = getMessageDigest(queryString);
            String queryHash = String.format("%0128x", new BigInteger(1, md.digest()));
            String jwtToken = makeJWT(queryHash);

            URI uri = URI.create(properties.getServerUrl() + "/v1/orders/chance?" + queryString);

            ResponseEntity<String> response = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    new HttpEntity(getHttpHeaders(jwtToken)),
                    String.class);

            if (response.getStatusCode() != HttpStatus.OK) {
                throw new Exception("StatusCode = " + response.getStatusCode().value());
            }

            try {
                return JsonUtil.fromJson(response.getBody(), MarketOrderableResult.class);
            } catch (Exception e) {
                throw e;
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 주문 리스트 조회
     * @param orderRequest
     */
    public List<OrderResult> getOrderListInfo(OrderListRequest orderRequest) {
        try {
            String queryString = orderRequest.toQueryString();
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(queryString.getBytes("UTF-8"));

            String queryHash = String.format("%0128x", new BigInteger(1, md.digest()));

            Algorithm algorithm = Algorithm.HMAC256(properties.getSecretKey());
            String jwtToken = JWT.create()
                    .withClaim("access_key", properties.getAccessKey())
                    .withClaim("nonce", UUID.randomUUID().toString())
                    .withClaim("query_hash", queryHash)
                    .withClaim("query_hash_alg", "SHA512")
                    .sign(algorithm);

            String authenticationToken = "Bearer " + jwtToken;

            try {
                HttpClient client = HttpClientBuilder.create().build();
                HttpGet request = new HttpGet(properties.getServerUrl() + "/v1/orders?" + queryString);
                request.setHeader("Content-Type", "application/json");
                request.addHeader("Authorization", authenticationToken);

                HttpResponse response = client.execute(request);
                org.apache.http.HttpEntity entity = response.getEntity();

                return JsonUtil.listFromJson(EntityUtils.toString(entity, "UTF-8"), OrderResult.class);
            } catch (IOException e) {
                throw e;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 주문 취소
     * @param orderCancelRequest
     * @return
     */
    public OrderCancelResult orderCancel(OrderCancelRequest orderCancelRequest) {
        try {
            String queryString = orderCancelRequest.toQueryString();

            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(queryString.getBytes("UTF-8"));

            String queryHash = String.format("%0128x", new BigInteger(1, md.digest()));

            Algorithm algorithm = Algorithm.HMAC256(properties.getSecretKey());
            String jwtToken = JWT.create()
                    .withClaim("access_key", properties.getAccessKey())
                    .withClaim("nonce", UUID.randomUUID().toString())
                    .withClaim("query_hash", queryHash)
                    .withClaim("query_hash_alg", "SHA512")
                    .sign(algorithm);

            String authenticationToken = "Bearer " + jwtToken;

            try {
                HttpClient client = HttpClientBuilder.create().build();
                HttpDelete request = new HttpDelete(properties.getServerUrl() + "/v1/order?" + queryString);
                request.setHeader("Content-Type", "application/json");
                request.addHeader("Authorization", authenticationToken);

                HttpResponse response = client.execute(request);
                org.apache.http.HttpEntity entity = response.getEntity();

                return JsonUtil.fromJson(EntityUtils.toString(entity, "UTF-8"), OrderCancelResult.class);
            } catch (IOException e) {
                throw e;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @param market
     * @param ordSideType
     * @param volume
     * @param price
     */
    public void order(String market, OrdSideType ordSideType, String volume, String price) {
        order(market, ordSideType, volume, price, null);
    }

    private void order(String market, OrdSideType ordSideType, String volume, String price, Double percent) {
        try {
            if (volume != null && percent != null) {
                volume = new BigDecimal(Double.parseDouble(volume) * percent).round(MathContext.DECIMAL32).toString();
            }
            HashMap<String, String> params = settingBody(market, ordSideType, volume, price);

            ArrayList<String> queryElements = new ArrayList<>();
            for(Map.Entry<String, String> entity : params.entrySet()) {
                queryElements.add(entity.getKey() + "=" + entity.getValue());
            }

            MessageDigest md = getMessageDigest(String.join("&", queryElements.toArray(new String[0])));
            String queryHash = String.format("%0128x", new BigInteger(1, md.digest()));
            String jwtToken = makeJWT(queryHash);

            try {
                URI uri = URI.create(properties.getServerUrl() + "/v1/orders");
                ResponseEntity<String> response = restTemplate.exchange(
                        uri,
                        HttpMethod.POST,
                        new HttpEntity(params, getHttpHeaders(jwtToken)),
                        String.class);

                if (response.getStatusCode() != HttpStatus.CREATED) {
                    throw new Exception("StatusCode = " + response.getStatusCode().value());
                }

                System.out.println(response.getBody());
            } catch (IOException e) {
                throw e;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private HttpHeaders getHttpHeaders(String jwtToken) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        httpHeaders.set(AUTHORIZATION, "Bearer " + jwtToken);
        return httpHeaders;
    }

    private String makeJWT(String queryHash) {
        return JWT.create()
                .withClaim("access_key", properties.getAccessKey())
                .withClaim("nonce", UUID.randomUUID().toString())
                .withClaim("query_hash", queryHash)
                .withClaim("query_hash_alg", "SHA512")
                .sign(Algorithm.HMAC256(properties.getSecretKey()));
    }

    private MessageDigest getMessageDigest(String queryString) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        md.update(queryString.getBytes(StandardCharsets.UTF_8));
        return md;
    }

    private HashMap<String, String> settingBody(String market, OrdSideType ordSideType, String volume, String price) throws Exception {
        HashMap<String, String> params = new HashMap<>();
        params.put("market", market);
        params.put("side", ordSideType.getType());
        switch (ordSideType) {
            case ASK:
                // 매도
                params.put("volume", volume); // 매도수량 필수
                params.put("ord_type", OrdType.MARKET.getType()); // 시장가 매도
                break;
            case BID:
                // 매수
                params.put("price", price); // 매수 가격
                params.put("ord_type", OrdType.PRICE.getType()); // 시장가 매수
                break;
            default:
                String message = "ord_side 값이 아님 : " + ordSideType.getType();
                log.error(message);
                throw new Exception(message);
        }
        return params;
    }
}
