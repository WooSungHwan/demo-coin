package com.example.democoin.upbit.db.repository;

import com.example.democoin.upbit.db.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdersRepository extends JpaRepository<Orders, Long> {
}
