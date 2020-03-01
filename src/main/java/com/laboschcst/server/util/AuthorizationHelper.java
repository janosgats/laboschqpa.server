package com.laboschcst.server.util;

import com.laboschcst.server.config.auth.user.CustomOauth2User;
import com.laboschcst.server.enums.Authority;
import com.laboschcst.server.exceptions.UnAuthorizedException;

import java.util.Set;
import java.util.stream.Collectors;

public class AuthorizationHelper {

    public static void assertHasAdminAuthority(CustomOauth2User authenticationPrincipal) {
        assertHasAnySufficientAuthority(authenticationPrincipal, Set.of(Authority.Admin));
    }

    public static void hasAdminAuthority(CustomOauth2User authenticationPrincipal) {
        hasAnySufficientAuthority(authenticationPrincipal, Set.of(Authority.Admin));
    }

    public static void assertHasEditorOrAdminAuthority(CustomOauth2User authenticationPrincipal) {
        assertHasAnySufficientAuthority(authenticationPrincipal, Set.of(Authority.Editor, Authority.Admin));
    }

    public static void assertHasAnySufficientAuthority(CustomOauth2User authenticationPrincipal, Set<Authority> setOfSufficientAuthorities) {
        if (!hasAnySufficientAuthority(authenticationPrincipal, setOfSufficientAuthorities))
            throw new UnAuthorizedException("You have none of the following authorities: " +
                    setOfSufficientAuthorities.stream().map(Authority::getStringValue)
                            .collect(Collectors.joining(","))
            );
    }

    public static boolean hasAnySufficientAuthority(CustomOauth2User authenticationPrincipal, Set<Authority> setOfSufficientAuthorities) {
        return authenticationPrincipal.getUserAccEntity()
                .getAuthorities().stream()
                .anyMatch(ownedAuthority -> setOfSufficientAuthorities.stream().anyMatch(ownedAuthority::equals));
    }
}