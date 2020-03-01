package com.laboschcst.server.api.controller.admin;

import com.laboschcst.server.api.service.admin.AuthorityAdminService;
import com.laboschcst.server.config.auth.user.CustomOauth2User;
import com.laboschcst.server.enums.Authority;
import com.laboschcst.server.exceptions.UnAuthorizedException;
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

    @GetMapping("userAuthority")
    public Set<String> getUserAuthorities(@RequestParam("userAccId") Long userAccId,
                                          @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        assertHasAdminAuthority(authenticationPrincipal);
        return authorityAdminService.getUserAuthorities(userAccId).stream().map(Authority::getStringValue).collect(Collectors.toSet());
    }

    @PostMapping("userAuthority")
    public void postAddUserAuthority(@RequestParam("userAccId") Long userAccId,
                                     @RequestParam("authority") String authorityStringValue,
                                     @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        assertHasAdminAuthority(authenticationPrincipal);
        authorityAdminService.addUserAuthority(userAccId, Authority.fromStringValue(authorityStringValue));
    }

    @DeleteMapping("userAuthority")
    public void deleteUserAuthority(@RequestParam("userAccId") Long userAccId,
                                    @RequestParam("authority") String authorityStringValue,
                                    @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        assertHasAdminAuthority(authenticationPrincipal);
        authorityAdminService.deleteUserAuthority(userAccId, Authority.fromStringValue(authorityStringValue));
    }

    private void assertHasAdminAuthority(CustomOauth2User authenticationPrincipal) {
        if (authenticationPrincipal.getUserAccEntity().getAuthorities().stream().noneMatch(authority -> authority.equals(Authority.Admin)))
            throw new UnAuthorizedException();
    }
}
