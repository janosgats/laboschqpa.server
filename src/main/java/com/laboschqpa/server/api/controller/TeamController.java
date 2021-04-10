package com.laboschqpa.server.api.controller;

import com.laboschqpa.server.api.dto.team.CreateNewTeamRequest;
import com.laboschqpa.server.api.dto.team.GetTeamMemberResponse;
import com.laboschqpa.server.api.dto.team.GetTeamResponse;
import com.laboschqpa.server.api.dto.team.GetTeamWithScoreResponse;
import com.laboschqpa.server.api.service.TeamService;
import com.laboschqpa.server.config.userservice.CustomOauth2User;
import com.laboschqpa.server.service.TeamLifecycleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/team")
public class TeamController {
    private final TeamLifecycleService teamLifecycleService;
    private final TeamService teamService;

    @PostMapping("/createNewTeam")
    public GetTeamResponse postCreateNewTeam(@RequestBody CreateNewTeamRequest createNewTeamRequest,
                                             @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        return new GetTeamResponse(
                teamLifecycleService.createNewTeam(createNewTeamRequest, authenticationPrincipal.getUserId())
        );
    }

    @PostMapping("/applyToTeam")
    public void postApplyToTeam(@RequestParam("teamId") Long teamId,
                                @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        teamLifecycleService.applyToTeam(teamId, authenticationPrincipal.getUserId());
    }

    @PostMapping("/cancelApplicationToTeam")
    public void postCancelApplicationToTeam(@AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        teamLifecycleService.cancelApplicationToTeam(authenticationPrincipal.getUserId());
    }

    @PostMapping("/declineApplicationToTeam")
    public void postDeclineApplicationToTeam(@RequestParam("userAccIdToDecline") Long userAccIdToDecline,
                                             @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        teamLifecycleService.declineApplicationToTeam(userAccIdToDecline, authenticationPrincipal.getUserId());
    }

    @PostMapping("/approveApplicationToTeam")
    public void postApproveApplicationToTeam(@RequestParam("userAccIdToApprove") Long userAccIdToApprove,
                                             @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        teamLifecycleService.approveApplicationToTeam(userAccIdToApprove, authenticationPrincipal.getUserId());
    }

    @PostMapping("/leaveTeam")
    public void postLeaveTeam(@AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        teamLifecycleService.leaveTeam(authenticationPrincipal.getUserId());
    }

    @PostMapping("/kickFromTeam")
    public void postKickFromTeam(@RequestParam("userAccIdToKick") Long userAccIdToKick,
                                 @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        teamLifecycleService.kickFromTeam(userAccIdToKick, authenticationPrincipal.getUserId());
    }

    @PostMapping("/archiveAndLeaveTeam")
    public void postArchiveAndLeaveTeam(@AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        teamLifecycleService.archiveAndLeaveTeam(authenticationPrincipal.getUserId());
    }

    @PostMapping("/giveLeaderRights")
    public void postGiveLeaderRights(@RequestParam("userAccIdToGiveLeaderRights") Long userAccIdToGiveLeaderRights,
                                     @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        teamLifecycleService.giveLeaderRights(userAccIdToGiveLeaderRights, authenticationPrincipal.getUserId());
    }

    @PostMapping("/takeAwayLeaderRights")
    public void postTakeAwayLeaderRights(@RequestParam("userAccIdToTakeAwayLeaderRights") Long userAccIdToTakeAwayLeaderRights,
                                         @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        teamLifecycleService.takeAwayLeaderRights(userAccIdToTakeAwayLeaderRights, authenticationPrincipal.getUserId());
    }

    @PostMapping("/resignFromLeadership")
    public void postResignFromLeadership(@AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        teamLifecycleService.resignFromLeadership(authenticationPrincipal.getUserId());
    }

    @GetMapping("/listActiveTeamsWithScores")
    public List<GetTeamWithScoreResponse> getListActiveTeamsWithScores() {
        return teamService.listActiveTeamsWithScores().stream()
                .map(GetTeamWithScoreResponse::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/info")
    public GetTeamResponse getInfo(@RequestParam("id") Long id) {
        return new GetTeamResponse(teamService.getById(id));
    }

    @GetMapping("/listMembers")
    public List<GetTeamMemberResponse> listMembers(@RequestParam("id") Long id) {
        return teamService.listMembers(id).stream()
                .map(GetTeamMemberResponse::new)
                .collect(Collectors.toList());
    }
}
