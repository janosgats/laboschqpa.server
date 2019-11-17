package com.laboschcst.server.repo;

import com.laboschcst.server.entity.externalaccount.GoogleExternalAccountDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GoogleExternalAccountDetailRepository extends JpaRepository<GoogleExternalAccountDetail, Long> {
    Optional<GoogleExternalAccountDetail> findBySub(String sub);
}
