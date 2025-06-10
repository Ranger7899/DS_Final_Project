// src/main/java/com/wedding/broker/repository/UserRepository.java
package com.wedding.broker.repository;

import com.wedding.broker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional; // Import Optional

@Repository
public interface UserRepository extends JpaRepository<User, Long> { // User is your entity, Long is the ID type

    // Methods for finding users by username or email
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    // Methods for checking if a username or email exists
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}