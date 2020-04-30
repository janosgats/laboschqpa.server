package com.laboschqpa.server.service.oauth2;

import com.laboschqpa.server.entity.account.UserAcc;
import com.laboschqpa.server.entity.account.externalaccountdetail.ExternalAccountDetail;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;

public abstract class AbstractOAuth2ProviderService {
    public abstract ExtractedOAuth2UserRequestDataDto extractDataFromOauth2UserRequest(OAuth2UserRequest oAuth2UserRequest);

    public abstract UserAcc loadUserAccFromDbByExternalAccountDetail(ExternalAccountDetail externalAccountDetail);

    public final void saveExternalAccountDetailForUserAcc(ExternalAccountDetail externalAccountDetail, UserAcc userAcc) {
        externalAccountDetail.setUserAcc(userAcc);
        saveExternalAccountDetail(externalAccountDetail);
    }

    protected abstract void saveExternalAccountDetail(ExternalAccountDetail externalAccountDetail);
}
