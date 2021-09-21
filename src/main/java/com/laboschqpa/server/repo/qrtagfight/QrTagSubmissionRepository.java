package com.laboschqpa.server.repo.qrtagfight;

import com.laboschqpa.server.entity.Team;
import com.laboschqpa.server.entity.qrfight.QrTag;
import com.laboschqpa.server.entity.qrfight.QrTagSubmission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QrTagSubmissionRepository extends JpaRepository<QrTagSubmission, Long> {

    Optional<QrTagSubmission> findByQrTagAndTeam(QrTag qrTag, Team team);
}
