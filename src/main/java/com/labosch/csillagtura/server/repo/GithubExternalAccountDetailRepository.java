package com.labosch.csillagtura.server.repo;

import com.labosch.csillagtura.server.entity.externalaccount.GithubExternalAccountDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GithubExternalAccountDetailRepository extends JpaRepository<GithubExternalAccountDetail, Long> {
    Optional<GithubExternalAccountDetail> findByGithubId(String githubId);
}
