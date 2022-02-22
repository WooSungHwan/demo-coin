package com.example.democoin.upbit.db.repository;

import com.example.democoin.upbit.db.entity.FiveMinutesCandle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface FiveMinutesCandleRepository extends JpaRepository<FiveMinutesCandle, Long> {

    @Query(value = "SELECT max(candleDateTimeUtc) FROM FiveMinutesCandle")
    LocalDateTime maxCandleDateTimeUtc();

    boolean existsByTimestamp(@Param("timestamp") Long timestamp);

}
