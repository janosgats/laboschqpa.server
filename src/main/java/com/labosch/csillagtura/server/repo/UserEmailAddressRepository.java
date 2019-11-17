package com.labosch.csillagtura.server.repo;

import com.labosch.csillagtura.server.entity.UserAcc;
import com.labosch.csillagtura.server.entity.UserEmailAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserEmailAddressRepository extends JpaRepository<UserEmailAddress, Long> {
    Optional<UserEmailAddress> findByEmail(String email);

    @Modifying
    @Query("update UserEmailAddress uea set uea.userAcc  = :newUser where uea.userAcc = :oldUser")
    int updateBelongingUser(@Param("oldUser") UserAcc oldUserAcc,
                            @Param("newUser") UserAcc newUserAcc);

}
