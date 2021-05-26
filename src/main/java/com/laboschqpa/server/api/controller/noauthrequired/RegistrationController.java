package com.laboschqpa.server.api.controller.noauthrequired;

import com.laboschqpa.server.api.service.noauthrequired.RegistrationService;
import com.laboschqpa.server.config.helper.AppConstants;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping(AppConstants.apiNoAuthRequiredUrl + "/register")
@Validated
public class RegistrationController {
    private final RegistrationService registrationService;

    @ApiOperation("Call this when the user explicitly wants to create a new account after trying to log in with OAuth")
    @PostMapping("/createNewAccountFromSessionOAuthInfo")
    public long postCreateNewAccountFromSessionOAuthInfo() {
        return registrationService.createNewAccountFromSessionOAuthInfo().getId();
    }
}
