package com.wedding.broker.repository;

import com.wedding.broker.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // Spring Data JPA automatically provides methods like save(), findById(), findAll(), delete()

    // NEW: Find all orders by a specific user ID
    List<Order> findByUserId(String userId);

    Optional<Order> findById(Long id);
}