package com.laboschqpa.server.api.controller.noauthrequired;

import com.laboschqpa.server.api.service.noauthrequired.EmailVerificationService;
import com.laboschqpa.server.config.helper.AppConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping(AppConstants.apiNoAuthRequiredUrl + "/emailVerification")
@Validated
public class EmailVerificationController {
    private final EmailVerificationService emailVerificationService;

    @PostMapping("/verify")
    public void postVerifyEmail(@RequestParam("id") Long verificationRequestId,
                                @RequestParam("key") String verificationKey) {
        emailVerificationService.onVisitingPageFromVerificationEmailLink(verificationRequestId, verificationKey);
    }
}
