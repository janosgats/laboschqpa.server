package com.laboschqpa.server.api.service.internal;

import com.laboschqpa.server.api.dto.IndexedFileServingRequestDto;
import com.laboschqpa.server.api.dto.IsUserAuthorizedToResourceResponseDto;
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

    public IsUserAuthorizedToResourceResponseDto getIsAuthorizedToResource(IndexedFileServingRequestDto indexedFileServingRequestDto,
                                                                           CustomOauth2User authenticationPrincipal,
                                                                           HttpServletRequest request) {
        indexedFileServingRequestDto.validateSelf();
        logger.debug("Authorizing IndexedFileServingRequestDto: {}", indexedFileServingRequestDto);
        if (!isCsrfTokenValid(indexedFileServingRequestDto, request)) {
            return IsUserAuthorizedToResourceResponseDto.builder()
                    .authenticated(true)
                    .csrfValid(false)
                    .authorized(false)
                    .build();
        }

        if (true) {//TODO: Do proper triage of authorization here
            return IsUserAuthorizedToResourceResponseDto.builder()
                    .authenticated(true)
                    .csrfValid(true)
                    .authorized(true)
                    .build();
        } else {
            return IsUserAuthorizedToResourceResponseDto.builder()
                    .authenticated(true)
                    .csrfValid(true)
                    .authorized(false)
                    .build();
        }

    }

    private boolean isCsrfTokenValid(IndexedFileServingRequestDto indexedFileServingRequestDto, HttpServletRequest request) {
        HttpMethod method = indexedFileServingRequestDto.getHttpMethod();
        if (method == HttpMethod.POST
                || method == HttpMethod.DELETE
                || method == HttpMethod.PUT
                || method == HttpMethod.PATCH) {
            String csrfTokenToValidate = indexedFileServingRequestDto.getCsrfToken();
            String realCsrfToken = csrfTokenRepository.loadToken(request).getToken();

            return csrfTokenToValidate != null && csrfTokenToValidate.equals(realCsrfToken);
        } else
            return true;
    }
}
