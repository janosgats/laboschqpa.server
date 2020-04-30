package com.laboschqpa.server.service.loginauthentication;

import com.laboschqpa.server.config.userservice.CustomOauth2User;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;

public interface UserAccountLoginManager {

    CustomOauth2User getExactUserFromOAuth2UserRequest(OAuth2UserRequest oAuth2UserRequest);
}
