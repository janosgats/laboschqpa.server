package com.laboschcst.server.repo;

import com.laboschcst.server.entity.ProfileDetails;
import com.laboschcst.server.entity.account.UserAcc;
import com.laboschcst.server.entity.usergeneratedcontent.NewsPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProfileDetailsRepository extends JpaRepository<ProfileDetails, Long> {

    Optional<ProfileDetails> findByUserAccId(@Param("userAccId") Long userAccId);
}
