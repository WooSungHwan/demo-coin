package com.example.democoin.upbit.db.repository;

import com.example.democoin.upbit.db.entity.FiveMinutesCandle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface FiveMinutesCandleRepository extends JpaRepository<FiveMinutesCandle, Long> {

    @Query(value = "SELECT max(candleDateTimeUtc) FROM FiveMinutesCandle")
    LocalDateTime maxCandleDateTimeUtc();

    boolean existsByTimestamp(@Param("timestamp") Long timestamp);

    @Query(nativeQuery = true, value = "SELECT trade_price FROM five_minutes_candle ORDER BY candle_date_time_kst DESC LIMIT :limit")
    double[] findFiveMinutesCandlesTradePricesByLimit(@Param("limit") Integer limit);

    @Query(nativeQuery = true, value = "SELECT candle_acc_trade_volume FROM five_minutes_candle ORDER BY candle_date_time_kst DESC LIMIT :limit")
    double[] findFiveMinutesCandlesAccTradeVolumesByLimit(@Param("limit") Integer limit);

    @Query(nativeQuery = true, value = "SELECT * FROM five_minutes_candle ORDER BY candle_date_time_kst LIMIT :limit OFFSET :offset")
    List<FiveMinutesCandle> findFiveMinutesCandlesLimitOffset(@Param("limit") int limit,
                                                              @Param("offset") int offset);

    @Query(nativeQuery = true, value = "SELECT * FROM five_minutes_candle WHERE timestamp <= :timestamp ORDER BY candle_date_time_kst DESC LIMIT 50")
    List<FiveMinutesCandle> findFiveMinutesCandlesUnderByTimestamp(@Param("timestamp") long timstamp);
}
