package com.laboschqpa.server.api.service;

import com.laboschqpa.server.api.dto.ugc.objective.CreateNewObjectiveRequest;
import com.laboschqpa.server.api.dto.ugc.objective.EditObjectiveRequest;
import com.laboschqpa.server.entity.account.UserAcc;
import com.laboschqpa.server.entity.usergeneratedcontent.Objective;
import com.laboschqpa.server.enums.ugc.ObjectiveType;
import com.laboschqpa.server.exceptions.apierrordescriptor.ContentNotFoundException;
import com.laboschqpa.server.repo.usergeneratedcontent.ObjectiveRepository;
import com.laboschqpa.server.util.AttachmentHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Log4j2
@RequiredArgsConstructor
@Service
public class ObjectiveService {
    private final ObjectiveRepository objectiveRepository;
    private final AttachmentHelper attachmentHelper;

    public Objective getObjective(Long objectiveId) {
        Optional<Objective> objectiveOptional = objectiveRepository.findByIdWithEagerAttachments(objectiveId);

        if (objectiveOptional.isEmpty())
            throw new ContentNotFoundException("Cannot find Objective with Id: " + objectiveId);

        return objectiveOptional.get();
    }

    public Objective createNewObjective(CreateNewObjectiveRequest createNewObjectiveRequest, UserAcc creatorUserAcc) {
        attachmentHelper.assertAllFilesAvailableAndHaveOwnerUserOf(createNewObjectiveRequest.getAttachments(), creatorUserAcc.getId());

        Objective objective = new Objective();
        objective.setUGCAsCreatedByUser(creatorUserAcc);
        objective.setAttachments(createNewObjectiveRequest.getAttachments());

        objective.setTitle(createNewObjectiveRequest.getTitle());
        objective.setDescription(createNewObjectiveRequest.getDescription());
        objective.setSubmittable(createNewObjectiveRequest.getSubmittable());
        objective.setDeadline(createNewObjectiveRequest.getDeadline());
        objective.setHideSubmissionsBefore(createNewObjectiveRequest.getHideSubmissionsBefore());
        objective.setObjectiveType(createNewObjectiveRequest.getObjectiveType());

        objectiveRepository.save(objective);
        log.info("Objective {} created by user {}.", objective.getId(), creatorUserAcc.getId());
        return objective;
    }

    public void editObjective(EditObjectiveRequest editObjectiveRequest, UserAcc editorUserAcc) {
        Optional<Objective> objectiveOptional = objectiveRepository.findById(editObjectiveRequest.getId());
        if (objectiveOptional.isEmpty())
            throw new ContentNotFoundException("Cannot find Objective with Id: " + editObjectiveRequest.getId());

        attachmentHelper.assertAllFilesAvailableAndHaveOwnerUserOf(editObjectiveRequest.getAttachments(), editorUserAcc.getId());

        Objective objective = objectiveOptional.get();
        objective.setUGCAsEditedByUser(editorUserAcc);
        objective.setAttachments(editObjectiveRequest.getAttachments());

        objective.setTitle(editObjectiveRequest.getTitle());
        objective.setDescription(editObjectiveRequest.getDescription());
        objective.setSubmittable(editObjectiveRequest.getSubmittable());
        objective.setDeadline(editObjectiveRequest.getDeadline());
        objective.setHideSubmissionsBefore(editObjectiveRequest.getHideSubmissionsBefore());
        objective.setObjectiveType(editObjectiveRequest.getObjectiveType());

        objectiveRepository.save(objective);

        log.info("Objective {} edited by user {}.", objective.getId(), editorUserAcc.getId());
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

    public List<Objective> listWithAttachments(Collection<ObjectiveType> objectiveTypes) {
        return objectiveRepository.findAllByObjectiveType_OrderByCreationTimeDesc_withEagerAttachments(objectiveTypes);
    }
}
