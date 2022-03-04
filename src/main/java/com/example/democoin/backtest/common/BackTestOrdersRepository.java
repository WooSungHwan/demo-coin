package com.example.democoin.backtest.common;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BackTestOrdersRepository extends JpaRepository<BackTestOrders, Long> {
}
