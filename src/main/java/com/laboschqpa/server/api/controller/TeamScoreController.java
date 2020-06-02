package com.laboschqpa.server.api.controller;

import com.laboschqpa.server.api.dto.teamscore.CreateNewTeamScoreDto;
import com.laboschqpa.server.api.dto.teamscore.EditTeamScoreDto;
import com.laboschqpa.server.api.dto.teamscore.GetTeamScoreDto;
import com.laboschqpa.server.api.service.TeamScoreService;
import com.laboschqpa.server.config.userservice.CustomOauth2User;
import com.laboschqpa.server.enums.auth.Authority;
import com.laboschqpa.server.service.PrincipalAuthorizationHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/teamScore")
public class TeamScoreController {
    private final TeamScoreService teamScoreService;

    @GetMapping("/teamScore")
    public GetTeamScoreDto getObjective(@RequestParam(name = "id") Long teamScoreId) {
        return new GetTeamScoreDto(teamScoreService.getTeamScore(teamScoreId));
    }

    @GetMapping("/listAll")
    public List<GetTeamScoreDto> getListAllObjectives() {
        return teamScoreService.listAllTeamScores().stream()
                .map(GetTeamScoreDto::new)
                .collect(Collectors.toList());
    }

    @PostMapping("/createNew")
    public Long postCreateNewObjective(@RequestBody CreateNewTeamScoreDto createNewTeamScoreDto,
                                       @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        createNewTeamScoreDto.validateSelf();
        new PrincipalAuthorizationHelper(authenticationPrincipal).assertHasAnySufficientAuthority(Authority.TeamScoreEditor, Authority.Admin);
        return teamScoreService.createNewTeamScore(createNewTeamScoreDto, authenticationPrincipal.getUserAccEntity()).getId();
    }

    @PostMapping("/edit")
    public void postEditObjective(@RequestBody EditTeamScoreDto editTeamScoreDto,
                                  @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        editTeamScoreDto.validateSelf();
        new PrincipalAuthorizationHelper(authenticationPrincipal).assertHasAnySufficientAuthority(Authority.TeamScoreEditor, Authority.Admin);
        teamScoreService.editTeamScore(editTeamScoreDto, authenticationPrincipal.getUserAccEntity());
    }

    @DeleteMapping("/delete")
    public void deleteObjective(@RequestParam(name = "id") Long teamScoreId,
                                @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        new PrincipalAuthorizationHelper(authenticationPrincipal).assertHasAnySufficientAuthority(Authority.TeamScoreEditor, Authority.Admin);
        teamScoreService.deleteTeamScore(teamScoreId, authenticationPrincipal.getUserAccEntity());
    }
}
