package com.laboschqpa.server.api.controller;

import com.laboschqpa.server.api.dto.CreatedEntityResponse;
import com.laboschqpa.server.api.dto.ugc.objective.CreateNewObjectiveRequest;
import com.laboschqpa.server.api.dto.ugc.objective.EditObjectiveRequest;
import com.laboschqpa.server.api.dto.ugc.objective.GetObjectiveResponse;
import com.laboschqpa.server.api.dto.ugc.objective.ListObjectivesRequest;
import com.laboschqpa.server.api.service.ObjectiveService;
import com.laboschqpa.server.config.userservice.CustomOauth2User;
import com.laboschqpa.server.enums.auth.Authority;
import com.laboschqpa.server.util.PrincipalAuthorizationHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/objective")
public class ObjectiveController {
    private final ObjectiveService objectiveService;

    @GetMapping("/objective")
    public GetObjectiveResponse getObjective(@RequestParam(name = "id") Long objectiveId) {
        return new GetObjectiveResponse(objectiveService.getObjective(objectiveId), true);
    }

    @GetMapping("/listAll")
    public List<GetObjectiveResponse> getListAllObjectives() {
        return objectiveService.listAllObjectives().stream()
                .map(GetObjectiveResponse::new)
                .collect(Collectors.toList());
    }

    @PostMapping("/listWithAttachments")
    public List<GetObjectiveResponse> getListAllWithAttachments(@RequestBody ListObjectivesRequest request) {
        request.validateSelf();
        return objectiveService.listWithAttachments(request.getObjectiveTypes()).stream()
                .map(o -> new GetObjectiveResponse(o, true))
                .collect(Collectors.toList());
    }

    @PostMapping("/createNew")
    public CreatedEntityResponse postCreateNewObjective(@RequestBody CreateNewObjectiveRequest createNewObjectiveRequest,
                                                        @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        createNewObjectiveRequest.validateSelf();
        new PrincipalAuthorizationHelper(authenticationPrincipal).assertHasAnySufficientAuthority(Authority.ObjectiveEditor, Authority.Admin);
        long newId = objectiveService.createNewObjective(createNewObjectiveRequest, authenticationPrincipal.getUserAccEntity()).getId();
        return new CreatedEntityResponse(newId);
    }

    @PostMapping("/edit")
    public void postEditObjective(@RequestBody EditObjectiveRequest editObjectiveRequest,
                                  @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        editObjectiveRequest.validateSelf();
        new PrincipalAuthorizationHelper(authenticationPrincipal).assertHasAnySufficientAuthority(Authority.ObjectiveEditor, Authority.Admin);
        objectiveService.editObjective(editObjectiveRequest, authenticationPrincipal.getUserAccEntity());
    }

    @DeleteMapping("/delete")
    public void deleteObjective(@RequestParam(name = "id") Long objectiveId,
                                @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        new PrincipalAuthorizationHelper(authenticationPrincipal).assertHasAnySufficientAuthority(Authority.ObjectiveEditor, Authority.Admin);
        objectiveService.deleteObjective(objectiveId, authenticationPrincipal.getUserAccEntity());
    }
}
