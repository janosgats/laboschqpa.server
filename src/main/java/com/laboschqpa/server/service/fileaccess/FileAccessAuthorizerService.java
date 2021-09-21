package com.laboschqpa.server.service.fileaccess;

import com.laboschqpa.server.api.service.SubmissionService;
import com.laboschqpa.server.entity.account.UserAcc;
import com.laboschqpa.server.entity.usergeneratedcontent.Submission;
import com.laboschqpa.server.enums.TeamRole;
import com.laboschqpa.server.enums.auth.Authority;
import com.laboschqpa.server.enums.ugc.UserGeneratedContentType;
import com.laboschqpa.server.repo.dto.UserGeneratedContentParentJpaDto;
import com.laboschqpa.server.repo.usergeneratedcontent.RiddleRepository;
import com.laboschqpa.server.repo.usergeneratedcontent.SubmissionRepository;
import com.laboschqpa.server.repo.usergeneratedcontent.UserGeneratedContentRepository;
import com.laboschqpa.server.util.PrincipalAuthorizationHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Log4j2
@RequiredArgsConstructor
@Service
public class FileAccessAuthorizerService implements FileAccessAuthorizer {
    private final UserGeneratedContentRepository userGeneratedContentRepository;
    private final SubmissionRepository submissionRepository;
    private final SubmissionService submissionService;
    private final RiddleRepository riddleRepository;

    @Override
    public boolean canUserDeleteFile(UserAcc userAcc, File file) {
        if (Objects.equals(userAcc.getId(), file.getOwnerUserId())) {
            return true;
        }
        if (userAcc.getTeamRole() == TeamRole.LEADER
                && Objects.equals(userAcc.getTeam().getId(), file.getOwnerTeamId())) {
            return true;
        }
        return false;
    }

    @Override
    public boolean canUserReadFile(UserAcc userAcc, File file) {
        if (shouldBeVisibleToSpecialUser(userAcc, file)) {
            return true;
        }

        final List<UserGeneratedContentParentJpaDto> contentsFileIsAttachedTo = userGeneratedContentRepository.getOnlyParentsThatHaveAttachment(file.getId());
        if (contentsFileIsAttachedTo.isEmpty()) {
            return false;//The file is not attached to anything -> the user can't see it -> not visible
        }

        if (shouldBeVisibleToEveryone(contentsFileIsAttachedTo)) {
            return true;
        }
        if (shouldBeVisibleBySubmissions(userAcc, contentsFileIsAttachedTo)) {
            return true;
        }
        if (shouldBeVisibleByRiddles(userAcc, contentsFileIsAttachedTo)) {
            return true;
        }

        return false;
    }

    private boolean shouldBeVisibleToSpecialUser(UserAcc userAcc, File file) {
        if (new PrincipalAuthorizationHelper(userAcc).hasAdminAuthority()) {
            return true;
        }
        if (Objects.equals(userAcc.getId(), file.getOwnerUserId())) {
            return true;
        }
        if (userAcc.isMemberOrLeaderOfAnyTeam() && Objects.equals(userAcc.getTeam().getId(), file.getOwnerTeamId())) {
            return true;
        }

        return false;
    }

    private boolean shouldBeVisibleToEveryone(List<UserGeneratedContentParentJpaDto> contentsAttachedToFile) {
        for (UserGeneratedContentParentJpaDto content : contentsAttachedToFile) {
            if (content.getType() == UserGeneratedContentType.NEWS_POST
                    || content.getType() == UserGeneratedContentType.OBJECTIVE
                    || content.getType() == UserGeneratedContentType.SPEED_DRINKING) {
                return true;
            }
        }
        return false;
    }

    private boolean shouldBeVisibleBySubmissions(UserAcc userAcc, List<UserGeneratedContentParentJpaDto> contentsFileIsAttachedTo) {
        final List<Long> submissionIdsFileIsAttachedTo = contentsFileIsAttachedTo.stream()
                .filter(ugc -> ugc.getType() == UserGeneratedContentType.SUBMISSION)
                .map(UserGeneratedContentParentJpaDto::getId)
                .collect(Collectors.toList());

        if (submissionIdsFileIsAttachedTo.isEmpty()) {
            return false;//The file is not a submission attachment -> can't be visible by submissions
        }

        final List<Submission> submissionsFileIsAttachedTo = submissionRepository.findByIdIn_withObjective(submissionIdsFileIsAttachedTo);
        final List<Submission> visibleSubmissionsFileIsAttachedTo = submissionService.filterSubmissionsThatUserCanSee(submissionsFileIsAttachedTo, userAcc);

        return !visibleSubmissionsFileIsAttachedTo.isEmpty();
    }

    private boolean shouldBeVisibleByRiddles(UserAcc userAcc, List<UserGeneratedContentParentJpaDto> contentsFileIsAttachedTo) {
        if (!userAcc.isMemberOrLeaderOfAnyTeam()) {
            return false;
        }

        final List<Long> riddleIdsFileIsAttachedTo = contentsFileIsAttachedTo.stream()
                .filter(ugc -> ugc.getType() == UserGeneratedContentType.RIDDLE)
                .map(UserGeneratedContentParentJpaDto::getId)
                .collect(Collectors.toList());

        if (riddleIdsFileIsAttachedTo.isEmpty()) {
            return false;//The file is not a riddle attachment -> can't be visible by riddles
        }

        if (new PrincipalAuthorizationHelper(userAcc).hasAuthority(Authority.RiddleEditor)) {
            return true;
        }

        final List<Long> visibleRiddleIds = riddleRepository.findAccessibleRiddleIds(userAcc.getTeam().getId());
        return riddleIdsFileIsAttachedTo.stream().anyMatch(visibleRiddleIds::contains);//The attached riddles is accessible for the user
    }
}
