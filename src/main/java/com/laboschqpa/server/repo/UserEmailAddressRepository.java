package com.laboschqpa.server.repo;

import com.laboschqpa.server.entity.account.UserAcc;
import com.laboschqpa.server.entity.account.UserEmailAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserEmailAddressRepository extends JpaRepository<UserEmailAddress, Long> {
    Optional<UserEmailAddress> findByEmail(String email);

}
