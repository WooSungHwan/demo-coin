package com.example.democoin;

import com.example.democoin.upbit.client.UpbitCandleClient;
import com.example.democoin.upbit.db.entity.FiveMinutesCandle;
import com.example.democoin.upbit.db.repository.FiveMinutesCandleRepository;
import com.example.democoin.upbit.result.candles.MinuteCandle;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ScheduleServiceImpl implements ScheduleService {

    private final FiveMinutesCandleRepository fiveMinutesCandleRepository;

    private final UpbitCandleClient upbitCandleClient;

    @Override
    public void collectGetCoinFiveMinutesCandles() throws Exception {

        int size = 0;
        LocalDateTime nextTo = LocalDateTime.now().minusMinutes(5); // 현재 만들어지는 분봉에 의해서 값이 왜곡된다.
        boolean flag = true;
        while (flag) {
            long start = System.currentTimeMillis();

            List<MinuteCandle> minuteCandles = upbitCandleClient.getMinuteCandles(5, "KRW-BTC", 200, nextTo);

            for (MinuteCandle candle : minuteCandles) {
                if (!fiveMinutesCandleRepository.existsByTimestamp(candle.getTimestamp())) {
                    fiveMinutesCandleRepository.save(FiveMinutesCandle.of(candle));
                    size ++;
                } else {
                    flag = false;
                    break;
                }
            }

            long end = System.currentTimeMillis();
            System.out.printf("========== %s초 =========== 사이즈 : %s\r\n", (end - start) / 1000.0, size);
            Thread.sleep(100);
        }
    }
}
