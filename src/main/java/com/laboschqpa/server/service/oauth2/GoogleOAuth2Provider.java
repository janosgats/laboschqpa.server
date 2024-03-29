package com.laboschqpa.server.service.oauth2;

import com.laboschqpa.server.entity.account.UserAcc;
import com.laboschqpa.server.entity.account.externalaccountdetail.ExternalAccountDetail;
import com.laboschqpa.server.entity.account.externalaccountdetail.GoogleExternalAccountDetail;
import com.laboschqpa.server.exceptions.authentication.DefectiveAuthProviderResponseAuthenticationException;
import com.laboschqpa.server.repo.externalaccountdetail.GoogleExternalAccountDetailRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Log4j2
@RequiredArgsConstructor
@Service
public class GoogleOAuth2Provider implements OAuth2Provider {
    private final GoogleExternalAccountDetailRepository googleExternalAccountDetailRepository;


    @Override
    public Oauth2UserProfileData extractDataFromOauth2UserRequest(OAuth2UserRequest oAuth2UserRequest) {
        Map<String, Object> claims = ((OidcUserRequest) oAuth2UserRequest).getIdToken().getClaims();
        final Object googleSubObject = claims.get("sub");
        final String firstName = (String) claims.get("given_name");
        final String lastName = (String) claims.get("family_name");
        final String profilePicUrl = (String) claims.get("picture");
        if (!(googleSubObject instanceof String) || ((String) googleSubObject).isBlank()) {
            throw new DefectiveAuthProviderResponseAuthenticationException("Google OAuth2 sub is invalid!");
        }

        GoogleExternalAccountDetail googleExternalAccountDetail = new GoogleExternalAccountDetail();
        googleExternalAccountDetail.setSub((String) googleSubObject);

        final String emailAddress = tryToGetEmailFromClaims(claims);

        final String nickName = Helpers.getNickName(firstName, lastName, emailAddress, null, "Google");
        return new Oauth2UserProfileData(googleExternalAccountDetail, emailAddress, firstName, lastName, nickName, profilePicUrl);
    }

    private String tryToGetEmailFromClaims(Map<String, Object> claims) {
        try {
            Object buff = claims.get("email");
            if (buff instanceof String && !((String) buff).isBlank())
                return (String) buff;
        } catch (Exception ignored) {
        }
        return null;
    }

    @Override
    public UserAcc loadUserAccFromDbByExternalAccountDetail(ExternalAccountDetail externalAccountDetail) {
        Optional<GoogleExternalAccountDetail> googleExternalAccountDetailOptional =
                googleExternalAccountDetailRepository.findBySub(((GoogleExternalAccountDetail) externalAccountDetail).getSub());
        if (googleExternalAccountDetailOptional.isPresent()) {
            return googleExternalAccountDetailOptional.get().getUserAcc();
        } else {
            return null;
        }
    }

    @Override
    public void saveExternalAccountDetailForUserAcc(ExternalAccountDetail externalAccountDetail, UserAcc userAcc) {
        externalAccountDetail.setUserAcc(userAcc);
        googleExternalAccountDetailRepository.save((GoogleExternalAccountDetail) externalAccountDetail);
    }

    @Override
    public ExternalAccountDetail instantiateExternalAccountDetail() {
        return new GoogleExternalAccountDetail();
    }
}
