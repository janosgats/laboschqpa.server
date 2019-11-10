package com.labosch.csillagtura.repo;

import com.labosch.csillagtura.entity.UserAcc;
import com.labosch.csillagtura.entity.externalaccount.ExternalAccountDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ExternalAccountDetailRepository extends JpaRepository<ExternalAccountDetail, Long> {

    @Modifying
    @Query("update ExternalAccountDetail ead set ead.userAcc  = :newUser where ead.userAcc = :oldUser")
    int updateBelongingUser(@Param("oldUser") UserAcc oldUserAcc,
                            @Param("newUser") UserAcc newUserAcc);
}
