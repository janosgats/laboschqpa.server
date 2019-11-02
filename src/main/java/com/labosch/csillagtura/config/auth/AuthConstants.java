package com.labosch.csillagtura.config.auth;

import java.util.Arrays;
import java.util.List;

public class AuthConstants {
    public static final String oAuthAuthorizationRequestBaseUri = "/login/";//This HAS TO HAVE a "/" (slash) character at the end!
    public static final String logOutUrl = "/logout";
    public static final String loginPageUrl = "/loginPage";

    public static final String defaultLoginSuccessUrl = "/loginSuccess";
    public static final String defaultLoginFailureUrl = "/loginFailure";

    public static final String logOutSuccessUrl = "/";

    public static final List<String> oAuth2ProviderRegistrationIds = Arrays.asList("google", "github");
}
