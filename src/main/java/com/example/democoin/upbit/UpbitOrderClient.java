package com.example.democoin.upbit;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.democoin.configuration.properties.UpbitProperties;
import com.example.democoin.upbit.result.MarketOrderableResult;
import com.example.democoin.utils.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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

            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(queryString.getBytes(StandardCharsets.UTF_8));

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
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
                httpHeaders.set("Authorization", authenticationToken);

                URI uri = URI.create(properties.getServerUrl() + "/v1/orders/chance?" + queryString);

                ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, new HttpEntity(httpHeaders), String.class);
                return JsonUtil.fromJson(response.getBody(), MarketOrderableResult.class);
            } catch (Exception e) {
                throw e;
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
