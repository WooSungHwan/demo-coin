package com.example.democoin.task.service;

import com.example.democoin.upbit.enums.MarketType;
import com.example.democoin.upbit.enums.MinuteType;

public interface ScheduleService {
    void collectGetCoinCandles(MinuteType minute, MarketType market) throws Exception ;
}
