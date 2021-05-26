package com.laboschqpa.server.repo;

import com.laboschqpa.server.entity.account.UserEmailAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserEmailAddressRepository extends JpaRepository<UserEmailAddress, Long> {
    Optional<UserEmailAddress> findByEmail(String email);

    List<UserEmailAddress> findAllByUserAccId(Long userId);
}
