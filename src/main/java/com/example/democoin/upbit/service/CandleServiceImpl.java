package com.example.democoin.upbit.service;

import com.example.democoin.upbit.db.entity.FifteenMinutesCandle;
import com.example.democoin.upbit.db.entity.FiveMinutesCandle;
import com.example.democoin.upbit.db.repository.FifteenMinutesCandleRepository;
import com.example.democoin.upbit.db.repository.FiveMinutesCandleRepository;
import com.example.democoin.upbit.enums.MinuteType;
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
    private final FifteenMinutesCandleRepository fifteenMinutesCandleRepository;

    @Override
    public List<FiveMinutesCandle> findFiveMinutesCandlesLimitOffset(String market, LocalDateTime of, int limit, int offset) {
        return fiveMinutesCandleRepository.findFiveMinutesCandlesLimitOffset(market, of, limit, offset);
    }

    @Override
    public List<FiveMinutesCandle> findFiveMinutesCandlesUnderByTimestamp(String market, Long timestamp) {
        return fiveMinutesCandleRepository.findFiveMinutesCandlesUnderByTimestamp(market, timestamp);
    }

    @Override
    public List<FifteenMinutesCandle> findFifteenMinutesCandlesUnderByTimestamp(String market, Long timestamp) {
        return fifteenMinutesCandleRepository.findFifteenMinutesCandlesUnderByTimestamp(market, timestamp);
    }

    @Override
    public FiveMinutesCandle nextCandle(Long timestamp, String market) {
        return fiveMinutesCandleRepository.nextCandle(timestamp, market);
    }
}
