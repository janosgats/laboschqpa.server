package com.laboschqpa.server.api.controller.admin;

import com.laboschqpa.server.api.service.admin.UsersAdminService;
import com.laboschqpa.server.config.auth.user.CustomOauth2User;
import com.laboschqpa.server.service.AuthorizationHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/users")
public class UsersAdminController {
    private final UsersAdminService usersAdminService;

    @PostMapping("loginasuser")
    public Long loginAsUser(@RequestParam("userAccId") Long userAccId,
                               @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        new AuthorizationHelper(authenticationPrincipal).assertHasAdminAuthority();
        return usersAdminService.loginAsUser(userAccId);
    }
}
