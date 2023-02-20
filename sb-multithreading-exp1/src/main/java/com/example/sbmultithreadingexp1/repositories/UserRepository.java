package com.example.sbmultithreadingexp1.repositories;

import com.example.sbmultithreadingexp1.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}