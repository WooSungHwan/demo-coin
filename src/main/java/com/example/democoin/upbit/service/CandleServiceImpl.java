package com.example.democoin.upbit.service;

import com.example.democoin.slack.SlackMessageService;
import com.example.democoin.upbit.db.entity.FiveMinutesCandle;
import com.example.democoin.upbit.db.repository.FiveMinutesCandleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
@Service
public class CandleServiceImpl implements CandleService {

    private final FiveMinutesCandleRepository fiveMinutesCandleRepository;
    private final SlackMessageService slackMessageService;

    @Override
    public List<FiveMinutesCandle> findFiveMinutesCandlesLimitOffset(String market, LocalDateTime of, int limit, int offset) {
        long start = System.currentTimeMillis();
        List<FiveMinutesCandle> result = fiveMinutesCandleRepository.findFiveMinutesCandlesLimitOffset(market, of, limit, offset);
        long end = System.currentTimeMillis();
        if (end - start >= 400) {
            slackMessageService.backtestMessage(String.format("[slow query] findFiveMinutesCandlesLimitOffset [%s]", end - start));
        }
        return result;
    }

    @Override
    public FiveMinutesCandle findFiveMinutesCandleByKst(String market, LocalDateTime targetDate) {
        long start = System.currentTimeMillis();
        FiveMinutesCandle result = fiveMinutesCandleRepository.findFiveMinutesCandleByKst(market, targetDate);
        long end = System.currentTimeMillis();
        if (end - start >= 400) {
            slackMessageService.backtestMessage(String.format("[slow query] findFiveMinutesCandlesLimitOffset [%s]", end - start));
        }
        return result;
    }

    @Override
    public List<FiveMinutesCandle> findFiveMinutesCandlesUnderByTimestamp(String market, Long timestamp) {
        long start = System.currentTimeMillis();
        List<FiveMinutesCandle> result = fiveMinutesCandleRepository.findFiveMinutesCandlesUnderByTimestamp(market, timestamp);
        long end = System.currentTimeMillis();
        if (end - start >= 400) {
            slackMessageService.backtestMessage(String.format("[slow query] findFiveMinutesCandlesUnderByTimestamp [%s]", end - start));
        }
        return result;
    }

    @Override
    public FiveMinutesCandle nextCandle(Long timestamp, String market) {
        long start = System.currentTimeMillis();
        FiveMinutesCandle result = fiveMinutesCandleRepository.nextCandle(timestamp, market);
        long end = System.currentTimeMillis();
        if (end - start >= 400) {
            slackMessageService.backtestMessage(String.format("[slow query] nextCandle [%s]", end - start));
        }
        return result;
    }

    @Override
    public Double getFiveMinuteCandlesMA(FiveMinutesCandle candle, int limit) {
        long start = System.currentTimeMillis();
        Double result = fiveMinutesCandleRepository.getMA(candle.getMarket().getType(), candle.getCandleDateTimeKst(), limit);
        long end = System.currentTimeMillis();
        if (end - start >= 400) {
            slackMessageService.backtestMessage(String.format("[slow query] getFiveMinuteCandlesMA [%s]", end - start));
        }
        return result;
    }
}
