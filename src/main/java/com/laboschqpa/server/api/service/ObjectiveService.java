package com.laboschqpa.server.api.service;

import com.laboschqpa.server.api.dto.ugc.objective.CreateNewObjectiveRequest;
import com.laboschqpa.server.api.dto.ugc.objective.EditObjectiveRequest;
import com.laboschqpa.server.entity.TeamScore;
import com.laboschqpa.server.entity.account.UserAcc;
import com.laboschqpa.server.entity.usergeneratedcontent.Objective;
import com.laboschqpa.server.entity.usergeneratedcontent.Program;
import com.laboschqpa.server.enums.ugc.ObjectiveType;
import com.laboschqpa.server.exceptions.apierrordescriptor.ContentNotFoundException;
import com.laboschqpa.server.repo.TeamScoreRepository;
import com.laboschqpa.server.repo.usergeneratedcontent.ObjectiveRepository;
import com.laboschqpa.server.repo.usergeneratedcontent.dto.GetObjectiveWithTeamScoreJpaDto;
import com.laboschqpa.server.repo.usergeneratedcontent.dto.ObjectiveWithTeamScoreDtoAdapter;
import com.laboschqpa.server.util.AttachmentHelper;
import com.laboschqpa.server.util.CollectionHelpers;
import com.laboschqpa.server.util.MappingHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.util.annotation.Nullable;

import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@RequiredArgsConstructor
@Service
public class ObjectiveService {
    private final ObjectiveRepository objectiveRepository;
    private final TeamScoreRepository teamScoreRepository;
    private final AttachmentHelper attachmentHelper;
    private final ProgramService programService;

    public GetObjectiveWithTeamScoreJpaDto getObjective(long objectiveId, @Nullable Long observerTeamId) {
        Optional<Objective> objectiveOptional = objectiveRepository.findByIdWithEagerAttachments(objectiveId);

        if (objectiveOptional.isEmpty())
            throw new ContentNotFoundException("Cannot find Objective with Id: " + objectiveId);

        Optional<TeamScore> teamScore = getTeamScoreOptional(objectiveId, observerTeamId);
        Integer score = null;
        if (teamScore.isPresent()) {
            score = teamScore.get().getScore();
        }
        return new ObjectiveWithTeamScoreDtoAdapter(objectiveOptional.get(), score);
    }

    private Optional<TeamScore> getTeamScoreOptional(long objectiveId, @Nullable Long observerTeamId) {
        if (observerTeamId == null) {
            return Optional.empty();
        }
        return teamScoreRepository.findByObjectiveIdAndTeamId(objectiveId, observerTeamId);
    }

    public Objective createNewObjective(CreateNewObjectiveRequest request, UserAcc creatorUserAcc) {
        final Program program = programService.getExisting(request.getProgramId());
        attachmentHelper.assertAllFilesAvailableAndHaveOwnerUserOf(request.getAttachments(), creatorUserAcc);

        Objective objective = new Objective();
        objective.setUGCAsCreatedByUser(creatorUserAcc);
        objective.setAttachments(request.getAttachments());

        objective.setProgram(program);
        objective.setTitle(request.getTitle());
        objective.setDescription(request.getDescription());
        objective.setSubmittable(request.getSubmittable());
        objective.setDeadline(request.getDeadline());
        objective.setHideSubmissionsBefore(request.getHideSubmissionsBefore());
        objective.setObjectiveType(request.getObjectiveType());

        objectiveRepository.save(objective);
        log.info("Objective {} created by user {}.", objective.getId(), creatorUserAcc.getId());
        return objective;
    }

    public void editObjective(EditObjectiveRequest request, UserAcc editorUserAcc) {
        Objective editedObjective = objectiveRepository.findByIdWithEagerAttachments(request.getId())
                .orElseThrow(() -> new ContentNotFoundException("Cannot find Objective with Id: " + request.getId()));
        final Program program = programService.getExisting(request.getProgramId());

        final HashSet<Long> newlyAddedAttachments = CollectionHelpers.subtractToSet(request.getAttachments(), editedObjective.getAttachments());
        attachmentHelper.assertAllFilesAvailableAndHaveOwnerUserOf(newlyAddedAttachments, editorUserAcc);

        editedObjective.setUGCAsEditedByUser(editorUserAcc);
        editedObjective.setAttachments(request.getAttachments());

        editedObjective.setProgram(program);
        editedObjective.setTitle(request.getTitle());
        editedObjective.setDescription(request.getDescription());
        editedObjective.setSubmittable(request.getSubmittable());
        editedObjective.setDeadline(request.getDeadline());
        editedObjective.setHideSubmissionsBefore(request.getHideSubmissionsBefore());
        editedObjective.setObjectiveType(request.getObjectiveType());

        objectiveRepository.save(editedObjective);

        log.info("Objective {} edited by user {}.", editedObjective.getId(), editorUserAcc.getId());
    }

    @Transactional
    public void deleteObjective(Long objectiveId, UserAcc deleterUserAcc) {
        int deletedRowCount;
        if ((deletedRowCount = objectiveRepository.deleteByIdAndGetDeletedRowCount(objectiveId)) != 1) {
            throw new ContentNotFoundException("Count of deleted rows is " + deletedRowCount + "!");
        }

        log.info("Objective {} deleted by user {}.", objectiveId, deleterUserAcc.getId());
    }

    public List<Objective> listAllObjectives() {
        return objectiveRepository.findAll();
    }

    public List<Objective> listObjectivesBelongingToProgram(long programId) {
        return objectiveRepository.findAllByProgramIdWithEagerAttachments(programId);
    }

    public List<GetObjectiveWithTeamScoreJpaDto> listForDisplay(Collection<ObjectiveType> objectiveTypes, @Nullable Long observerTeamId) {
        List<Objective> objectives = objectiveRepository.findAllByObjectiveType_OrderByCreationTimeDesc_withEagerAttachments(objectiveTypes);

        final List<Long> objectiveIds = objectives.stream().map(Objective::getId).collect(Collectors.toList());
        Map<Long, TeamScore> teamScoreMap = getTeamScoreMap(objectiveIds, observerTeamId);

        List<GetObjectiveWithTeamScoreJpaDto> mergedEntities = new ArrayList<>(objectives.size());
        for (var objective : objectives) {
            final TeamScore teamScore = teamScoreMap.get(objective.getId());
            Integer score = null;
            if (teamScore != null) {
                score = teamScore.getScore();
            }
            mergedEntities.add(new ObjectiveWithTeamScoreDtoAdapter(objective, score));
        }

        return mergedEntities;
    }

    private Map<Long, TeamScore> getTeamScoreMap(List<Long> objectiveIds, @Nullable Long observerTeamId) {
        if (observerTeamId == null) {
            return new HashMap<>();
        }

        List<TeamScore> teamScoreList = teamScoreRepository.findByObjectiveIdInAndTeamId(objectiveIds, observerTeamId);
        return MappingHelper.toMap(teamScoreList, ts -> ts.getObjective().getId());
    }
}
