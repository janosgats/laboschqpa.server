package com.laboschqpa.server.repo.externalaccountdetail;

import com.laboschqpa.server.entity.account.externalaccountdetail.AuthSchExternalAccountDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthSchExternalAccountDetailRepository extends JpaRepository<AuthSchExternalAccountDetail, Long> {
    Optional<AuthSchExternalAccountDetail> findByInternalId(String internalId);
}
