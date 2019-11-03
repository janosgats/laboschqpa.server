package com.labosch.csillagtura.repo;

import com.labosch.csillagtura.entity.UserEmailAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserEmailAddressRepository extends JpaRepository<UserEmailAddress, Long> {
    Optional<UserEmailAddress> findByEmail(String email);
}
