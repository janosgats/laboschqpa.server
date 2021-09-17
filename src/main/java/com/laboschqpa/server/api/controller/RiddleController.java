package com.laboschqpa.server.api.controller;

import com.laboschqpa.server.api.dto.ugc.riddle.GetAccessibleRiddleResponse;
import com.laboschqpa.server.api.dto.ugc.riddle.RiddleSubmitSolutionResponse;
import com.laboschqpa.server.api.service.RiddleService;
import com.laboschqpa.server.config.userservice.CustomOauth2User;
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
    public List<GetAccessibleRiddleResponse> listAccessibleRiddles(@AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        Long teamId = Helpers.getTeamId(authenticationPrincipal);
        return riddleService.listAccessibleRiddleJpaDtos(teamId).stream()
                .map((visibleRiddleJpaDto
                        -> new GetAccessibleRiddleResponse(visibleRiddleJpaDto, visibleRiddleJpaDto.getWasHintUsed(), visibleRiddleJpaDto.getIsAlreadySolved())
                )).collect(Collectors.toList());
    }

    @GetMapping("/riddle")
    public GetAccessibleRiddleResponse getOneRiddleToShow(@RequestParam("id") Long riddleIdToShow,
                                                          @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        Long teamId = Helpers.getTeamId(authenticationPrincipal);
        return riddleService.getOneRiddleToShow(teamId, riddleIdToShow);
    }

    @PostMapping("/useHint")
    public String postUseHint(@RequestParam("id") Long riddleIdToUseHintOf,
                              @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        Long teamId = Helpers.getTeamId(authenticationPrincipal);
        return riddleService.useHint(teamId, riddleIdToUseHintOf);
    }

    @PostMapping("/submitSolution")
    public RiddleSubmitSolutionResponse postSubmitSolution(@RequestParam("id") Long riddleIdToSubmitSolutionTo,
                                                           @RequestParam("solution") String givenSolution,
                                                           @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        Long teamId = Helpers.getTeamId(authenticationPrincipal);
        return riddleService.submitSolution(teamId, riddleIdToSubmitSolutionTo, givenSolution);
    }
}
