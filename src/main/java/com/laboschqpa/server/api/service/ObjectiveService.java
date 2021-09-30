package com.laboschqpa.server.api.service;

import com.laboschqpa.server.api.dto.ugc.objective.CreateNewObjectiveRequest;
import com.laboschqpa.server.api.dto.ugc.objective.EditObjectiveRequest;
import com.laboschqpa.server.entity.account.UserAcc;
import com.laboschqpa.server.entity.usergeneratedcontent.Objective;
import com.laboschqpa.server.entity.usergeneratedcontent.Program;
import com.laboschqpa.server.enums.ugc.ObjectiveType;
import com.laboschqpa.server.exceptions.apierrordescriptor.ContentNotFoundException;
import com.laboschqpa.server.repo.ObjectiveAcceptanceRepository;
import com.laboschqpa.server.repo.usergeneratedcontent.ObjectiveRepository;
import com.laboschqpa.server.repo.usergeneratedcontent.dto.GetObjectiveWithObserverTeamDataJpaDto;
import com.laboschqpa.server.repo.usergeneratedcontent.dto.ObjectiveWithObserverTeamDataDtoAdapter;
import com.laboschqpa.server.util.AttachmentHelper;
import com.laboschqpa.server.util.CollectionHelpers;
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
    private final ObjectiveAcceptanceRepository objectiveAcceptanceRepository;
    private final AttachmentHelper attachmentHelper;
    private final ProgramService programService;

    public GetObjectiveWithObserverTeamDataJpaDto getObjective(long objectiveId, @Nullable Long observerTeamId, boolean showHiddenObjectives) {
        Objective objective = objectiveRepository.findByIdWithEagerAttachments(objectiveId, showHiddenObjectives)
                .orElseThrow(() -> new ContentNotFoundException("Cannot find Objective with Id: " + objectiveId));

        return augmentObjectivesWithObserverTeamData(List.of(objective), observerTeamId).get(0);
    }

    public Objective createNewObjective(CreateNewObjectiveRequest request, UserAcc creatorUserAcc) {
        final Program program = programService.getExisting(request.getProgramId());
        attachmentHelper.assertAllFilesAvailableAndHaveOwnerUserOf(request.getAttachments(), creatorUserAcc);

        Objective newObjective = new Objective();
        newObjective.setUGCAsCreatedByUser(creatorUserAcc);
        newObjective.setAttachments(request.getAttachments());

        newObjective.setProgram(program);
        newObjective.setTitle(request.getTitle());
        newObjective.setDescription(request.getDescription());
        newObjective.setSubmittable(request.getSubmittable());
        newObjective.setDeadline(request.getDeadline());
        newObjective.setHideSubmissionsBefore(request.getHideSubmissionsBefore());
        newObjective.setObjectiveType(request.getObjectiveType());
        newObjective.setIsHidden(request.getIsHidden());

        objectiveRepository.save(newObjective);
        log.info("Objective {} created by user {}.", newObjective.getId(), creatorUserAcc.getId());
        return newObjective;
    }

    public void editObjective(EditObjectiveRequest request, UserAcc editorUserAcc) {
        Objective editedObjective = objectiveRepository.findByIdWithEagerAttachments(request.getId(), true)
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
        editedObjective.setIsHidden(request.getIsHidden());

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

    public List<Objective> listAllObjectives(boolean showHiddenObjectives) {
        return objectiveRepository.findAll(showHiddenObjectives);
    }

    public List<GetObjectiveWithObserverTeamDataJpaDto> listObjectivesBelongingToProgram(long programId,
                                                                                         @Nullable Long observerTeamId, boolean showHiddenObjectives) {
        List<Objective> objectives = objectiveRepository.findAllByProgramIdWithEagerAttachments(programId, showHiddenObjectives);
        return augmentObjectivesWithObserverTeamData(objectives, observerTeamId);
    }

    public List<GetObjectiveWithObserverTeamDataJpaDto> listObjectivesBelongingToProgram(long programId, ObjectiveType objectiveType,
                                                                                         @Nullable Long observerTeamId, boolean showHiddenObjectives) {
        List<Objective> objectives = objectiveRepository.findAllByProgramIdAndObjectiveTypeWithEagerAttachments(programId, objectiveType, showHiddenObjectives);
        return augmentObjectivesWithObserverTeamData(objectives, observerTeamId);
    }

    public List<GetObjectiveWithObserverTeamDataJpaDto> listForDisplay(Collection<ObjectiveType> objectiveTypes, @Nullable Long observerTeamId,
                                                                       boolean showHiddenObjectives) {
        List<Objective> objectives = objectiveRepository.findAllByObjectiveType_OrderByCreationTimeDesc_withEagerAttachmentsAndProgram(objectiveTypes, showHiddenObjectives);
        return augmentObjectivesWithObserverTeamData(objectives, observerTeamId);
    }

    private List<GetObjectiveWithObserverTeamDataJpaDto> augmentObjectivesWithObserverTeamData(Collection<Objective> objectives, @Nullable Long observerTeamId) {
        final List<Long> objectiveIds = objectives.stream().map(Objective::getId).collect(Collectors.toList());
        Set<Long> objectiveIdsThatAreAccepted = getObjectiveIdsThatAreAcceptedForTeam(objectiveIds, observerTeamId);
        Set<Long> objectiveIdsThatHaveSubmission = getObjectiveIdsThatHaveSubmissionByTeam(objectiveIds, observerTeamId);

        List<GetObjectiveWithObserverTeamDataJpaDto> mergedEntities = new ArrayList<>(objectives.size());
        for (var augmentedObjective : objectives) {
            final boolean isAccepted = objectiveIdsThatAreAccepted.contains(augmentedObjective.getId());
            final boolean hasSubmission = objectiveIdsThatHaveSubmission.contains(augmentedObjective.getId());

            mergedEntities.add(new ObjectiveWithObserverTeamDataDtoAdapter(augmentedObjective, isAccepted, hasSubmission));
        }

        return mergedEntities;
    }

    private Set<Long> getObjectiveIdsThatAreAcceptedForTeam(List<Long> objectiveIds, @Nullable Long observerTeamId) {
        if (observerTeamId == null) {
            return new HashSet<>();
        }

        return objectiveAcceptanceRepository.filterObjectiveIdsThatAreAcceptedForTeam(objectiveIds, observerTeamId);
    }

    private Set<Long> getObjectiveIdsThatHaveSubmissionByTeam(List<Long> objectiveIds, @Nullable Long observerTeamId) {
        if (observerTeamId == null) {
            return new HashSet<>();
        }

        return objectiveRepository.filterObjectiveIdsThatHaveAtLeastOneSubmissionByTeam(objectiveIds, observerTeamId);
    }
}
