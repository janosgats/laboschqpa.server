package com.laboschqpa.server.api.controller;

import com.laboschqpa.server.api.dto.objectiveacceptance.GetObjectiveAcceptanceResponse;
import com.laboschqpa.server.api.dto.objectiveacceptance.SetObjectiveAcceptanceRequest;
import com.laboschqpa.server.api.service.ObjectiveAcceptanceService;
import com.laboschqpa.server.config.userservice.CustomOauth2User;
import com.laboschqpa.server.enums.auth.Authority;
import com.laboschqpa.server.util.PrincipalAuthorizationHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/objectiveAcceptance")
public class ObjectiveAcceptanceController {
    private final ObjectiveAcceptanceService objectiveAcceptanceService;

    @GetMapping("/isAccepted")
    public GetObjectiveAcceptanceResponse getTeamScore(@RequestParam(name = "objectiveId") Long objectiveId,
                                                       @RequestParam(name = "teamId") Long teamId) {
        return new GetObjectiveAcceptanceResponse(objectiveAcceptanceService.isAccepted(objectiveId, teamId));
    }

    @PostMapping("/setAcceptance")
    public void postCreateNewTeamScore(@RequestBody SetObjectiveAcceptanceRequest request,
                                       @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        request.validateSelf();
        new PrincipalAuthorizationHelper(authenticationPrincipal).assertHasAuthority(Authority.TeamScorer);

        objectiveAcceptanceService.setAcceptance(request, authenticationPrincipal.getUserAccEntity());
    }
}
