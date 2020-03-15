package com.laboschcst.server.api.controller;

import com.laboschcst.server.api.dto.TeamDto;
import com.laboschcst.server.api.service.TeamService;
import com.laboschcst.server.config.auth.user.CustomOauth2User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/team")
public class TeamController {
    private final TeamService teamService;

    @PostMapping("/createNewTeam")
    public void postCreateNewTeam(@RequestBody TeamDto teamDto,
                                  @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        teamService.createNewTeam(teamDto, authenticationPrincipal.getUserId());
    }

    @PostMapping("/applyToTeam")
    public void postApplyToTeam(@RequestParam("teamId") Long teamId,
                                @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        teamService.applyToTeam(teamId, authenticationPrincipal.getUserId());
    }

    @PostMapping("/cancelApplicationToTeam")
    public void postCancelApplicationToTeam(@AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        teamService.cancelApplicationToTeam(authenticationPrincipal.getUserId());
    }

    @PostMapping("/declineApplicationToTeam")
    public void postDeclineApplicationToTeam(@RequestParam("userAccIdToDecline") Long userAccIdToDecline,
                                             @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        teamService.declineApplicationToTeam(userAccIdToDecline, authenticationPrincipal.getUserId());
    }

    @PostMapping("/approveApplicationToTeam")
    public void postApproveApplicationToTeam(@RequestParam("userAccIdToApprove") Long userAccIdToApprove,
                                             @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        teamService.approveApplicationToTeam(userAccIdToApprove, authenticationPrincipal.getUserId());
    }
}
