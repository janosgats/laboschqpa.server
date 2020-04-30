package com.laboschqpa.server.service.loginauthentication;

import com.laboschqpa.server.enums.auth.OAuth2ProviderRegistrations;
import com.laboschqpa.server.service.oauth2.AbstractOAuth2ProviderService;
import com.laboschqpa.server.service.oauth2.ExtractedOAuth2UserRequestDataDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExplodedOAuth2UserRequestDto {
    private OAuth2ProviderRegistrations providerRegistration;
    private AbstractOAuth2ProviderService oAuth2ProviderService;
    private ExtractedOAuth2UserRequestDataDto extractedOAuth2UserRequestDataDto;
    private OAuth2UserRequest originalOAuth2UserRequest;
}
