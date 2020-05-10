package com.laboschqpa.server.service;

import com.laboschqpa.server.config.userservice.CustomOauth2User;
import com.laboschqpa.server.enums.auth.Authority;
import com.laboschqpa.server.exceptions.UnAuthorizedException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
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
        assertHasAnySufficientAuthority(Authority.Admin);
    }

    public boolean hasAdminAuthority() {
        return hasAnySufficientAuthority(Authority.Admin);
    }

    public void assertHasAnySufficientAuthority(Authority... sufficientAuthorities) {
        if (!hasAnySufficientAuthority(sufficientAuthorities))
            throw new UnAuthorizedException("You have none of the following authorities: " +
                    Arrays.stream(sufficientAuthorities).map(Authority::getStringValue)
                            .collect(Collectors.joining(","))
            );
    }

    private boolean hasAnySufficientAuthority(Authority... sufficientAuthorities) {
        return authenticationPrincipal.getUserAccEntity()
                .getAuthorities().stream()
                .anyMatch(
                        ownedAuthority -> Arrays.asList(sufficientAuthorities).contains(ownedAuthority)
                );
    }
}