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
        return fiveMinutesCandleRepository.findFiveMinutesCandlesLimitOffset(market, of, limit, offset);
    }

    @Override
    public FiveMinutesCandle findFiveMinutesCandleByKst(String market, LocalDateTime targetDate) {
        return fiveMinutesCandleRepository.findFiveMinutesCandleByKst(market, targetDate);
    }

    @Override
    public List<FiveMinutesCandle> findFiveMinutesCandlesUnderByTimestamp(String market, Long timestamp, int limit) {
        return fiveMinutesCandleRepository.findFiveMinutesCandlesUnderByTimestamp(market, timestamp, limit);
    }

    @Override
    public FiveMinutesCandle nextCandle(Long timestamp, String market) {
        return fiveMinutesCandleRepository.nextCandle(timestamp, market);
    }

    @Override
    public Double getFiveMinuteCandlesMA(FiveMinutesCandle candle, int limit) {
        return fiveMinutesCandleRepository.getMA(candle.getMarket().getType(), candle.getCandleDateTimeKst(), limit);
    }
}
