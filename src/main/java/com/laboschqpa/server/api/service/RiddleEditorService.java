package com.laboschqpa.server.api.service;

import com.laboschqpa.server.api.dto.ugc.riddleeditor.CreateNewRiddleRequest;
import com.laboschqpa.server.api.dto.ugc.riddleeditor.EditRiddleRequest;
import com.laboschqpa.server.entity.account.UserAcc;
import com.laboschqpa.server.entity.usergeneratedcontent.Riddle;
import com.laboschqpa.server.exceptions.apierrordescriptor.ContentNotFoundException;
import com.laboschqpa.server.repo.usergeneratedcontent.RiddleRepository;
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
public class RiddleEditorService {
    private final RiddleRepository riddleRepository;
    private final AttachmentHelper attachmentHelper;

    public Riddle getRiddle(Long riddleId) {
        Optional<Riddle> riddleOptional = riddleRepository.findByIdWithEagerAttachments(riddleId);

        if (riddleOptional.isEmpty())
            throw new ContentNotFoundException("Cannot find Riddle with Id: " + riddleId);

        return riddleOptional.get();
    }

    public Riddle createNewRiddle(CreateNewRiddleRequest createNewRiddleRequest, UserAcc creatorUserAcc) {
        attachmentHelper.assertAllFilesAvailableAndHaveOwnerUserOf(createNewRiddleRequest.getAttachments(), creatorUserAcc.getId());

        Riddle riddle = new Riddle();
        riddle.setUGCAsCreatedByUser(creatorUserAcc);
        riddle.setAttachments(createNewRiddleRequest.getAttachments());

        riddle.setTitle(createNewRiddleRequest.getTitle());
        riddle.setHint(createNewRiddleRequest.getHint());
        riddle.setSolution(createNewRiddleRequest.getSolution());

        riddleRepository.save(riddle);
        log.info("Riddle {} created by user {}.", riddle.getId(), creatorUserAcc.getId());
        return riddle;
    }

    public void editRiddle(EditRiddleRequest editRiddleRequest, UserAcc editorUserAcc) {
        Optional<Riddle> riddleOptional = riddleRepository.findById(editRiddleRequest.getId());
        if (riddleOptional.isEmpty())
            throw new ContentNotFoundException("Cannot find Riddle with Id: " + editRiddleRequest.getId());

        attachmentHelper.assertAllFilesAvailableAndHaveOwnerUserOf(editRiddleRequest.getAttachments(), editorUserAcc.getId());

        Riddle riddle = riddleOptional.get();
        riddle.setUGCAsEditedByUser(editorUserAcc);
        riddle.setAttachments(editRiddleRequest.getAttachments());

        riddle.setTitle(editRiddleRequest.getTitle());
        riddle.setHint(editRiddleRequest.getHint());
        riddle.setSolution(editRiddleRequest.getSolution());

        riddleRepository.save(riddle);

        log.info("Riddle {} edited by user {}.", riddle.getId(), editorUserAcc.getId());
    }

    @Transactional
    public void deleteRiddle(Long riddleId, UserAcc deleterUserAcc) {
        int deletedRowCount;
        if ((deletedRowCount = riddleRepository.deleteByIdAndGetDeletedRowCount(riddleId)) != 1) {
            throw new ContentNotFoundException("Count of deleted rows is " + deletedRowCount + "!");
        }

        log.info("Riddle {} deleted by user {}.", riddleId, deleterUserAcc.getId());
    }

    public List<Riddle> listAllRiddles() {
        return riddleRepository.findAll();
    }
}
