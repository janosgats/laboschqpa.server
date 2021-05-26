package com.laboschqpa.server.service.loginauthentication;

import com.laboschqpa.server.enums.auth.OAuth2ProviderRegistration;
import com.laboschqpa.server.service.oauth2.OAuth2Provider;
import com.laboschqpa.server.service.oauth2.Oauth2UserProfileData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExplodedOAuth2UserRequestDto {
    private OAuth2ProviderRegistration providerRegistration;
    private OAuth2Provider oAuth2Provider;
    private Oauth2UserProfileData oauth2UserProfileData;
    private OAuth2UserRequest originalOAuth2UserRequest;
}
