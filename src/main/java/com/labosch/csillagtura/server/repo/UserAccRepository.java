package com.labosch.csillagtura.server.repo;

import com.labosch.csillagtura.server.entity.UserAcc;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAccRepository extends JpaRepository<UserAcc, Long> {
    Optional<UserAcc> findById(long id);
}
