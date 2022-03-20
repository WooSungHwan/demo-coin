package com.example.democoin.backtest.repository;

import com.example.democoin.backtest.entity.BackTestOrders;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BackTestOrdersRepository extends JpaRepository<BackTestOrders, Long> {
}
