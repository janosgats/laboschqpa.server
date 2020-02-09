package com.laboschcst.server.repo;

import com.laboschcst.server.entity.account.externalaccountdetail.GithubExternalAccountDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GithubExternalAccountDetailRepository extends JpaRepository<GithubExternalAccountDetail, Long> {
    Optional<GithubExternalAccountDetail> findByGithubId(String githubId);
}
