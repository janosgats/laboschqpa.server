package com.laboschqpa.server.api.service.qrtagfight;

import com.laboschqpa.server.entity.account.UserAcc;
import com.laboschqpa.server.entity.qrtagfight.QrTag;
import com.laboschqpa.server.entity.qrtagfight.QrTagSubmission;
import com.laboschqpa.server.enums.apierrordescriptor.QrTagFightApiError;
import com.laboschqpa.server.enums.apierrordescriptor.TeamMembershipApiError;
import com.laboschqpa.server.exceptions.apierrordescriptor.QrTagFightException;
import com.laboschqpa.server.exceptions.apierrordescriptor.TeamMembershipException;
import com.laboschqpa.server.repo.dto.QrTagWithSubmissionCountJpaDto;
import com.laboschqpa.server.repo.qrtagfight.QrTagRepository;
import com.laboschqpa.server.repo.qrtagfight.QrTagSubmissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Log4j2
@RequiredArgsConstructor
@Service
public class QrTagFightService {
    private final QrTagRepository qrTagRepository;
    private final QrTagSubmissionRepository qrTagSubmissionRepository;

    public List<QrTagWithSubmissionCountJpaDto> listAllTagsWithSubmissionCount() {
        return qrTagRepository.findAll_withSubmissionCount();
    }

    public void submitQrTag(UserAcc userAcc, long tagId, String submittedSecret) {
        if (!userAcc.isMemberOrLeaderOfAnyTeam()) {
            throw new TeamMembershipException(TeamMembershipApiError.YOU_ARE_NOT_IN_A_TEAM);
        }

        final QrTag qrTag = getExistingQrTag(tagId);

        if (!Objects.equals(qrTag.getSecret(), submittedSecret)) {
            throw new QrTagFightException(QrTagFightApiError.TAG_SECRET_MISMATCH);
        }

        if (qrTagSubmissionRepository.findByQrTagAndTeam(qrTag, userAcc.getTeam()).isPresent()) {
            throw new QrTagFightException(QrTagFightApiError.YOUR_TEAM_ALREADY_SUBMITTED_THIS_TAG);
        }

        saveNewSubmission(qrTag, userAcc);
    }

    private void saveNewSubmission(QrTag qrTag, UserAcc userAcc) {
        QrTagSubmission newSubmission = new QrTagSubmission();
        newSubmission.setQrTag(qrTag);
        newSubmission.setTeam(userAcc.getTeam());
        newSubmission.setSubmitterUserAcc(userAcc);
        newSubmission.setCreated(Instant.now());

        qrTagSubmissionRepository.save(newSubmission);
    }

    private QrTag getExistingQrTag(long id) {
        return qrTagRepository.findById(id)
                .orElseThrow(() -> new QrTagFightException(QrTagFightApiError.TAG_DOES_NOT_EXIST));
    }
}
