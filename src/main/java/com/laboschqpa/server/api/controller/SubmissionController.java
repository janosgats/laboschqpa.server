package com.laboschqpa.server.api.controller;

import com.laboschqpa.server.api.dto.CreatedEntityResponse;
import com.laboschqpa.server.api.dto.ugc.submission.CreateNewSubmissionDto;
import com.laboschqpa.server.api.dto.ugc.submission.EditSubmissionDto;
import com.laboschqpa.server.api.dto.ugc.submission.GetSubmissionResponse;
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
    public GetSubmissionResponse getSubmission(@RequestParam(name = "id") Long submissionId) {
        return new GetSubmissionResponse(submissionService.getSubmission(submissionId), true);
    }

    @PostMapping("/createNew")
    public CreatedEntityResponse postCreateNewSubmission(@RequestBody CreateNewSubmissionDto createNewSubmissionDto,
                                                         @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        createNewSubmissionDto.validateSelf();
        long newId = submissionService.createNewSubmission(createNewSubmissionDto, authenticationPrincipal.getUserAccEntity()).getId();
        return new CreatedEntityResponse(newId);
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
    public List<GetSubmissionResponse> getListAll() {
        return submissionService.listAll().stream()
                .map(GetSubmissionResponse::new)
                .collect(Collectors.toList());
    }
}
