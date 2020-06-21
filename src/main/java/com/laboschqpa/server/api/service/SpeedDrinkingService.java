package com.laboschqpa.server.api.service;

import com.laboschqpa.server.api.dto.ugc.speeddrinking.CreateNewSpeedDrinkingDto;
import com.laboschqpa.server.api.dto.ugc.speeddrinking.EditSpeedDrinkingDto;
import com.laboschqpa.server.entity.account.UserAcc;
import com.laboschqpa.server.entity.usergeneratedcontent.SpeedDrinking;
import com.laboschqpa.server.exceptions.apierrordescriptor.ContentNotFoundException;
import com.laboschqpa.server.repo.UserAccRepository;
import com.laboschqpa.server.repo.usergeneratedcontent.SpeedDrinkingRepository;
import com.laboschqpa.server.util.AttachmentHelper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class SpeedDrinkingService {
    private static final Logger logger = LoggerFactory.getLogger(SpeedDrinkingService.class);

    private final SpeedDrinkingRepository speedDrinkingRepository;
    private final UserAccRepository userAccRepository;
    private final AttachmentHelper attachmentHelper;

    public SpeedDrinking getSpeedDrinking(Long newsPostId) {
        Optional<SpeedDrinking> speedDrinkingOptional = speedDrinkingRepository.findByIdWithEagerAttachments(newsPostId);

        if (speedDrinkingOptional.isEmpty())
            throw new ContentNotFoundException("Cannot find SpeedDrinking with Id: " + newsPostId);

        return speedDrinkingOptional.get();
    }

    public SpeedDrinking createSpeedDrinking(CreateNewSpeedDrinkingDto createNewSpeedDrinkingDto, UserAcc creatorUserAcc) {
        attachmentHelper.assertAllFilesExistAndAvailableOnFileHost(createNewSpeedDrinkingDto.getAttachments());
        final UserAcc drinkerUserAcc = getExistingUserAcc(createNewSpeedDrinkingDto.getDrinkerUserId());

        SpeedDrinking speedDrinking = new SpeedDrinking();
        speedDrinking.setUGCAsCreatedByUser(creatorUserAcc);
        speedDrinking.setAttachments(createNewSpeedDrinkingDto.getAttachments());

        speedDrinking.setDrinkerUserAcc(drinkerUserAcc);
        speedDrinking.setTime(createNewSpeedDrinkingDto.getTime());
        speedDrinking.setCategory(createNewSpeedDrinkingDto.getCategory());
        speedDrinking.setNote(createNewSpeedDrinkingDto.getNote());

        speedDrinkingRepository.save(speedDrinking);
        logger.info("SpeedDrinking {} created by user {}.", speedDrinking.getId(), creatorUserAcc.getId());
        return speedDrinking;
    }

    public void editSpeedDrinking(EditSpeedDrinkingDto editSpeedDrinkingDto, UserAcc editorUserAcc) {
        Long speedDrinkingId = editSpeedDrinkingDto.getId();
        Optional<SpeedDrinking> speedDrinkingOptional = speedDrinkingRepository.findById(speedDrinkingId);
        if (speedDrinkingOptional.isEmpty())
            throw new ContentNotFoundException("Cannot find SpeedDrinking with Id: " + speedDrinkingId);

        attachmentHelper.assertAllFilesExistAndAvailableOnFileHost(editSpeedDrinkingDto.getAttachments());
        final UserAcc drinkerUserAcc = getExistingUserAcc(editSpeedDrinkingDto.getDrinkerUserId());

        SpeedDrinking speedDrinking = speedDrinkingOptional.get();
        speedDrinking.setUGCAsEditedByUser(editorUserAcc);
        speedDrinking.setAttachments(editSpeedDrinkingDto.getAttachments());

        speedDrinking.setDrinkerUserAcc(drinkerUserAcc);
        speedDrinking.setTime(editSpeedDrinkingDto.getTime());
        speedDrinking.setCategory(editSpeedDrinkingDto.getCategory());
        speedDrinking.setNote(editSpeedDrinkingDto.getNote());

        speedDrinkingRepository.save(speedDrinking);

        logger.info("SpeedDrinking {} edited by user {}.", speedDrinking.getId(), editorUserAcc.getId());
    }

    @Transactional
    public void deleteSpeedDrinking(Long speedDrinkingId, UserAcc deleterUserAcc) {
        int deletedRowCount;
        if ((deletedRowCount = speedDrinkingRepository.deleteByIdAndGetDeletedRowCount(speedDrinkingId)) != 1) {
            throw new ContentNotFoundException("Count of deleted rows is " + deletedRowCount + "!");
        }

        logger.info("SpeedDrinking {} deleted by user {}.", speedDrinkingId, deleterUserAcc.getId());
    }

    public List<SpeedDrinking> listAllSpeedDrinkings() {
        return speedDrinkingRepository.findAllByOrderByCreationTimeDesc();
    }

    public UserAcc getExistingUserAcc(Long userAccId) {
        Optional<UserAcc> userAccOptional = userAccRepository.findById(userAccId);
        if (userAccOptional.isEmpty()) {
            throw new ContentNotFoundException("UserAcc is not found: " + userAccId);
        }
        return userAccOptional.get();
    }
}
