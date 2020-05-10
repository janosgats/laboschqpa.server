package com.laboschqpa.server.api.controller;

import com.laboschqpa.server.api.dto.team.TeamDto;
import com.laboschqpa.server.api.service.TeamService;
import com.laboschqpa.server.config.userservice.CustomOauth2User;
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

    @PostMapping("/leaveTeam")
    public void postLeaveTeam(@AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        teamService.leaveTeam(authenticationPrincipal.getUserId());
    }

    @PostMapping("/kickFromTeam")
    public void postKickFromTeam(@RequestParam("userAccIdToKick") Long userAccIdToKick,
                                 @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        teamService.kickFromTeam(userAccIdToKick, authenticationPrincipal.getUserId());
    }

    @PostMapping("/archiveAndLeaveTeam")
    public void postArchiveAndLeaveTeam(@AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        teamService.archiveAndLeaveTeam(authenticationPrincipal.getUserId());
    }

    @PostMapping("/giveLeaderRights")
    public void postGiveLeaderRights(@RequestParam("userAccIdToGiveLeaderRights") Long userAccIdToGiveLeaderRights,
                                     @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        teamService.giveLeaderRights(userAccIdToGiveLeaderRights, authenticationPrincipal.getUserId());
    }

    @PostMapping("/takeAwayLeaderRights")
    public void postTakeAwayLeaderRights(@RequestParam("userAccIdToTakeAwayLeaderRights") Long userAccIdToTakeAwayLeaderRights,
                                         @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        teamService.takeAwayLeaderRights(userAccIdToTakeAwayLeaderRights, authenticationPrincipal.getUserId());
    }

    @PostMapping("/resignFromLeadership")
    public void postResignFromLeadership(@AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        teamService.resignFromLeadership(authenticationPrincipal.getUserId());
    }
}
