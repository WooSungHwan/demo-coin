package com.example.democoin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class BTCTask {

    private final ScheduleService scheduleService;

    /**
     * <pre>
     *     코인 5분봉을 수집한다. (매 5분마다)
     * </pre>
     * @throws Exception
     */
//    @Scheduled(fixedDelay = 1000 * 60 * 5)
    public void collectGetCoinFiveMinutesCandles() throws Exception {
        try {
            log.info("[Demo Coin Scheduling] 코인 5분봉 수집 스케줄 시작");

            scheduleService.collectGetCoinFiveMinutesCandles();

            log.info("[Demo Coin Scheduling] 코인 5분봉 수집 스케줄 종료");
        } catch (Exception e) {
            // TODO slack으로 메시지 보내기
            e.printStackTrace();
        }
    }
}
