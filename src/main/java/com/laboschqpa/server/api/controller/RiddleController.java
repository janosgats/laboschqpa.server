package com.laboschqpa.server.api.controller;

import com.laboschqpa.server.api.dto.ugc.riddle.GetAccessibleRiddleResponse;
import com.laboschqpa.server.api.dto.ugc.riddle.RiddleSubmitSolutionResponse;
import com.laboschqpa.server.api.service.riddle.RiddleService;
import com.laboschqpa.server.config.userservice.CustomOauth2User;
import com.laboschqpa.server.entity.account.UserAcc;
import com.laboschqpa.server.enums.TeamRateControlTopic;
import com.laboschqpa.server.enums.apierrordescriptor.RiddleApiError;
import com.laboschqpa.server.exceptions.apierrordescriptor.RiddleException;
import com.laboschqpa.server.service.TeamRateControlService;
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
    private final TeamRateControlService teamRateControlService;

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

        manageSubmissionRateLimiting(teamId, authenticationPrincipal.getUserAccEntity());
        return riddleService.submitSolution(teamId, riddleIdToSubmitSolutionTo, givenSolution);
    }


    private void manageSubmissionRateLimiting(long teamId, UserAcc userAcc) {
        if (!teamRateControlService.isRateLimitAlright(TeamRateControlTopic.RIDDLE_SUBMISSION_TRIAL, teamId)) {
            throw new RiddleException(RiddleApiError.TEAM_RATE_LIMIT_HIT_FOR_RIDDLE_SUBMISSIONS);
        }
        teamRateControlService.log(TeamRateControlTopic.RIDDLE_SUBMISSION_TRIAL, teamId, userAcc.getId());
    }
}
