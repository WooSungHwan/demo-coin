package com.example.democoin.backtest.repository;

import com.example.democoin.backtest.entity.BackTestOrders;
import org.springframework.data.repository.CrudRepository;

public interface BackTestOrdersRepository extends CrudRepository<BackTestOrders, Long> {
}
