package com.example.democoin.task.service;

import com.example.democoin.upbit.enums.MarketType;

public interface ScheduleService {
    void collectGetCoinFiveMinutesCandles(MarketType market) throws Exception ;
}
