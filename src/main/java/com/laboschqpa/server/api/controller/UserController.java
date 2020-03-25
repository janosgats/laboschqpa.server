package com.laboschqpa.server.api.controller;

import com.laboschqpa.server.api.dto.ProfileDetailsDto;
import com.laboschqpa.server.api.service.UserService;
import com.laboschqpa.server.config.auth.user.CustomOauth2User;
import com.laboschqpa.server.exceptions.UnAuthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/user")
public class UserController {
    private final UserService userService;

    @GetMapping("/profileDetails")
    public ProfileDetailsDto getProfileDetails(@RequestParam(name = "userAccId") Long userAccId) {
        return userService.getProfileDetails(userAccId);
    }

    @PostMapping("/profileDetails")
    public void postProfileDetails(@RequestBody ProfileDetailsDto profileDetailsDto,
                                   @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        if (!profileDetailsDto.getUserAccId().equals(authenticationPrincipal.getUserId()))
            throw new UnAuthorizedException("You cannot modify someone else's profile details!");

        userService.saveProfileDetails(profileDetailsDto);
    }
}
