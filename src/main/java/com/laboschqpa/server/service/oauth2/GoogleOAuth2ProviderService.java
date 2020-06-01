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
public class GoogleOAuth2ProviderService extends AbstractOAuth2ProviderService {
    private final GoogleExternalAccountDetailRepository googleExternalAccountDetailRepository;


    @Override
    public ExtractedOAuth2UserRequestDataDto extractDataFromOauth2UserRequest(OAuth2UserRequest oAuth2UserRequest) {
        Map<String, Object> claims = ((OidcUserRequest) oAuth2UserRequest).getIdToken().getClaims();
        final Object googleSubObject = claims.get("sub");
        final String firstName = (String) claims.get("given_name");
        final String lastName = (String) claims.get("family_name");
        if (!(googleSubObject instanceof String) || ((String) googleSubObject).isBlank()) {
            throw new DefectiveAuthProviderResponseAuthenticationException("Google OAuth2 sub is invalid!");
        }

        GoogleExternalAccountDetail googleExternalAccountDetail = new GoogleExternalAccountDetail();
        googleExternalAccountDetail.setSub((String) googleSubObject);

        String emailAddress = tryToGetEmailFromClaims(claims);
        return new ExtractedOAuth2UserRequestDataDto(googleExternalAccountDetail, emailAddress, firstName, lastName, null);
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
    protected void saveExternalAccountDetail(ExternalAccountDetail externalAccountDetail) {
        googleExternalAccountDetailRepository.save((GoogleExternalAccountDetail) externalAccountDetail);
    }
}
