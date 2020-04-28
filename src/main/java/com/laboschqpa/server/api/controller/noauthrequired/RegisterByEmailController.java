package com.laboschqpa.server.api.controller.noauthrequired;

import com.laboschqpa.server.api.service.RegisterByEmailService;
import com.laboschqpa.server.config.AppConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Email;

@RequiredArgsConstructor
@RestController
@RequestMapping(AppConstants.apiNoAuthRequiredUrl + "registerbyemail")
@Validated
public class RegisterByEmailController {
    private final RegisterByEmailService registerByEmailService;

    @GetMapping("/submitemail")
    public void getSubmitEmail(@Email @RequestParam("email") String email) {
        registerByEmailService.onSubmitEmailToRegister(email);
    }

    @GetMapping("/verifyemail")
    public void getVerifyEmail(@RequestParam("registrationRequestId") Long registrationRequestId,
                                @RequestParam("registrationRequestKey") String registrationRequestKey) {
        registerByEmailService.onVisitingPageFromEmailLink(registrationRequestId, registrationRequestKey);
    }
}
