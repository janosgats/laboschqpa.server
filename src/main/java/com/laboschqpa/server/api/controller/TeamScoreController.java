package com.laboschqpa.server.api.controller;

import com.laboschqpa.server.api.dto.CreatedEntityResponse;
import com.laboschqpa.server.api.dto.teamscore.CreateNewTeamScoreDto;
import com.laboschqpa.server.api.dto.teamscore.EditTeamScoreDto;
import com.laboschqpa.server.api.dto.teamscore.GetTeamScoreDto;
import com.laboschqpa.server.api.service.TeamScoreService;
import com.laboschqpa.server.config.userservice.CustomOauth2User;
import com.laboschqpa.server.enums.auth.Authority;
import com.laboschqpa.server.util.PrincipalAuthorizationHelper;
import io.swagger.annotations.ApiOperation;
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
    public GetTeamScoreDto getTeamScore(@RequestParam(name = "id") Long teamScoreId,
                                        @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        new PrincipalAuthorizationHelper(authenticationPrincipal).assertHasAuthority(Authority.TeamScorer);

        return new GetTeamScoreDto(teamScoreService.getTeamScore(teamScoreId));
    }

    @ApiOperation("Returns a 1 element long array with the found dto, or an empty array if not found.")
    @GetMapping("/find")
    public List<GetTeamScoreDto> getTeamScore(@RequestParam(name = "objectiveId") Long objectiveId, @RequestParam(name = "teamId") Long teamId,
                                              @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        new PrincipalAuthorizationHelper(authenticationPrincipal).assertHasAuthority(Authority.TeamScorer);

        return teamScoreService.find(objectiveId, teamId).stream()
                .map(GetTeamScoreDto::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/listAll")
    public List<GetTeamScoreDto> getListAllTeamScores(@AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        new PrincipalAuthorizationHelper(authenticationPrincipal).assertHasAuthority(Authority.TeamScorer);

        return teamScoreService.listAllTeamScores().stream()
                .map(GetTeamScoreDto::new)
                .collect(Collectors.toList());
    }

    @PostMapping("/createNew")
    public CreatedEntityResponse postCreateNewTeamScore(@RequestBody CreateNewTeamScoreDto createNewTeamScoreDto,
                                                        @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        createNewTeamScoreDto.validateSelf();
        new PrincipalAuthorizationHelper(authenticationPrincipal).assertHasAuthority(Authority.TeamScorer);

        long newId = teamScoreService.createNewTeamScore(createNewTeamScoreDto, authenticationPrincipal.getUserAccEntity()).getId();
        return new CreatedEntityResponse(newId);
    }

    @PostMapping("/edit")
    public void postEditTeamScore(@RequestBody EditTeamScoreDto editTeamScoreDto,
                                  @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        editTeamScoreDto.validateSelf();
        new PrincipalAuthorizationHelper(authenticationPrincipal).assertHasAuthority(Authority.TeamScorer);

        teamScoreService.editTeamScore(editTeamScoreDto, authenticationPrincipal.getUserAccEntity());
    }

    @DeleteMapping("/delete")
    public void deleteTeamScore(@RequestParam(name = "id") Long teamScoreId,
                                @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        new PrincipalAuthorizationHelper(authenticationPrincipal).assertHasAuthority(Authority.TeamScorer);

        teamScoreService.deleteTeamScore(teamScoreId, authenticationPrincipal.getUserAccEntity());
    }
}
