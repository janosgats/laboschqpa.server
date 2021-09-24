package com.laboschqpa.server.repo;

import com.laboschqpa.server.entity.account.UserEmailAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserEmailAddressRepository extends JpaRepository<UserEmailAddress, Long> {
    Optional<UserEmailAddress> findByEmail(String email);

    List<UserEmailAddress> findAllByUserAccId(Long userId);

    @Modifying
    @Query("delete from UserEmailAddress " +
            " where id = :id " +
            "   and userAcc.id = :userId")
    int deleteByIdAndAndUserIdAndGetDeletedRowCount(Long id, Long userId);
}
