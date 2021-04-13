package com.laboschqpa.server.api.controller.admin;

import com.laboschqpa.server.api.service.admin.UsersAdminService;
import com.laboschqpa.server.config.userservice.CustomOauth2User;
import com.laboschqpa.server.util.PrincipalAuthorizationHelper;
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

    @PostMapping("logInAsUser")
    public Long loginAsUser(@RequestParam("userAccId") Long userAccId,
                               @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        new PrincipalAuthorizationHelper(authenticationPrincipal).assertHasAdminAuthority();
        return usersAdminService.loginAsUser(userAccId);
    }
}
