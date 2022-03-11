package com.example.democoin.backtest;

import com.example.democoin.backtest.common.BackTestOrdersRepository;
import com.example.democoin.upbit.db.repository.OrdersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {

    private final BackTestOrdersRepository ordersRepository;

    @Override
    public void bid() {

    }

    @Override
    public void ask() {

    }
}
