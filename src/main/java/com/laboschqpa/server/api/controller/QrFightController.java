package com.laboschqpa.server.api.controller;

import com.laboschqpa.server.api.dto.qrfight.QrFightAreaResponse;
import com.laboschqpa.server.api.dto.qrfight.QrFightAreaWithTagCountResponse;
import com.laboschqpa.server.api.dto.qrfight.QrFightStatResponse;
import com.laboschqpa.server.api.dto.qrfight.QrTagForUserResponse;
import com.laboschqpa.server.api.service.qrfight.QrFightService;
import com.laboschqpa.server.config.userservice.CustomOauth2User;
import com.laboschqpa.server.entity.account.UserAcc;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/qrFight")
public class QrFightController {
    private final QrFightService qrFightService;

    @GetMapping("/listAllTagsWithSubmissionCount")
    public List<QrTagForUserResponse> getListAllWithSubmissionCount() {
        return qrFightService.listAllTagsWithSubmissionCount().stream()
                .map(QrTagForUserResponse::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/fightStats")
    public List<QrFightStatResponse> getListAreasWithTeamSubmissionCount() {
        return qrFightService.listEnabledAreasWithTeamSubmissionCount().stream()
                .map(QrFightStatResponse::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/listEnabledAreas")
    public List<QrFightAreaResponse> getListEnabledAreas() {
        return qrFightService.listEnabledAreas().stream()
                .map(QrFightAreaResponse::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/listEnabledAreasWithTagCount")
    public List<QrFightAreaResponse> getListEnabledAreasWithTagCount() {
        return qrFightService.listEnabledAreasWithTagCount().stream()
                .map(QrFightAreaWithTagCountResponse::new)
                .collect(Collectors.toList());
    }

    @PostMapping("/submit")
    public void postSubmit(@RequestParam("tagId") Long tagId,
                           @RequestParam("secret") String secret,
                           @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        final UserAcc userAcc = ControllerHelpers.getUserAcc(authenticationPrincipal);
        qrFightService.submitQrTag(userAcc, tagId, secret);
    }
}
