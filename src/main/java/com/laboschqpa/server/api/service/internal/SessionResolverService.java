package com.laboschqpa.server.api.service.internal;

import com.laboschqpa.server.api.dto.IndexedFileServingRequestDto;
import com.laboschqpa.server.api.dto.IsUserAuthorizedToResourceResponseDto;
import com.laboschqpa.server.config.auth.user.CustomOauth2User;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SessionResolverService {
    private static final Logger logger = LoggerFactory.getLogger(SessionResolverService.class);

    public IsUserAuthorizedToResourceResponseDto getIsAuthorizedToResource(IndexedFileServingRequestDto indexedFileServingRequestDto, CustomOauth2User authenticationPrincipal) {
        indexedFileServingRequestDto.validateSelf();
        logger.debug("Authorizing IndexedFileServingRequestDto: {}", indexedFileServingRequestDto);

        if (true) {
            return IsUserAuthorizedToResourceResponseDto.builder()
                    .authenticated(true)
                    .authorized(true)//TODO: Do proper triage of authorization here
                    .build();
        } else {
            return IsUserAuthorizedToResourceResponseDto.builder()
                    .authenticated(true)
                    .authorized(false)
                    .build();
        }

    }
}
