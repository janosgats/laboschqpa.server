package com.laboschcst.server.api.controller;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.laboschcst.server.config.auth.user.CustomOauth2User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/sessionresolver")
public class SessionResolverController {

    @GetMapping("/user")
    public ObjectNode getUserAcc(@AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        ObjectNode objectNode = new ObjectNode(JsonNodeFactory.instance);
        objectNode.put("userAccId", authenticationPrincipal.getUserId());
        return objectNode;
    }

    @GetMapping("/isauthorizedtoresource")
    public ObjectNode getIsAuthorizedToResource(@RequestParam("resourceId") String resourceId,
                                                @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        ObjectNode objectNode = new ObjectNode(JsonNodeFactory.instance);
        objectNode.put("isAuthorized", true);//TODO: Do proper triage of authorization here
        return objectNode;
    }
}
