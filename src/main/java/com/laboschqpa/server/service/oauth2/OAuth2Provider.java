package com.laboschqpa.server.service.oauth2;

import com.laboschqpa.server.entity.account.UserAcc;
import com.laboschqpa.server.entity.account.externalaccountdetail.ExternalAccountDetail;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;

public interface OAuth2Provider {
    Oauth2UserProfileData extractDataFromOauth2UserRequest(OAuth2UserRequest oAuth2UserRequest);

    UserAcc loadUserAccFromDbByExternalAccountDetail(ExternalAccountDetail externalAccountDetail);

    void saveExternalAccountDetailForUserAcc(ExternalAccountDetail externalAccountDetail, UserAcc userAcc);

    ExternalAccountDetail instantiateExternalAccountDetail();
}
