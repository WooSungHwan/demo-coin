package com.example.democoin.backtest.strategy;

import com.example.democoin.backtest.strategy.bid.BidStrategy;
import com.example.democoin.indicator.result.BollingerBands;
import com.example.democoin.indicator.result.RSIs;
import com.example.democoin.upbit.db.entity.FifteenMinutesCandle;
import com.example.democoin.upbit.db.entity.FiveMinutesCandle;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Builder
@Getter
public class BidSignalParams {

    private BidStrategy bidStrategy;

    private BollingerBands bollingerBands;
    private RSIs rsi14;
    private List<FiveMinutesCandle> candles;
    private FiveMinutesCandle targetCandle;

    private BollingerBands fifBollingerBands;
    private RSIs fifRsi14;
    private List<FifteenMinutesCandle> fifCandles;
    private FifteenMinutesCandle fifTargetCandle;
}
