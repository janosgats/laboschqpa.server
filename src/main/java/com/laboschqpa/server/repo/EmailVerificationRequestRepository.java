package com.laboschqpa.server.repo;

import com.laboschqpa.server.entity.EmailVerificationRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface EmailVerificationRequestRepository extends JpaRepository<EmailVerificationRequest, Long> {
    List<EmailVerificationRequest> findAllByIdIn(Collection<Long> ids);
}
