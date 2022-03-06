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

    /**
     * 캔들 시간순서대로 진행하기 위해 사용.
     * @param limit
     * @param offset
     * @return
     */
    @Query(nativeQuery = true, value = "SELECT * FROM five_minutes_candle WHERE candle_date_time_kst >= :kst ORDER BY candle_date_time_kst LIMIT :limit OFFSET :offset")
    List<FiveMinutesCandle> findFiveMinutesCandlesLimitOffset(@Param("kst") LocalDateTime kst,
                                                              @Param("limit") int limit,
                                                              @Param("offset") int offset);

    /**
     * 해당 시점을 포함하여 이전 캔들 200개 가져온다.
     * @param timstamp
     * @return
     */
    @Query(nativeQuery = true, value = "SELECT * FROM five_minutes_candle WHERE timestamp <= :timestamp ORDER BY candle_date_time_kst DESC LIMIT 200")
    List<FiveMinutesCandle> findFiveMinutesCandlesUnderByTimestamp(@Param("timestamp") long timstamp);

    /**
     * 해당 timestamp값의 다음 캔들을 가져온다.
     * @param timestamp
     * @return
     */
    @Query(nativeQuery = true, value = "SELECT * FROM five_minutes_candle WHERE market = :market AND timestamp > :timestamp ORDER BY candle_date_time_kst LIMIT 1")
    FiveMinutesCandle nextCandle(@Param("timestamp") long timestamp,
                                 @Param("market") String market);
}
