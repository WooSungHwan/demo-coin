package com.example.democoin.configuration.properties;

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
@ConfigurationProperties(prefix = "slack.api")
public class SlackProperties {

    @NotBlank(message = "슬랙 메시지 전송 서버 URL을 설정해주세요.")
    private String messageUrl;

    @NotBlank(message = "슬랙 oauth token을 설정해주세요.")
    private String token;

    @NotBlank(message = "슬랙 메시지 수신 채널 코드를 설정해주세요.")
    private String channel;

    @NotBlank(message = "슬랙 메시지 수신 백테스트 채널 코드를 설정해주세요.")
    private String channelBacktest;

    @NotBlank(message = "슬랙 메시지 수신 스케줄 오류 채널 코드를 설정해주세요.")
    private String channelScheduleError;
}
