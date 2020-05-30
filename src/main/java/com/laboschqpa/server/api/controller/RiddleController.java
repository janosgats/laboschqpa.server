package com.laboschqpa.server.api.controller;

import com.laboschqpa.server.api.dto.ugc.riddle.GetAccessibleRiddleDto;
import com.laboschqpa.server.api.dto.ugc.riddle.RiddleSubmitSolutionResponseDto;
import com.laboschqpa.server.api.service.RiddleService;
import com.laboschqpa.server.config.userservice.CustomOauth2User;
import com.laboschqpa.server.entity.account.UserAcc;
import com.laboschqpa.server.enums.apierrordescriptor.RiddleApiError;
import com.laboschqpa.server.exceptions.apierrordescriptor.RiddleException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/riddle")
public class RiddleController {
    private final RiddleService riddleService;

    @GetMapping("/listAccessibleRiddles")
    public List<GetAccessibleRiddleDto> listAccessibleRiddles(@AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        Long teamId = assertAndGetTeamId(authenticationPrincipal);
        return riddleService.listAccessibleRiddleJpaDtos(teamId).stream()
                .map((visibleRiddleJpaDto
                        -> new GetAccessibleRiddleDto(visibleRiddleJpaDto, visibleRiddleJpaDto.getWasHintUsed(), visibleRiddleJpaDto.getAlreadySolved())
                )).collect(Collectors.toList());
    }

    @GetMapping("/riddle")
    public GetAccessibleRiddleDto getOneRiddleToShow(@RequestParam("id") Long riddleIdToShow,
                                                     @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        Long teamId = assertAndGetTeamId(authenticationPrincipal);
        return riddleService.getOneRiddleToShow(teamId, riddleIdToShow);
    }

    @PostMapping("/useHint")
    public String postUseHint(@RequestParam("id") Long riddleIdToUseHintOf,
                              @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        Long teamId = assertAndGetTeamId(authenticationPrincipal);
        return riddleService.useHint(teamId, riddleIdToUseHintOf);
    }

    @PostMapping("/submitSolution")
    public RiddleSubmitSolutionResponseDto postSubmitSolution(@RequestParam("id") Long riddleIdToSubmitSolutionTo,
                                                              @RequestParam("solution") String givenSolution,
                                                              @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        Long teamId = assertAndGetTeamId(authenticationPrincipal);
        return riddleService.submitSolution(teamId, riddleIdToSubmitSolutionTo, givenSolution);
    }

    private Long assertAndGetTeamId(CustomOauth2User authenticationPrincipal) {
        final UserAcc userAcc = authenticationPrincipal.getUserAccEntity();
        final Long teamId = userAcc.getTeam().getId();

        if (teamId != null && userAcc.getTeamRole().isMemberOrLeader()) {
            return teamId;
        } else {
            throw new RiddleException(RiddleApiError.YOU_ARE_NOT_IN_A_TEAM);
        }
    }
}
