package com.example.democoin.upbit;

import com.example.democoin.MarketUnit;
import com.example.democoin.configuration.properties.UpbitProperties;
import com.example.democoin.upbit.result.MarketResult;
import com.example.democoin.utils.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class UpbitAllMarketClient {

    private final UpbitProperties properties;
    private final RestTemplate restTemplate;

    public List<MarketResult> getAllMarketInfo(MarketUnit marketUnit) {
        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

            URI uri = URI.create(properties.getServerUrl() + "/v1/market/all?isDetails=false");

            ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, new HttpEntity(httpHeaders), String.class);

            if (response.getStatusCode() != HttpStatus.OK) {
                throw new Exception("StatusCode = " + response.getStatusCode().value());
            }
            try {
                List<MarketResult> marketResults = JsonUtil.listFromJson(response.getBody(), MarketResult.class);
                if (CollectionUtils.isEmpty(marketResults)) {
                    return Collections.emptyList();
                }
                return marketResults.stream()
                                    .filter(market -> marketUnit.matched(market.getMarket()))
                                    .collect(Collectors.toUnmodifiableList());
            } catch (Exception e) {
                throw e;
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
