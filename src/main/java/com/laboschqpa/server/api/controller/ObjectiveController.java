package com.laboschqpa.server.api.controller;

import com.laboschqpa.server.api.dto.CreatedEntityResponse;
import com.laboschqpa.server.api.dto.ugc.objective.CreateNewObjectiveRequest;
import com.laboschqpa.server.api.dto.ugc.objective.EditObjectiveRequest;
import com.laboschqpa.server.api.dto.ugc.objective.GetObjectiveResponse;
import com.laboschqpa.server.api.dto.ugc.objective.ListObjectivesForDisplayRequest;
import com.laboschqpa.server.api.service.ObjectiveService;
import com.laboschqpa.server.config.userservice.CustomOauth2User;
import com.laboschqpa.server.entity.usergeneratedcontent.Objective;
import com.laboschqpa.server.enums.auth.Authority;
import com.laboschqpa.server.enums.ugc.ObjectiveType;
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
    public GetObjectiveResponse getObjective(@RequestParam(name = "id") Long objectiveId,
                                             @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        final Long observerTeamId = extractObserverTeamId(authenticationPrincipal);
        final boolean showFractionObjectives = shouldShowFractionObjectives(authenticationPrincipal);

        return new GetObjectiveResponse(objectiveService.getObjective(objectiveId, observerTeamId, showFractionObjectives), true);
    }

    @GetMapping("/listAll")
    public List<GetObjectiveResponse> getListAllObjectives(@AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        final boolean showFractionObjectives = shouldShowFractionObjectives(authenticationPrincipal);

        return objectiveService.listAllObjectives(showFractionObjectives).stream()
                .map(GetObjectiveResponse::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/listObjectivesBelongingToProgram")
    public List<GetObjectiveResponse> getListObjectivesBelongingToProgram(@RequestParam(name = "programId") Long programId,
                                                                          @RequestParam(name = "objectiveType", required = false) ObjectiveType objectiveType,
                                                                          @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        final boolean showFractionObjectives = shouldShowFractionObjectives(authenticationPrincipal);

        final List<Objective> objectives;
        if (objectiveType != null) {
            objectives = objectiveService.listObjectivesBelongingToProgram(programId, objectiveType, showFractionObjectives);
        } else {
            objectives = objectiveService.listObjectivesBelongingToProgram(programId, showFractionObjectives);
        }
        return objectives.stream()
                .map(o -> new GetObjectiveResponse(o, true))
                .collect(Collectors.toList());
    }

    @PostMapping("/listForDisplay")
    public List<GetObjectiveResponse> postListForDisplay(@RequestBody ListObjectivesForDisplayRequest request,
                                                         @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        request.validateSelf();
        final Long observerTeamId = extractObserverTeamId(authenticationPrincipal);
        final boolean showFractionObjectives = shouldShowFractionObjectives(authenticationPrincipal);

        return objectiveService.listForDisplay(request.getObjectiveTypes(), observerTeamId, showFractionObjectives).stream()
                .map(o -> new GetObjectiveResponse(o, true))
                .collect(Collectors.toList());
    }

    @PostMapping("/createNew")
    public CreatedEntityResponse postCreateNewObjective(@RequestBody CreateNewObjectiveRequest createNewObjectiveRequest,
                                                        @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        createNewObjectiveRequest.validateSelf();
        new PrincipalAuthorizationHelper(authenticationPrincipal).assertHasAuthority(Authority.ObjectiveEditor);
        long newId = objectiveService.createNewObjective(createNewObjectiveRequest, authenticationPrincipal.getUserAccEntity()).getId();
        return new CreatedEntityResponse(newId);
    }

    @PostMapping("/edit")
    public void postEditObjective(@RequestBody EditObjectiveRequest editObjectiveRequest,
                                  @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        editObjectiveRequest.validateSelf();
        new PrincipalAuthorizationHelper(authenticationPrincipal).assertHasAuthority(Authority.ObjectiveEditor);
        objectiveService.editObjective(editObjectiveRequest, authenticationPrincipal.getUserAccEntity());
    }

    @DeleteMapping("/delete")
    public void deleteObjective(@RequestParam(name = "id") Long objectiveId,
                                @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        new PrincipalAuthorizationHelper(authenticationPrincipal).assertHasAuthority(Authority.ObjectiveEditor);
        objectiveService.deleteObjective(objectiveId, authenticationPrincipal.getUserAccEntity());
    }

    private Long extractObserverTeamId(CustomOauth2User authenticationPrincipal) {
        if (authenticationPrincipal.getUserAccEntity().isMemberOrLeaderOfAnyTeam()) {
            return authenticationPrincipal.getUserAccEntity().getTeam().getId();
        }
        return null;
    }

    private boolean shouldShowFractionObjectives(CustomOauth2User authenticationPrincipal) {
        return new PrincipalAuthorizationHelper(authenticationPrincipal).hasAnySufficientAuthority(Authority.ObjectiveEditor, Authority.TeamScorer);
    }
}
