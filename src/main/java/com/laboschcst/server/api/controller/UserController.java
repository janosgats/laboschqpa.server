package com.laboschcst.server.api.controller;

import com.laboschcst.server.api.dto.ProfileDetailsDto;
import com.laboschcst.server.api.service.UserService;
import com.laboschcst.server.exceptions.NotImplementedException;
import org.codehaus.jackson.node.ObjectNode;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profileDetails")
    public ProfileDetailsDto getProfileDetails(@RequestParam(name = "userAccId") Long userAccId) {
        return userService.getProfileDetails(userAccId);
    }

    @PostMapping("/profileDetails")
    public String postProfileDetails(@RequestBody ObjectNode requestBody) {
        throw new NotImplementedException("No post implemented for this method yet!");
    }
}
