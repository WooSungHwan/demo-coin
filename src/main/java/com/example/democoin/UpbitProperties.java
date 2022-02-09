package com.example.democoin;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Getter
@AllArgsConstructor
@Validated
@ConstructorBinding
@ConfigurationProperties(prefix = "upbit.api")
public class UpbitProperties {

    @NotBlank(message = "Secret Key를 설정해주세요.")
    private String secretKey;

    @NotBlank(message = "Access Key를 설정해주세요.")
    private String accessKey;

    @NotBlank(message = "서버 URL을 설정해주세요.")
    private String serverUrl;
}
