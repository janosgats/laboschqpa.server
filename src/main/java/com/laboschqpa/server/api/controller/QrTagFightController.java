package com.laboschqpa.server.api.controller;

import com.laboschqpa.server.api.dto.qrtagfight.QrTagForUserResponse;
import com.laboschqpa.server.api.service.qrtagfight.QrTagFightService;
import com.laboschqpa.server.config.userservice.CustomOauth2User;
import com.laboschqpa.server.entity.account.UserAcc;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/qrTagFight")
public class QrTagFightController {
    private final QrTagFightService qrTagFightService;

    @GetMapping("/listAllTagsWithSubmissionCount")
    public List<QrTagForUserResponse> getListAllWithSubmissionCount() {
        return qrTagFightService.listAllTagsWithSubmissionCount().stream()
                .map(QrTagForUserResponse::new)
                .collect(Collectors.toList());
    }

    @PostMapping("/submit")
    public void postSubmit(@RequestParam("tagId") Long tagId,
                           @RequestParam("secret") String secret,
                           @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        final UserAcc userAcc = Helpers.getUserAcc(authenticationPrincipal);
        qrTagFightService.submitQrTag(userAcc, tagId, secret);
    }
}
