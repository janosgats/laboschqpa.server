package com.laboschcst.server.api.controller;

import com.google.gson.JsonObject;
import com.laboschcst.server.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.laboschcst.server.api.ApiSupplierExecutor;

@RestController
public class UserController {
    @Autowired
    ApiSupplierExecutor apiSupplierExecutor;

    @Autowired
    UserService userService;

    @GetMapping("/api/user/profileDetails")
    JsonObject getProfileDetails(@RequestParam(name = "userAccId") Long userAccId) {
        return apiSupplierExecutor.executeAndGetJsonObjectOrCatch(() -> userService.getProfileDetails(userAccId));
    }
}
