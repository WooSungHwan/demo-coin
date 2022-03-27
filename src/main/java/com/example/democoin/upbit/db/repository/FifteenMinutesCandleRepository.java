package com.example.democoin.upbit.db.repository;

import com.example.democoin.upbit.db.entity.FifteenMinutesCandle;
import com.example.democoin.upbit.db.entity.FiveMinutesCandle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface FifteenMinutesCandleRepository extends JpaRepository<FifteenMinutesCandle, Long> {

    boolean existsByTimestamp(@Param("timestamp") Long timestamp);

    /**
     * 캔들 시간순서대로 진행하기 위해 사용.
     * @param limit
     * @param offset
     * @return
     */
    @Query(nativeQuery = true, value = "SELECT * FROM fifteen_minutes_candle WHERE market = :market AND candle_date_time_kst >= :kst ORDER BY candle_date_time_kst LIMIT :limit OFFSET :offset")
    List<FifteenMinutesCandle> findFifteenMinutesCandlesLimitOffset(@Param("market") String market,
                                                                 @Param("kst") LocalDateTime kst,
                                                                 @Param("limit") int limit,
                                                                 @Param("offset") int offset);

    /**
     * 해당 시점을 포함하여 이전 캔들 200개 가져온다.
     * @param timestamp
     * @return
     */
    @Query(nativeQuery = true, value = "SELECT * FROM fifteen_minutes_candle WHERE market = :market AND timestamp <= :timestamp ORDER BY timestamp DESC LIMIT 200")
    List<FifteenMinutesCandle> findFifteenMinutesCandlesUnderByTimestamp(@Param("market") String market,
                                                                         @Param("timestamp") long timestamp);

    /**
     * 해당 timestamp값의 다음 캔들을 가져온다.
     * @param timestamp
     * @return
     */
    @Query(nativeQuery = true, value = "SELECT * FROM fifteen_minutes_candle WHERE market = :market AND timestamp > :timestamp ORDER BY timestamp LIMIT 1")
    FifteenMinutesCandle nextCandle(@Param("timestamp") long timestamp,
                                    @Param("market") String market);
}
