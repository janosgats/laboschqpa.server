package com.labosch.csillagtura.server.repo;

import com.labosch.csillagtura.server.entity.AccountJoinInitiation;
import com.labosch.csillagtura.server.entity.UserAcc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AccountJoinInitiationRepository extends JpaRepository<AccountJoinInitiation, Long> {

    Optional<AccountJoinInitiation> findByInitiatorUserAcc(UserAcc initiatorUserAcc);

    List<AccountJoinInitiation> findByApproverUserAcc(UserAcc approverUserAcc);

    @Modifying
    @Query("delete from AccountJoinInitiation where initiatorUserAcc = :userAcc or approverUserAcc = :userAcc")
    int deleteByInitiatorUserOrApproverUser(@Param("userAcc") UserAcc userAcc);

    @Modifying
    @Query("delete from AccountJoinInitiation where id = ?1")
    int deleteById_DoNOTThrowExceptionIfNotExists(Long id);
}
