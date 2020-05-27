package com.laboschqpa.server.api.controller;

import com.laboschqpa.server.api.dto.ugc.submission.CreateNewSubmissionDto;
import com.laboschqpa.server.api.dto.ugc.submission.EditSubmissionDto;
import com.laboschqpa.server.api.dto.ugc.submission.GetSubmissionDto;
import com.laboschqpa.server.api.service.SubmissionService;
import com.laboschqpa.server.config.userservice.CustomOauth2User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/submission")
public class SubmissionController {
    private final SubmissionService submissionService;

    @GetMapping("/submission")
    public GetSubmissionDto getSubmission(@RequestParam(name = "id") Long submissionId) {
        return new GetSubmissionDto(submissionService.getSubmission(submissionId), true);
    }

    @PostMapping("/createNew")
    public Long postCreateNewSubmission(@RequestBody CreateNewSubmissionDto createNewSubmissionDto,
                                        @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        createNewSubmissionDto.validateSelf();
        return submissionService.createNewSubmission(createNewSubmissionDto, authenticationPrincipal.getUserAccEntity()).getId();
    }

    @PostMapping("/edit")
    public void postEditSubmission(@RequestBody EditSubmissionDto editSubmissionDto,
                                   @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        editSubmissionDto.validateSelf();
        submissionService.editSubmission(editSubmissionDto, authenticationPrincipal.getUserAccEntity());
    }

    @DeleteMapping("/delete")
    public void deleteDeleteSubmission(@RequestParam("id") Long submissionIdToDelete,
                                       @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        submissionService.deleteSubmission(submissionIdToDelete, authenticationPrincipal.getUserAccEntity());
    }

    @GetMapping("/listAll")
    public List<GetSubmissionDto> getListAll() {
        return submissionService.listAll().stream()
                .map(GetSubmissionDto::new)
                .collect(Collectors.toList());
    }
}
