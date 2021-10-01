package com.laboschqpa.server.api.service;

import com.laboschqpa.server.api.dto.ugc.riddleeditor.CreateNewRiddleRequest;
import com.laboschqpa.server.api.dto.ugc.riddleeditor.EditRiddleRequest;
import com.laboschqpa.server.entity.account.UserAcc;
import com.laboschqpa.server.entity.usergeneratedcontent.Riddle;
import com.laboschqpa.server.enums.RiddleCategory;
import com.laboschqpa.server.enums.apierrordescriptor.RiddleApiError;
import com.laboschqpa.server.exceptions.apierrordescriptor.ContentNotFoundException;
import com.laboschqpa.server.exceptions.apierrordescriptor.RiddleException;
import com.laboschqpa.server.repo.RiddleResolutionRepository;
import com.laboschqpa.server.repo.event.dto.RiddleTeamProgressJpaDto;
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
    private final RiddleResolutionRepository riddleResolutionRepository;
    private final AttachmentHelper attachmentHelper;

    public Riddle getRiddle(Long riddleId) {
        Optional<Riddle> riddleOptional = riddleRepository.findByIdWithEagerAttachments(riddleId);

        if (riddleOptional.isEmpty())
            throw new ContentNotFoundException("Cannot find Riddle with Id: " + riddleId);

        return riddleOptional.get();
    }

    public Riddle createNewRiddle(CreateNewRiddleRequest request, UserAcc creatorUserAcc) {
        attachmentHelper.assertAllFilesAvailableAndHaveOwnerUserOf(request.getAttachments(), creatorUserAcc, true);

        if (request.getAttachments().size() != 1) {
            throw new RiddleException(RiddleApiError.A_RIDDLE_HAS_TO_HAVE_EXACTLY_ONE_ATTACHMENT);
        }

        Riddle riddle = new Riddle();
        riddle.setUGCAsCreatedByUser(creatorUserAcc);
        riddle.setAttachments(request.getAttachments());

        riddle.setTitle(request.getTitle());
        riddle.setCategory(request.getCategory());
        riddle.setHint(request.getHint());
        riddle.setSolution(request.getSolution());

        riddleRepository.save(riddle);
        log.info("Riddle {} created by user {}.", riddle.getId(), creatorUserAcc.getId());
        return riddle;
    }

    public void editRiddle(EditRiddleRequest request, UserAcc editorUserAcc) {
        Optional<Riddle> riddleOptional = riddleRepository.findById(request.getId());
        if (riddleOptional.isEmpty())
            throw new ContentNotFoundException("Cannot find Riddle with Id: " + request.getId());

        attachmentHelper.assertAllFilesAvailableAndHaveOwnerUserOf(request.getAttachments(), editorUserAcc, true);
        if (request.getAttachments().size() != 1) {
            throw new RiddleException(RiddleApiError.A_RIDDLE_HAS_TO_HAVE_EXACTLY_ONE_ATTACHMENT);
        }

        Riddle riddle = riddleOptional.get();
        riddle.setUGCAsEditedByUser(editorUserAcc);
        riddle.setAttachments(request.getAttachments());

        riddle.setTitle(request.getTitle());
        riddle.setCategory(request.getCategory());
        riddle.setHint(request.getHint());
        riddle.setSolution(request.getSolution());

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

    public List<Riddle> listAllRiddlesInCategory(RiddleCategory category) {
        return riddleRepository.findAllByCategory(category);
    }

    public List<RiddleTeamProgressJpaDto> listProgressOfTeams() {
        return riddleResolutionRepository.listProgressOfTeams();
    }
}
