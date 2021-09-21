package com.laboschqpa.server.util;

import com.laboschqpa.server.config.userservice.CustomOauth2User;
import com.laboschqpa.server.entity.account.UserAcc;
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
    private final UserAcc userAccEntity;

    public PrincipalAuthorizationHelper(CustomOauth2User authenticationPrincipal) {
        userAccEntity = authenticationPrincipal.getUserAccEntity();
    }

    public boolean isTrueOrIsAdmin(BooleanSupplier condition) {
        return hasAdminAuthority() || condition.getAsBoolean();
    }

    public void assertHasAdminAuthority() {
        assertHasAnySufficientAuthority(Authority.Admin);
    }

    public boolean hasAdminAuthority() {
        return hasAnySufficientAuthority(Authority.Admin);
    }

    public boolean hasAuthority(Authority authority) {
        return hasAnySufficientAuthority(authority);
    }


    public void assertHasAuthority(Authority authority) {
        assertHasAnySufficientAuthority(authority);
    }

    public void assertHasAnySufficientAuthority(Authority... sufficientAuthorities) {
        if (!hasAnySufficientAuthority(sufficientAuthorities))
            throw new UnAuthorizedException("You have none of the following authorities: " +
                    Arrays.stream(sufficientAuthorities).map(Authority::getStringValue)
                            .collect(Collectors.joining(","))
            );
    }

    public boolean hasAnySufficientAuthority(Authority... sufficientAuthorities) {
        return userAccEntity
                .getAuthorities().stream()
                .anyMatch(
                        ownedAuthority -> Arrays.asList(sufficientAuthorities).contains(ownedAuthority)
                );
    }
}