package com.laboschcst.server.repo;

import com.laboschcst.server.entity.account.UserAcc;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAccRepository extends JpaRepository<UserAcc, Long> {
    Optional<UserAcc> findById(long id);
}
