package com.laboschqpa.server.repo;

import com.laboschqpa.server.entity.ProfileDetails;
import com.laboschqpa.server.entity.account.UserAcc;
import com.laboschqpa.server.entity.usergeneratedcontent.NewsPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProfileDetailsRepository extends JpaRepository<ProfileDetails, Long> {

    Optional<ProfileDetails> findByUserAccId(@Param("userAccId") Long userAccId);
}
