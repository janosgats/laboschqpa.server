package com.laboschqpa.server.config;

import org.springframework.ui.Model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppConstants {
    public static final String oAuthAuthorizationRequestBaseUri = "/login/";//This HAS TO HAVE a "/" (slash) character at the end!
    public static final String logOutUrl = "/logout";
    public static final String loginPageUrl = "/loginPage";

    public static final String adminBaseUrl = "/admin/";//This HAS TO HAVE a "/" (slash) character at the end!

    public static final String defaultLoginSuccessUrl = "/loginSuccess";
    public static final String defaultLoginFailureUrl = "/loginFailure";

    public static final String logOutSuccessUrl = "/";

    public static final String errorPageUrl = "/error/";//This HAS TO HAVE a "/" (slash) character at the end!

    public static final String apiInternalUrl = "/api/internal/";//This HAS TO HAVE a "/" (slash) character at the end!

    public static final List<String> oAuth2ProviderRegistrationIds = Arrays.asList("google", "github");

    public static void preFillModel(Model model) {
        Map<String, String> constants = new HashMap<>();
        constants.put("logoutUrl", logOutUrl);
        constants.put("loginPageUrl", loginPageUrl);

        model.addAttribute("const", constants);
    }
}
