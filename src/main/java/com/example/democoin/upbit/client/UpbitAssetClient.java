package com.example.democoin.upbit.client;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.democoin.upbit.result.accounts.AccountsResult;
import com.example.democoin.configuration.properties.UpbitProperties;
import com.example.democoin.utils.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class UpbitAssetClient {

    private final UpbitProperties upbitProperties;
    private final RestTemplate restTemplate;

    public List<AccountsResult> getAllAssets() {
        try {
            Algorithm algorithm = Algorithm.HMAC256(upbitProperties.getSecretKey());
            String jwtToken = JWT.create()
                    .withClaim("access_key", upbitProperties.getAccessKey())
                    .withClaim("nonce", UUID.randomUUID().toString())
                    .sign(algorithm);

            String authenticationToken = "Bearer " + jwtToken;

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
            httpHeaders.set("Authorization", authenticationToken);

            URI uri = URI.create(upbitProperties.getServerUrl() + "/v1/accounts");

            ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, new HttpEntity(httpHeaders), String.class);
            if(response.getStatusCode() != HttpStatus.OK) {
                throw new Exception("StatusCode = " + response.getStatusCode().value());
            }

            try {
                return JsonUtil.listFromJson(response.getBody(), AccountsResult.class);
            } catch(Exception e) {
                throw e;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
