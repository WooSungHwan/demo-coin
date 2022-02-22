package com.example.democoin.upbit.client;

import com.example.democoin.configuration.properties.UpbitProperties;
import com.example.democoin.upbit.result.candles.MinuteCandle;
import com.example.democoin.utils.JsonUtil;
import com.example.democoin.utils.LocalDateTimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import org.yaml.snakeyaml.util.UriEncoder;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class UpbitCandleClient {

    private final UpbitProperties upbitProperties;
    private final RestTemplate restTemplate;

    public List<MinuteCandle> getMinuteCandles(int minutes,
                                               String market,
                                               int count,
                                               LocalDateTime to) {
        if (count < 1) {
            return Collections.emptyList();
        }
        try {
            String url = String.format("%s/v1/candles/minutes/%s",
                    upbitProperties.getServerUrl(),
                    minutes);
            String queryString = String.format("market=%s&count=%s&to=%s",
                    market,
                    count,
                    to.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            String path = String.format("%s?%s", url, UriEncoder.encode(queryString));

            ResponseEntity<String> response = restTemplate.exchange(URI.create(path), HttpMethod.GET, HttpEntity.EMPTY, String.class);
            if (response.getStatusCode() != HttpStatus.OK) {
                throw new Exception("StatusCode = " + response.getStatusCode().value());
            }

            try {
                return JsonUtil.listFromJson(response.getBody(), MinuteCandle.class);
            } catch (Exception e) {
                throw e;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
