package com.laboschqpa.server.api.controller;

import com.laboschqpa.server.api.dto.currentuser.GetCsrfTokenResponse;
import com.laboschqpa.server.api.dto.user.UserInfoResponse;
import com.laboschqpa.server.api.service.UserService;
import com.laboschqpa.server.config.userservice.CustomOauth2User;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.Optional;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/currentUser")
public class CurrentUserController {
    private final CsrfTokenRepository csrfTokenRepository;
    private final UserService userService;

    @GetMapping("/userInfoWithAuthoritiesAndTeam")
    public UserInfoResponse getUserInfoWithAuthoritiesAndTeam(@AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        return new UserInfoResponse(userService.getByIdWithAuthoritiesAndTeam(authenticationPrincipal.getUserId()), true, true);
    }

    /**
     * We don't need to be in sync with the token generation by all means.
     * This {@code check->waitRandomIfNotPresent->recheck->generateIfNotPresent} method is good enough.
     */
    @GetMapping("/csrfToken")
    public Mono<GetCsrfTokenResponse> getCsrfToken(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        final long maxDelayBeforeRecheckingIfTokenExistsMillis = 1000;

        return deferredReadCsrf(httpServletRequest)
                .switchIfEmpty(Mono.empty()
                        .delaySubscription(
                                Duration.ofMillis(randomMillisBetweenZeroAnd(maxDelayBeforeRecheckingIfTokenExistsMillis))
                        ).then(deferredReadCsrf(httpServletRequest))
                ).switchIfEmpty(
                        deferredGenerateCsrf(httpServletRequest, httpServletResponse)
                ).map(GetCsrfTokenResponse::new);
    }

    private Mono<CsrfToken> deferredReadCsrf(HttpServletRequest httpServletRequest) {
        return Mono.defer(() ->
                Optional.ofNullable(csrfTokenRepository.loadToken(httpServletRequest))
                        .map(Mono::just)
                        .orElseGet(Mono::empty)
        );
    }

    private Mono<CsrfToken> deferredGenerateCsrf(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        return Mono.defer(() -> {
            final CsrfToken generatedToken = csrfTokenRepository.generateToken(httpServletRequest);
            csrfTokenRepository.saveToken(generatedToken, httpServletRequest, httpServletResponse);
            return Mono.just(generatedToken);
        });
    }

    private long randomMillisBetweenZeroAnd(long maxMillis) {
        return (long) (Math.random() * maxMillis);
    }
}
