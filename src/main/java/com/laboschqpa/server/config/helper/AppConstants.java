package com.laboschqpa.server.config.helper;

import org.springframework.ui.Model;

import java.util.HashMap;
import java.util.Map;

public class AppConstants {
    public static final String oAuth2AuthorizationRequestBaseUri = "/login/oauth2/";//This HAS TO HAVE a "/" (slash) character at the end!
    public static final String logOutUrl = "/logout";
    public static final String loginPageUrl = "/loginPage";

    public static final String defaultLoginSuccessUrl = "/loginSuccess";
    public static final String defaultLoginFailureUrl = "/loginFailure";

    public static final String logOutSuccessUrl = "/";

    public static final String errorPageUrlAntPattern = "/error/**";

    public static final String apiInternalUrl = "/api/internal";
    public static final String apiInternalUrlAntPattern = apiInternalUrl + "/**";

    public static final String apiNoAuthRequiredUrl = "/api/noAuthRequired";
    public static final String apiNoAuthRequiredUrlAntPattern = apiNoAuthRequiredUrl + "/**";

    public static final String prometheusMetricsExposeUrl = "/actuator/prometheus";

    //language=RegExp
    public static final String generalNameValidatorPattern = "^[a-zA-Z0-9!_'áéíóöőúüűÁÉÍÓÖŐÚÜŰ -+]*$";
    //language=RegExp
    public static final String nickNameValidatorPattern = "^[a-zA-Z0-9!_(),':%áéíóöőúüűÁÉÍÓÖŐÚÜŰ -+]*$";

    public static final String sessionAttributeNameCsrfToken = "CSRF_TOKEN";

    public static void preFillModel(Model model) {
        Map<String, String> constants = new HashMap<>();
        constants.put("logoutUrl", logOutUrl);
        constants.put("loginPageUrl", loginPageUrl);

        model.addAttribute("const", constants);
    }
}
