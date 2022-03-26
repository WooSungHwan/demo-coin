package com.example.democoin.task.service;

import com.example.democoin.upbit.client.UpbitCandleClient;
import com.example.democoin.upbit.db.entity.FifteenMinutesCandle;
import com.example.democoin.upbit.db.entity.FiveMinutesCandle;
import com.example.democoin.upbit.db.repository.FifteenMinutesCandleRepository;
import com.example.democoin.upbit.db.repository.FiveMinutesCandleRepository;
import com.example.democoin.upbit.enums.MarketType;
import com.example.democoin.upbit.enums.MinuteType;
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

    private final FifteenMinutesCandleRepository fifteenMinutesCandleRepository;

    private final UpbitCandleClient upbitCandleClient;

    @Override
    public void collectGetCoinCandles(MinuteType minuteType, MarketType market) throws Exception {
        int minute = minuteType.getMinute();
        LocalDateTime nextTo = LocalDateTime.now().minusMinutes(minute); // 현재 만들어지는 분봉에 의해서 값이 왜곡된다.
        boolean flag = true;
        while (flag) {
            int size = 0;
            long start = System.currentTimeMillis();

            List<MinuteCandle> minuteCandles = upbitCandleClient.getMinuteCandles(minute, market, 200, nextTo);

            for (MinuteCandle candle : minuteCandles) {
                switch (minuteType) {
                    case FIVE:
                        if (!fiveMinutesCandleRepository.existsByTimestamp(candle.getTimestamp())) {
                            fiveMinutesCandleRepository.save(FiveMinutesCandle.of(candle));
                            size ++;
                        } else {
                            flag = false;
                        }
                        break;
                    case FIFTEEN:
                        if (!fifteenMinutesCandleRepository.existsByTimestamp(candle.getTimestamp())) {
                            fifteenMinutesCandleRepository.save(FifteenMinutesCandle.of(candle));
                            size++;
                        } else {
                            flag = false;
                        }
                        break;
                    default:
                        throw new RuntimeException("There isn`t type : " + minuteType.name());
                }
            }
            nextTo = minuteCandles.get(minuteCandles.size() - 1).getCandleDateTimeUtc();
            long end = System.currentTimeMillis();
            System.out.printf("========== %s초 =========== 사이즈 : %s\r\n", (end - start) / 1000.0, size);
            Thread.sleep(100);
        }
    }
}
