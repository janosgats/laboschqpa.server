package com.laboschcst.server.api.controller;

import com.google.gson.JsonObject;
import com.laboschcst.server.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.laboschcst.server.api.ApiSupplierExecutor;

@RestController
@RequestMapping(path = "/api/user")
public class UserController {
    @Autowired
    ApiSupplierExecutor apiSupplierExecutor;

    @Autowired
    UserService userService;

    @GetMapping("/profileDetails")
    public ResponseEntity<JsonObject> getProfileDetails(@RequestParam(name = "userAccId") Long userAccId) {
        return apiSupplierExecutor.executeAndGetJsonObjectOrCatch(() -> userService.getProfileDetails(userAccId));
    }

    @PostMapping("/profileDetails")
    public ResponseEntity<JsonObject> postProfileDetails(@RequestBody JsonObject requestBody) {
        return apiSupplierExecutor.executeAndGetJsonObjectOrCatch(() -> userService.setProfileDetails(requestBody));
    }
}
