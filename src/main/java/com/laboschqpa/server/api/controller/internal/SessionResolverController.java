package com.laboschqpa.server.api.controller.internal;

import com.laboschqpa.server.api.dto.internal.IsUserAuthorizedToResourceRequestDto;
import com.laboschqpa.server.api.dto.internal.IsUserAuthorizedToResourceResponseDto;
import com.laboschqpa.server.api.service.internal.SessionResolverService;
import com.laboschqpa.server.config.helper.AppConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RestController
@RequestMapping(path = AppConstants.apiInternalUrl + "/sessionResolver")
public class SessionResolverController {
    private final SessionResolverService sessionResolverService;

    @GetMapping("/isUserAuthorizedToResource")
    public IsUserAuthorizedToResourceResponseDto getIsUserAuthorizedToResource(@RequestBody IsUserAuthorizedToResourceRequestDto isUserAuthorizedToResourceRequestDto) {
        return sessionResolverService.getIsUserAuthorizedToResource(isUserAuthorizedToResourceRequestDto);
    }
}
