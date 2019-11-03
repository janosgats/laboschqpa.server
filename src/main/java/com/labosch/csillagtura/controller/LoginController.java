package com.labosch.csillagtura.controller;

import com.labosch.csillagtura.config.AppConstants;
import com.labosch.csillagtura.config.auth.user.CustomOauth2User;
import com.labosch.csillagtura.entity.UserEmailAddress;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class LoginController {
    @GetMapping(AppConstants.loginPageUrl)
    public String getLoginPage(Model model) {
        Map<String, String> oauth2AuthenticationUrls = new HashMap<>();

        oauth2AuthenticationUrls.put("Google", AppConstants.oAuthAuthorizationRequestBaseUri + "google");
        oauth2AuthenticationUrls.put("GitHub", AppConstants.oAuthAuthorizationRequestBaseUri + "github");

        model.addAttribute("urls", oauth2AuthenticationUrls);

        return "loginPage";
    }

    @GetMapping("/loginSuccess")
    public String getLogSuc(Model model) {
        AppConstants.preFillModel(model);

        SecurityContext sc = SecurityContextHolder.getContext();
        List<UserEmailAddress> buff = ((CustomOauth2User) sc.getAuthentication().getPrincipal()).getUserEntity().getUserEmailAddresses();
        if (!buff.isEmpty())
            model.addAttribute("name", buff.get(0).getEmail());

        return "loginSuccess";
    }

    @GetMapping("/loginFailure")
    public String getLoginFailure(Model model) {
        return "loginFailure";
    }

    @GetMapping("/")
    public String getRootForward(Model model) {
        AppConstants.preFillModel(model);
        return "rootIndex";
    }
}
