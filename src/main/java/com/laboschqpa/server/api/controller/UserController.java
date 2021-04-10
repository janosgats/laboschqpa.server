package com.laboschqpa.server.api.controller;

import com.laboschqpa.server.api.dto.user.PostSetUserInfoRequest;
import com.laboschqpa.server.api.dto.user.UserInfoResponse;
import com.laboschqpa.server.api.service.UserService;
import com.laboschqpa.server.config.userservice.CustomOauth2User;
import com.laboschqpa.server.exceptions.UnAuthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/user")
public class UserController {
    private final UserService userService;

    @GetMapping("/info")
    public UserInfoResponse getInfo(long id) {
        return new UserInfoResponse(userService.getById(id));
    }

    @PostMapping("/setInfo")
    public void postSetInfo(@RequestBody PostSetUserInfoRequest postSetUserInfoRequest,
                            @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        postSetUserInfoRequest.validateSelf();
        if (!postSetUserInfoRequest.getUserId().equals(authenticationPrincipal.getUserId())) {
            throw new UnAuthorizedException("You don't have permissions to edit someone else's profile!");
        }
        userService.setUserInfo(postSetUserInfoRequest);
    }
}
