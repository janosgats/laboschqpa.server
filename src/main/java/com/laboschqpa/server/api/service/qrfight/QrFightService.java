package com.laboschqpa.server.api.service.qrfight;

import com.laboschqpa.server.entity.Team;
import com.laboschqpa.server.entity.account.UserAcc;
import com.laboschqpa.server.entity.qrfight.QrFightArea;
import com.laboschqpa.server.entity.qrfight.QrTag;
import com.laboschqpa.server.entity.qrfight.QrTagSubmission;
import com.laboschqpa.server.enums.TeamRateControlTopic;
import com.laboschqpa.server.enums.apierrordescriptor.QrFightApiError;
import com.laboschqpa.server.enums.apierrordescriptor.TeamMembershipApiError;
import com.laboschqpa.server.exceptions.apierrordescriptor.QrFightException;
import com.laboschqpa.server.exceptions.apierrordescriptor.TeamMembershipException;
import com.laboschqpa.server.repo.dto.qrFightArea.QrFightAreaAndTeamSubmissionCountJpaDto;
import com.laboschqpa.server.repo.dto.qrFightArea.QrFightAreaWithTagCountJpaDto;
import com.laboschqpa.server.repo.dto.qrFightArea.QrTagWithSubmissionCountJpaDto;
import com.laboschqpa.server.repo.qrtagfight.QrFightAreaRepository;
import com.laboschqpa.server.repo.qrtagfight.QrTagRepository;
import com.laboschqpa.server.repo.qrtagfight.QrTagSubmissionRepository;
import com.laboschqpa.server.service.TeamRateControlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Log4j2
@RequiredArgsConstructor
@Service
public class QrFightService {
    private final QrFightAreaRepository qrFightAreaRepository;
    private final QrTagRepository qrTagRepository;
    private final QrTagSubmissionRepository qrTagSubmissionRepository;
    private final TeamRateControlService teamRateControlService;

    public List<QrTagWithSubmissionCountJpaDto> listAllTagsWithSubmissionCount() {
        return qrTagRepository.findAll_withSubmissionCount();
    }

    public List<QrFightAreaAndTeamSubmissionCountJpaDto> listEnabledAreasWithTeamSubmissionCount() {
        return qrFightAreaRepository.findEnabledAreasWithTeamSubmissionCount();
    }

    public List<QrFightArea> listEnabledAreas() {
        return qrFightAreaRepository.findAllByEnabledIsTrueOrderByIdAsc();
    }

    public List<QrFightAreaWithTagCountJpaDto> listEnabledAreasWithTagCount() {
        return qrFightAreaRepository.findAllByEnabledIsTrue_withBelongingTagCount_orderByIdAsc();
    }

    public void submitQrTag(UserAcc userAcc, long tagId, String submittedSecret) {
        if (!userAcc.isMemberOrLeaderOfAnyTeam()) {
            throw new TeamMembershipException(TeamMembershipApiError.YOU_ARE_NOT_IN_A_TEAM);
        }
        final Team team = userAcc.getTeam();
        manageSubmissionRateLimiting(team, userAcc);

        final QrTag qrTag = getExistingQrTagWithEagerArea(tagId);

        if (!qrTag.getArea().getEnabled()) {
            throw new QrFightException(QrFightApiError.FIGHT_AREA_IS_NOT_ENABLED);
        }

        if (!Objects.equals(qrTag.getSecret(), submittedSecret)) {
            throw new QrFightException(QrFightApiError.TAG_SECRET_MISMATCH);
        }

        if (qrTagSubmissionRepository.findByQrTagAndTeam(qrTag, userAcc.getTeam()).isPresent()) {
            throw new QrFightException(QrFightApiError.YOUR_TEAM_ALREADY_SUBMITTED_THIS_TAG);
        }

        saveNewSubmission(qrTag, userAcc);
    }

    private void manageSubmissionRateLimiting(Team team, UserAcc userAcc) {
        if (!teamRateControlService.isRateLimitAlright(TeamRateControlTopic.QR_FIGHT_TAG_SUBMISSION_TRIAL, team.getId())) {
            throw new QrFightException(QrFightApiError.TEAM_RATE_LIMIT_HIT_FOR_QR_FIGHT_SUBMISSIONS);
        }
        teamRateControlService.log(TeamRateControlTopic.QR_FIGHT_TAG_SUBMISSION_TRIAL, team.getId(), userAcc.getId());
    }

    private void saveNewSubmission(QrTag qrTag, UserAcc userAcc) {
        QrTagSubmission newSubmission = new QrTagSubmission();
        newSubmission.setQrTag(qrTag);
        newSubmission.setTeam(userAcc.getTeam());
        newSubmission.setSubmitterUserAcc(userAcc);
        newSubmission.setCreated(Instant.now());

        qrTagSubmissionRepository.save(newSubmission);
    }

    private QrTag getExistingQrTagWithEagerArea(long id) {
        return qrTagRepository.findById_withEagerArea(id)
                .orElseThrow(() -> new QrFightException(QrFightApiError.TAG_DOES_NOT_EXIST));
    }
}
