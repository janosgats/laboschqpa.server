package com.laboschqpa.server.service.oauth2;

import com.laboschqpa.server.entity.account.UserAcc;
import com.laboschqpa.server.entity.account.externalaccountdetail.AuthSchExternalAccountDetail;
import com.laboschqpa.server.entity.account.externalaccountdetail.ExternalAccountDetail;
import com.laboschqpa.server.exceptions.authentication.DefectiveAuthProviderResponseAuthenticationException;
import com.laboschqpa.server.repo.externalaccountdetail.AuthSchExternalAccountDetailRepository;
import com.laboschqpa.server.service.apiclient.authsch.AuthSchApiClient;
import com.laboschqpa.server.service.apiclient.authsch.AuthSchUserInfoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;

@Log4j2
@RequiredArgsConstructor
@Service
public class AuthSchOAuth2Provider implements OAuth2Provider {
    private final AuthSchExternalAccountDetailRepository authSchExternalAccountDetailRepository;
    private final AuthSchApiClient authSchApiClient;


    @Override
    public Oauth2UserProfileData extractDataFromOauth2UserRequest(OAuth2UserRequest oAuth2UserRequest) {
        AuthSchUserInfoDto userInfo = getValidUserInfo(oAuth2UserRequest.getAccessToken());

        AuthSchExternalAccountDetail externalAccountDetail = new AuthSchExternalAccountDetail();
        externalAccountDetail.setInternalId(userInfo.getInternalId());

        final String nickName = Helpers.getNickName(userInfo.getFirstName(), userInfo.getLastName(),
                userInfo.getEmail(), userInfo.getSchAcc(), "AuthSch");

        return new Oauth2UserProfileData(externalAccountDetail, userInfo.getEmail(),
                userInfo.getFirstName(), userInfo.getLastName(), nickName, null);
    }

    private AuthSchUserInfoDto getValidUserInfo(OAuth2AccessToken accessToken) {
        final AuthSchUserInfoDto authSchUserInfoDto;
        try {
            authSchUserInfoDto = authSchApiClient.getOAuth2UserInfo(accessToken);
        } catch (Exception e) {
            throw new DefectiveAuthProviderResponseAuthenticationException("Cannot get OAuth2 UserInfo resource from AuthSch", e);
        }

        if (!authSchUserInfoDto.isValid()) {
            throw new DefectiveAuthProviderResponseAuthenticationException("AuthSch OAuth2 UserInfo resource response is invalid!");
        }
        return authSchUserInfoDto;
    }

    @Override
    public UserAcc loadUserAccFromDbByExternalAccountDetail(ExternalAccountDetail externalAccountDetail) {
        return authSchExternalAccountDetailRepository
                .findByInternalId(((AuthSchExternalAccountDetail) externalAccountDetail).getInternalId())
                .map(ExternalAccountDetail::getUserAcc)
                .orElse(null);
    }

    @Override
    public void saveExternalAccountDetailForUserAcc(ExternalAccountDetail externalAccountDetail, UserAcc userAcc) {
        externalAccountDetail.setUserAcc(userAcc);
        authSchExternalAccountDetailRepository.save((AuthSchExternalAccountDetail) externalAccountDetail);
    }

    @Override
    public ExternalAccountDetail instantiateExternalAccountDetail() {
        return new AuthSchExternalAccountDetail();
    }
}
