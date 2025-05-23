package com.wedding.broker.repository;

import com.wedding.broker.model.Order;
import org.springframework.data.jpa.repository.JpaRepository; // Change to JpaRepository

public interface OrderRepository extends JpaRepository<Order, Long> { // Change ID type to Long
}