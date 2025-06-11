package com.snuggle.homework.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.snuggle.homework.domain.entity.Admin;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByUsername(String username);
}
