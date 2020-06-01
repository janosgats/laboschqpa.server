package com.laboschqpa.server.api.controller.noauthrequired;

import com.laboschqpa.server.api.service.noauthrequired.RegisterByEmailService;
import com.laboschqpa.server.config.helper.AppConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Email;

@RequiredArgsConstructor
@RestController
@RequestMapping(AppConstants.apiNoAuthRequiredUrl + "registerByEmail")
@Validated
public class RegisterByEmailController {
    private final RegisterByEmailService registerByEmailService;

    @PostMapping("/submitEmail")
    public void postSubmitEmail(@Email @RequestParam("email") String email) {
        registerByEmailService.onSubmitEmailToRegister(email);
    }

    @PostMapping("/verifyEmail")
    public void postVerifyEmail(@RequestParam("id") Long registrationRequestId,
                                @RequestParam("key") String registrationRequestKey) {
        registerByEmailService.onVisitingPageFromEmailLink(registrationRequestId, registrationRequestKey);
    }
}
