package com.laboschqpa.server.service;

import com.laboschqpa.server.config.auth.user.CustomOauth2User;
import com.laboschqpa.server.enums.Authority;
import com.laboschqpa.server.exceptions.UnAuthorizedException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
public class PrincipalAuthorizationHelper {

    private final CustomOauth2User authenticationPrincipal;

    public boolean isTrueOrIsAdmin(BooleanSupplier condition) {
        return hasAdminAuthority() || condition.getAsBoolean();
    }

    public void assertHasAdminAuthority() {
        assertHasAnySufficientAuthority(Set.of(Authority.Admin));
    }

    public boolean hasAdminAuthority() {
        return hasAnySufficientAuthority(Set.of(Authority.Admin));
    }

    public void assertHasEditorOrAdminAuthority() {
        assertHasAnySufficientAuthority(Set.of(Authority.Editor, Authority.Admin));
    }

    public void assertHasAnySufficientAuthority(Set<Authority> setOfSufficientAuthorities) {
        if (!hasAnySufficientAuthority(setOfSufficientAuthorities))
            throw new UnAuthorizedException("You have none of the following authorities: " +
                    setOfSufficientAuthorities.stream().map(Authority::getStringValue)
                            .collect(Collectors.joining(","))
            );
    }

    public boolean hasAnySufficientAuthority(Set<Authority> setOfSufficientAuthorities) {
        return authenticationPrincipal.getUserAccEntity()
                .getAuthorities().stream()
                .anyMatch(ownedAuthority -> setOfSufficientAuthorities.stream().anyMatch(ownedAuthority::equals));
    }
}