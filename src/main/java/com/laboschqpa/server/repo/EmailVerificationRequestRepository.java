package com.laboschqpa.server.repo;

import com.laboschqpa.server.entity.EmailVerificationRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailVerificationRequestRepository extends JpaRepository<EmailVerificationRequest, Long> {
}
