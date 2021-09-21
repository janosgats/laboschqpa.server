package com.laboschqpa.server.api.controller;

import com.laboschqpa.server.api.dto.user.PostSetUserInfoRequest;
import com.laboschqpa.server.api.dto.user.UserInfoResponse;
import com.laboschqpa.server.api.dto.user.UsersPageUserInfoResponse;
import com.laboschqpa.server.api.service.UserService;
import com.laboschqpa.server.config.userservice.CustomOauth2User;
import com.laboschqpa.server.exceptions.UnAuthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/user")
public class UserController {
    private final UserService userService;

    @GetMapping("/info")
    public UserInfoResponse getInfo(@RequestParam("id") Long id) {
        return new UserInfoResponse(userService.getById(id));
    }

    @GetMapping("/infoWithAuthorities")
    public UserInfoResponse getInfoWithAuthorities(@RequestParam("id") Long id) {
        return new UserInfoResponse(userService.getByIdWithAuthorities(id), true, false);
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

    @GetMapping("/listAll")
    public List<UserInfoResponse> getListAll() {
        return userService.listAll().stream()
                .map(UserInfoResponse::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/listAllWithTeamName")
    public List<UserInfoResponse> getListAllWithTeam() {
        return userService.listAllWithTeam().stream()
                .map(u -> new UserInfoResponse(u, false, true))
                .collect(Collectors.toList());
    }

    @GetMapping("/usersPage/listAllEnabled")
    public List<UsersPageUserInfoResponse> getUsersPageListAllEnabled() {
        return userService.listAllWithTeam().stream()
                .map(u -> new UsersPageUserInfoResponse(u, true))
                .collect(Collectors.toList());
    }
}
