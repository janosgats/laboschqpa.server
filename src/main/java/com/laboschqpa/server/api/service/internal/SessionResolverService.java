package com.laboschqpa.server.api.service.internal;

import com.laboschqpa.server.api.dto.internal.IsUserAuthorizedToResourceRequestDto;
import com.laboschqpa.server.api.dto.internal.IsUserAuthorizedToResourceResponseDto;
import com.laboschqpa.server.config.userservice.CustomOauth2User;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@Service
public class SessionResolverService {
    private static final Logger logger = LoggerFactory.getLogger(SessionResolverService.class);
    private final CsrfTokenRepository csrfTokenRepository;

    public IsUserAuthorizedToResourceResponseDto getIsAuthorizedToResource(IsUserAuthorizedToResourceRequestDto isUserAuthorizedToResourceRequestDto,
                                                                           CustomOauth2User authenticationPrincipal,
                                                                           HttpServletRequest request) {
        isUserAuthorizedToResourceRequestDto.validateSelf();
        logger.trace("Authorizing with IsUserAuthorizedToResourceRequestDto.");
        if (!isCsrfTokenValid(isUserAuthorizedToResourceRequestDto, request)) {
            return IsUserAuthorizedToResourceResponseDto.builder()
                    .authenticated(true)
                    .csrfValid(false)
                    .authorized(false)
                    .loggedInUserId(authenticationPrincipal.getUserAccEntity().getId())
                    .build();
        }

        if (true) {//TODO: Do proper triage of authorization here
            return IsUserAuthorizedToResourceResponseDto.builder()
                    .authenticated(true)
                    .csrfValid(true)
                    .authorized(true)
                    .loggedInUserId(authenticationPrincipal.getUserAccEntity().getId())
                    .loggedInUserTeamId(authenticationPrincipal.getUserAccEntity().getTeam().getId())//TODO: Only users in teams will be able to upload files
                    .build();
        } else {
            return IsUserAuthorizedToResourceResponseDto.builder()
                    .authenticated(true)
                    .csrfValid(true)
                    .authorized(false)
                    .loggedInUserId(authenticationPrincipal.getUserAccEntity().getId())
                    .build();
        }

    }

    private boolean isCsrfTokenValid(IsUserAuthorizedToResourceRequestDto isUserAuthorizedToResourceRequestDto, HttpServletRequest request) {
        HttpMethod method = isUserAuthorizedToResourceRequestDto.getHttpMethod();
        if (method == HttpMethod.POST
                || method == HttpMethod.DELETE
                || method == HttpMethod.PUT
                || method == HttpMethod.PATCH) {
            String csrfTokenToValidate = isUserAuthorizedToResourceRequestDto.getCsrfToken();
            String realCsrfToken = csrfTokenRepository.loadToken(request).getToken();

            return csrfTokenToValidate != null && csrfTokenToValidate.equals(realCsrfToken);
        } else
            return true;
    }
}
