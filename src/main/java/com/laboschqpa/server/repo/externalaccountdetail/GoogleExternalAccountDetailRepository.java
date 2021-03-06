package com.laboschqpa.server.repo.externalaccountdetail;

import com.laboschqpa.server.entity.account.externalaccountdetail.GoogleExternalAccountDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GoogleExternalAccountDetailRepository extends JpaRepository<GoogleExternalAccountDetail, Long> {
    Optional<GoogleExternalAccountDetail> findBySub(String sub);
}
