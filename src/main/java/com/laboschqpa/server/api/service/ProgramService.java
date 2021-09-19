package com.laboschqpa.server.api.service;

import com.laboschqpa.server.api.dto.ugc.program.CreateNewProgramRequest;
import com.laboschqpa.server.api.dto.ugc.program.EditProgramRequest;
import com.laboschqpa.server.entity.account.UserAcc;
import com.laboschqpa.server.entity.usergeneratedcontent.Program;
import com.laboschqpa.server.exceptions.apierrordescriptor.ContentNotFoundException;
import com.laboschqpa.server.repo.usergeneratedcontent.ProgramRepository;
import com.laboschqpa.server.repo.usergeneratedcontent.dto.GetProgramWithTeamScoreJpaDto;
import com.laboschqpa.server.util.AttachmentHelper;
import com.laboschqpa.server.util.CollectionHelpers;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

@Log4j2
@RequiredArgsConstructor
@Service
public class ProgramService {
    private final ProgramRepository programRepository;
    private final AttachmentHelper attachmentHelper;

    public Program getWithAttachments(long programId) {
        return programRepository.findByIdWithEagerAttachments(programId)
                .orElseThrow(() -> new ContentNotFoundException("Cannot find Program with Id: " + programId));
    }

    public Program getExisting(long programId) {
        return programRepository.findById(programId)
                .orElseThrow(() -> new ContentNotFoundException("Cannot find Program with Id: " + programId));
    }

    public int getTeamScore(long programId, long teamId) {
        return programRepository.getTeamScore(programId, teamId);
    }

    public Program create(CreateNewProgramRequest request, UserAcc creatorUserAcc) {
        attachmentHelper.assertAllFilesAvailableAndHaveOwnerUserOf(request.getAttachments(), creatorUserAcc);

        Program newProgram = new Program();
        newProgram.setUGCAsCreatedByUser(creatorUserAcc);
        newProgram.setAttachments(request.getAttachments());

        newProgram.setTitle(request.getTitle());
        newProgram.setHeadline(request.getHeadline());
        newProgram.setDescription(request.getDescription());
        newProgram.setStartTime(request.getStartTime());
        newProgram.setEndTime(request.getEndTime());

        programRepository.save(newProgram);
        log.info("Program {} created by user {}.", newProgram.getId(), creatorUserAcc.getId());
        return newProgram;
    }

    public void edit(EditProgramRequest request, UserAcc editorUserAcc) {
        Long programId = request.getId();
        Program editedProgram = programRepository.findByIdWithEagerAttachments(programId)
                .orElseThrow(() -> new ContentNotFoundException("Cannot find Program with Id: " + programId));

        final HashSet<Long> newlyAddedAttachments = CollectionHelpers.subtractToSet(request.getAttachments(), editedProgram.getAttachments());
        attachmentHelper.assertAllFilesAvailableAndHaveOwnerUserOf(newlyAddedAttachments, editorUserAcc);

        editedProgram.setUGCAsEditedByUser(editorUserAcc);
        editedProgram.setAttachments(request.getAttachments());

        editedProgram.setTitle(request.getTitle());
        editedProgram.setHeadline(request.getHeadline());
        editedProgram.setDescription(request.getDescription());
        editedProgram.setStartTime(request.getStartTime());
        editedProgram.setEndTime(request.getEndTime());

        programRepository.save(editedProgram);

        log.info("Program {} edited by user {}.", editedProgram.getId(), editorUserAcc.getId());
    }

    @Transactional
    public void delete(Long programId, UserAcc deleterUserAcc) {
        int deletedRowCount;
        if ((deletedRowCount = programRepository.deleteByIdAndGetDeletedRowCount(programId)) != 1) {
            throw new ContentNotFoundException("Count of deleted rows is " + deletedRowCount + "!");
        }

        log.info("Program {} deleted by user {}.", programId, deleterUserAcc.getId());
    }

    public List<Program> listAll() {
        return programRepository.findAllByOrderByStartTimeAsc();
    }

    public List<GetProgramWithTeamScoreJpaDto> listAllWithTeamScore(long teamId) {
        return programRepository.findAll_withTeamScore_orderByStartTimeAsc(teamId);
    }
}
