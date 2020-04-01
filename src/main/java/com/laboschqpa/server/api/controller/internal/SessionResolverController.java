package com.laboschqpa.server.api.controller.internal;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.laboschqpa.server.api.dto.IndexedFileServingRequestDto;
import com.laboschqpa.server.api.dto.IsUserAuthorizedToResourceResponseDto;
import com.laboschqpa.server.api.service.internal.SessionResolverService;
import com.laboschqpa.server.config.AppConstants;
import com.laboschqpa.server.config.auth.user.CustomOauth2User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = AppConstants.apiInternalUrl + "/sessionResolver")
public class SessionResolverController {
    private final SessionResolverService sessionResolverService;

    @GetMapping("/user")
    public ObjectNode getUserAcc(@AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        ObjectNode objectNode = new ObjectNode(JsonNodeFactory.instance);
        objectNode.put("userAccId", authenticationPrincipal.getUserId());
        return objectNode;
    }

    @GetMapping("/isUserAuthorizedToResource")
    public IsUserAuthorizedToResourceResponseDto getIsAuthorizedToResource(@RequestBody IndexedFileServingRequestDto indexedFileServingRequestDto,
                                                                           @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        return sessionResolverService.getIsAuthorizedToResource(indexedFileServingRequestDto, authenticationPrincipal);
    }
}