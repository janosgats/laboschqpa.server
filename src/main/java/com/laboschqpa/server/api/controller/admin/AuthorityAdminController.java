package com.laboschqpa.server.api.controller.admin;

import com.laboschqpa.server.api.controller.ControllerHelpers;
import com.laboschqpa.server.api.service.admin.AuthorityAdminService;
import com.laboschqpa.server.config.userservice.CustomOauth2User;
import com.laboschqpa.server.enums.auth.Authority;
import com.laboschqpa.server.util.PrincipalAuthorizationHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/authority")
public class AuthorityAdminController {
    private final AuthorityAdminService authorityAdminService;

    @GetMapping("user/get")
    public Set<String> getUserAuthorities(@RequestParam("userId") Long userAccId,
                                          @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        new PrincipalAuthorizationHelper(authenticationPrincipal).assertHasAdminAuthority();
        return authorityAdminService.getUserAuthorities(userAccId).stream().map(Authority::getStringValue).collect(Collectors.toSet());
    }

    @PostMapping("user/add")
    public void postAddUserAuthority(@RequestParam("userId") Long userAccId,
                                     @RequestParam("authority") String authorityStringValue,
                                     @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        new PrincipalAuthorizationHelper(authenticationPrincipal).assertHasAdminAuthority();
        long loggedInUserId = ControllerHelpers.getUserId(authenticationPrincipal);

        authorityAdminService.addUserAuthority(userAccId, Authority.fromStringValue(authorityStringValue), loggedInUserId);
    }

    @DeleteMapping("user/delete")
    public void deleteUserAuthority(@RequestParam("userId") Long userAccId,
                                    @RequestParam("authority") String authorityStringValue,
                                    @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        new PrincipalAuthorizationHelper(authenticationPrincipal).assertHasAdminAuthority();
        long loggedInUserId = ControllerHelpers.getUserId(authenticationPrincipal);

        authorityAdminService.deleteUserAuthority(userAccId, Authority.fromStringValue(authorityStringValue), loggedInUserId);
    }
}
