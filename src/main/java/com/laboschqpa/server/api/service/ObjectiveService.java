package com.laboschqpa.server.api.service;

import com.laboschqpa.server.api.dto.objective.CreateNewObjectiveDto;
import com.laboschqpa.server.api.dto.objective.EditObjectiveDto;
import com.laboschqpa.server.entity.account.UserAcc;
import com.laboschqpa.server.entity.usergeneratedcontent.Objective;
import com.laboschqpa.server.exceptions.ContentNotFoundApiException;
import com.laboschqpa.server.repo.ObjectiveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Log4j2
@RequiredArgsConstructor
@Service
public class ObjectiveService {
    private final ObjectiveRepository objectiveRepository;

    public Objective getObjective(Long objectiveId) {
        Optional<Objective> objectiveOptional = objectiveRepository.findById(objectiveId);

        if (objectiveOptional.isEmpty())
            throw new ContentNotFoundApiException("Cannot find Objective with Id: " + objectiveId);

        return objectiveOptional.get();
    }

    public void createNewObjective(CreateNewObjectiveDto createNewObjectiveDto, UserAcc creatorUserAcc) {
        Objective objective = new Objective();
        objective.setDescription(createNewObjectiveDto.getDescription());
        objective.setSubmittable(createNewObjectiveDto.getSubmittable());
        objective.setDeadline(createNewObjectiveDto.getDeadline());

        objective.setCreatorUser(creatorUserAcc);
        objective.setEditorUser(creatorUserAcc);
        objective.setCreationTime(Instant.now());
        objective.setEditTime(Instant.now());

        objectiveRepository.save(objective);
        log.info("Objective {} created by user {}.", objective.getId(), creatorUserAcc.getId());
    }

    public void editObjective(EditObjectiveDto editObjectiveDto, UserAcc editorUserAcc) {
        Optional<Objective> objectiveOptional = objectiveRepository.findById(editObjectiveDto.getId());
        if (objectiveOptional.isEmpty())
            throw new ContentNotFoundApiException("Cannot find Objective with Id: " + editObjectiveDto.getId());

        Objective objective = objectiveOptional.get();
        objective.setDescription(editObjectiveDto.getDescription());
        objective.setSubmittable(editObjectiveDto.getSubmittable());
        objective.setDeadline(editObjectiveDto.getDeadline());

        objective.setEditorUser(editorUserAcc);
        objective.setEditTime(Instant.now());

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
