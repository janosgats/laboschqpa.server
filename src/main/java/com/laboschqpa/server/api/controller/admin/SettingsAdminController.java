package com.laboschqpa.server.api.controller.admin;

import com.laboschqpa.server.api.service.admin.SettingsAdminService;
import com.laboschqpa.server.config.userservice.CustomOauth2User;
import com.laboschqpa.server.service.PrincipalAuthorizationHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin/settings")
public class SettingsAdminController {
    private final SettingsAdminService settingsAdminService;

    @PostMapping("/sendtestemail")
    public void postSendTestEmail(@RequestParam("toEmail") String toEmail, @RequestParam("testMessage") String testMessage,
                                  @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        new PrincipalAuthorizationHelper(authenticationPrincipal).assertHasAdminAuthority();
        settingsAdminService.sendTestEmail(toEmail, testMessage);
    }
}
