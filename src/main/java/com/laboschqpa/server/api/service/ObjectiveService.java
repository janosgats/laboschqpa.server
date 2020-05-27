package com.laboschqpa.server.api.service;

import com.laboschqpa.server.api.dto.ugc.objective.CreateNewObjectiveDto;
import com.laboschqpa.server.api.dto.ugc.objective.EditObjectiveDto;
import com.laboschqpa.server.entity.account.UserAcc;
import com.laboschqpa.server.entity.usergeneratedcontent.Objective;
import com.laboschqpa.server.exceptions.ContentNotFoundApiException;
import com.laboschqpa.server.repo.usergeneratedcontent.ObjectiveRepository;
import com.laboschqpa.server.util.AttachmentHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            throw new ContentNotFoundApiException("Cannot find Objective with Id: " + objectiveId);

        return objectiveOptional.get();
    }

    public Objective createNewObjective(CreateNewObjectiveDto createNewObjectiveDto, UserAcc creatorUserAcc) {
        attachmentHelper.assertAllFilesExistAndAvailableOnFileHost(createNewObjectiveDto.getAttachments());

        Objective objective = new Objective();
        objective.setUGCAsCreatedByUser(creatorUserAcc);
        objective.setAttachments(createNewObjectiveDto.getAttachments());

        objective.setDescription(createNewObjectiveDto.getDescription());
        objective.setSubmittable(createNewObjectiveDto.getSubmittable());
        objective.setDeadline(createNewObjectiveDto.getDeadline());

        objectiveRepository.save(objective);
        log.info("Objective {} created by user {}.", objective.getId(), creatorUserAcc.getId());
        return objective;
    }

    public void editObjective(EditObjectiveDto editObjectiveDto, UserAcc editorUserAcc) {
        Optional<Objective> objectiveOptional = objectiveRepository.findById(editObjectiveDto.getId());
        if (objectiveOptional.isEmpty())
            throw new ContentNotFoundApiException("Cannot find Objective with Id: " + editObjectiveDto.getId());

        attachmentHelper.assertAllFilesExistAndAvailableOnFileHost(editObjectiveDto.getAttachments());

        Objective objective = objectiveOptional.get();
        objective.setUGCAsEditedByUser(editorUserAcc);
        objective.setAttachments(editObjectiveDto.getAttachments());

        objective.setDescription(editObjectiveDto.getDescription());
        objective.setSubmittable(editObjectiveDto.getSubmittable());
        objective.setDeadline(editObjectiveDto.getDeadline());

        objectiveRepository.save(objective);

        log.info("Objective {} edited by user {}.", objective.getId(), editorUserAcc.getId());
    }

    @Transactional
    public void deleteObjective(Long objectiveId, UserAcc deleterUserAcc) {
        int deletedRowCount;
        if ((deletedRowCount = objectiveRepository.deleteByIdAndGetDeletedRowCount(objectiveId)) != 1) {
            throw new ContentNotFoundApiException("Count of deleted rows is " + deletedRowCount + "!");
        }

        log.info("Objective {} deleted by user {}.", objectiveId, deleterUserAcc.getId());
    }

    public List<Objective> listAllObjectives() {
        return objectiveRepository.findAll();
    }
}
