package com.example.democoin.upbit.service;

import com.example.democoin.upbit.db.entity.FifteenMinutesCandle;
import com.example.democoin.upbit.db.entity.FiveMinutesCandle;

import java.time.LocalDateTime;
import java.util.List;

public interface CandleService {
    List<FiveMinutesCandle> findFiveMinutesCandlesLimitOffset(String market,
                                                              LocalDateTime of,
                                                              int limit,
                                                              int offset);

    List<FiveMinutesCandle> findFiveMinutesCandlesUnderByTimestamp(String market,
                                                                   Long timestamp);

    List<FifteenMinutesCandle> findFifteenMinutesCandlesUnderByTimestamp(String market,
                                                                         Long timestamp);

    FiveMinutesCandle nextCandle(Long timestamp,
                                 String market);
}
