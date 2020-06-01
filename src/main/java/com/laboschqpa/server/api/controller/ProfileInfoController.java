package com.laboschqpa.server.api.controller;

import com.laboschqpa.server.api.dto.ugc.profileinfo.GetCurrentProfileInfoDto;
import com.laboschqpa.server.api.dto.ugc.profileinfo.EditCurrentProfileInfoDto;
import com.laboschqpa.server.api.service.ProfileInfoService;
import com.laboschqpa.server.config.userservice.CustomOauth2User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/profileInfo")
public class ProfileInfoController {
    private final ProfileInfoService profileInfoService;

    @GetMapping("/currentProfileInfo")
    public GetCurrentProfileInfoDto getCurrentProfileInfo(@AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        return new GetCurrentProfileInfoDto(authenticationPrincipal.getUserAccEntity());
    }

    @PostMapping("/currentProfileInfo")
    public void postCurrentProfileInfo(@RequestBody EditCurrentProfileInfoDto editCurrentProfileInfoDto,
                                       @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        profileInfoService.setCurrentProfileInfo(editCurrentProfileInfoDto, authenticationPrincipal.getUserAccEntity());
    }
}
