package com.laboschqpa.server.api.controller;

import com.laboschqpa.server.api.dto.CreatedEntityResponse;
import com.laboschqpa.server.api.dto.ugc.submission.CreateNewSubmissionDto;
import com.laboschqpa.server.api.dto.ugc.submission.DisplayListSubmissionRequest;
import com.laboschqpa.server.api.dto.ugc.submission.EditSubmissionDto;
import com.laboschqpa.server.api.dto.ugc.submission.GetSubmissionResponse;
import com.laboschqpa.server.api.service.SubmissionService;
import com.laboschqpa.server.config.userservice.CustomOauth2User;
import com.laboschqpa.server.entity.usergeneratedcontent.Submission;
import com.laboschqpa.server.enums.apierrordescriptor.SubmissionApiError;
import com.laboschqpa.server.exceptions.apierrordescriptor.SubmissionException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/submission")
public class SubmissionController {
    private final SubmissionService submissionService;

    @GetMapping("/submission")
    public GetSubmissionResponse getSubmission(@RequestParam(name = "id") Long submissionId,
                                               @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        Submission submission = submissionService.getSubmissionWithEagerDisplayEntities(submissionId);

        Optional<Submission> filteredSubmission
                = submissionService.filterSubmissionsThatUserCanSee(List.of(submission), authenticationPrincipal)
                .stream().findFirst();

        if (filteredSubmission.isEmpty()) {
            throw new SubmissionException(SubmissionApiError.SUBMISSION_IS_NOT_PUBLIC_YET);
        }

        return new GetSubmissionResponse(filteredSubmission.get(), true, true);
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

    @PostMapping("/display/list")
    public List<GetSubmissionResponse> getDisplayList(@RequestBody DisplayListSubmissionRequest request,
                                                      @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        request.validateSelf();

        final List<Submission> submissions = submissionService.listWithEagerDisplayEntities(request);

        return submissionService.filterSubmissionsThatUserCanSee(submissions, authenticationPrincipal).stream()
                .map(s -> new GetSubmissionResponse(s, true, true))
                .collect(Collectors.toList());
    }
}
