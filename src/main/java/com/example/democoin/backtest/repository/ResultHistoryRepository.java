package com.example.democoin.backtest.repository;

import com.example.democoin.backtest.entity.ResultHistory;
import com.example.democoin.backtest.entity.ResultInfo;
import org.springframework.data.repository.CrudRepository;

public interface ResultHistoryRepository extends CrudRepository<ResultHistory, Long> {
}
