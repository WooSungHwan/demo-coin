package com.example.democoin.backtest.strategy;

import com.example.democoin.backtest.strategy.ask.AskReason;
import lombok.Getter;

@Getter
public class StrategyResult {

    private Boolean result;
    private AskReason reason;

}
