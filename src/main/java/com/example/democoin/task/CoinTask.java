package com.example.democoin.task;

import com.example.democoin.slack.SlackMessageService;
import com.example.democoin.task.service.ScheduleService;
import com.example.democoin.upbit.client.UpbitAllMarketClient;
import com.example.democoin.upbit.client.UpbitCandleClient;
import com.example.democoin.upbit.enums.MarketType;
import com.example.democoin.upbit.enums.MarketUnit;
import com.example.democoin.upbit.enums.MinuteType;
import com.example.democoin.upbit.result.candles.MinuteCandle;
import com.example.democoin.upbit.result.market.MarketResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static java.math.RoundingMode.HALF_UP;

@Slf4j
@RequiredArgsConstructor
@Component
public class CoinTask {

    private final ScheduleService scheduleService;
    private final SlackMessageService slackMessageService;
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

            for (MarketType marketType : MarketType.marketTypeList) {
                log.info("코인 : {} 5분봉 수집 시작", marketType.getName());
                try {
                    scheduleService.collectGetCoinCandles(MinuteType.FIVE, marketType);
                } catch (Exception e) {
                    log.error("코인 : {} 5분봉 수집 중 에러가 발생\r\n 에러메시지 : {}", marketType.getName(), e.getMessage());
                    slackMessageService.message(String.format("%s 5분봉 수집 중 에러발생!! 에러메시지 : %s", marketType.getName(), e.getMessage()));
                } finally {
                    log.info("코인 : {} 5분봉 수집 종료", marketType.getName());
                }
            }
            log.info("[Demo Coin Scheduling] 코인 5분봉 수집 스케줄 종료");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            slackMessageService.message("5분봉 수집 완료");
        }
    }

    @Autowired
    private UpbitCandleClient upbitCandleClient;

    @Autowired
    private UpbitAllMarketClient upbitAllMarketClient;

//    @Scheduled(fixedDelay = 1000 * 60 * 1)
    public void tradeVolume() throws Exception {
        List<MarketResult> marketResults = upbitAllMarketClient.getAllMarketInfo(MarketUnit.KRW);

        marketResults.forEach(marketResult -> {
            MarketType market = marketResult.getMarket();

            log.info("======================= 종목 : {} 시작 =======================", market);
            List<MinuteCandle> minuteCandlesBofore = upbitCandleClient.getMinuteCandles(3, market, 200, LocalDateTime.now().minusMinutes(3));
            double beforeStdev = stdev(minuteCandlesBofore);

            List<MinuteCandle> minuteCandlesAfter = upbitCandleClient.getMinuteCandles(3, market, 200, LocalDateTime.now());
            double afterStdev = stdev(minuteCandlesAfter);

            if ((2.0 * beforeStdev) < afterStdev) {
                String nowFormat = LocalDateTime.now(ZoneId.of("Asia/Seoul")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                slackMessageService.message(String.format("%s 해당 시간에 거래량 급등 발생!! 종목 : %s", nowFormat, marketResult.getKoreanName()));
            }
            log.info("============ before: {}, after: {} ============", new BigDecimal(beforeStdev).setScale(2, HALF_UP), new BigDecimal(afterStdev).setScale(2, HALF_UP));
            log.info("======================= 종목 : {} 종료 =======================", market);

            try {
                Thread.sleep(150);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    private double stdev(List<MinuteCandle> minuteCandles) {
        SummaryStatistics statistics = new SummaryStatistics();
        for (MinuteCandle candle : minuteCandles) {
            statistics.addValue(candle.getCandleAccTradeVolume());
        }
        return statistics.getStandardDeviation();
    }
}
