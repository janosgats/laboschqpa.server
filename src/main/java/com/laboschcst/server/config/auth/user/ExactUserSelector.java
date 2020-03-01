package com.laboschcst.server.config.auth.user;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.laboschcst.server.enums.Authority;
import com.laboschcst.server.config.auth.authorities.EnumBasedAuthority;
import com.laboschcst.server.entity.account.UserAcc;
import com.laboschcst.server.entity.account.UserEmailAddress;
import com.laboschcst.server.entity.account.externalaccountdetail.ExternalAccountDetail;
import com.laboschcst.server.entity.account.externalaccountdetail.GithubExternalAccountDetail;
import com.laboschcst.server.entity.account.externalaccountdetail.GoogleExternalAccountDetail;
import com.laboschcst.server.repo.GithubExternalAccountDetailRepository;
import com.laboschcst.server.repo.GoogleExternalAccountDetailRepository;
import com.laboschcst.server.repo.UserEmailAddressRepository;
import com.laboschcst.server.repo.UserAccRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

@Component
public class ExactUserSelector {
    Logger logger = LoggerFactory.getLogger(ExactUserSelector.class);

    @Autowired
    UserEmailAddressRepository userEmailAddressRepository;

    @Autowired
    GoogleExternalAccountDetailRepository googleExternalAccountDetailRepository;

    @Autowired
    GithubExternalAccountDetailRepository githubExternalAccountDetailRepository;

    @Autowired
    UserAccRepository userAccRepository;

    @Value("${oauth2.provider.github.resource.user-info-uri}")
    private String gitHubUserInfoUri;

    private RestTemplate restTemplate = new RestTemplate();

    public CustomOauth2User getExactUser(OAuth2UserRequest oAuth2UserRequest) {
        Assert.notNull(oAuth2UserRequest, "oAuth2UserRequest cannot be null!");

        CustomOauth2User customOauth2User = new CustomOauth2User();

        String clientRegistrationId = oAuth2UserRequest.getClientRegistration().getRegistrationId();

        ExternalAccountDetail externalAccountDetail = null;
        String emailAddressFromRequest = null;//We try to automatically merge (not create new) accounts by their e-mail addresses if possible.
        switch (clientRegistrationId) {
            //Doing provider specific processing of 'oAuth2UserRequest'
            case "google":
                Map<String, Object> claims = ((OidcUserRequest) oAuth2UserRequest).getIdToken().getClaims();
                Object googleBuff = claims.get("sub");

                if (googleBuff instanceof String && !((String) googleBuff).isBlank()) {
                    GoogleExternalAccountDetail googleEADBuff = new GoogleExternalAccountDetail();
                    googleEADBuff.setSub((String) googleBuff);

                    externalAccountDetail = googleEADBuff;
                }

                emailAddressFromRequest = tryToGetGoogleEmail(claims);
                break;
            case "github":
                JsonObject gitHubResponseJsonObject = sendGitHubApiRequestForUserScope(oAuth2UserRequest.getAccessToken());

                if (gitHubResponseJsonObject.has("id")) {
                    String githubId = gitHubResponseJsonObject.get("id").getAsString();
                    if (!githubId.isBlank()) {
                        GithubExternalAccountDetail githubEADBuff = new GithubExternalAccountDetail();
                        githubEADBuff.setGithubId(githubId);

                        externalAccountDetail = githubEADBuff;
                    }
                }

                emailAddressFromRequest = tryToGetGithubEmail(gitHubResponseJsonObject);
                break;
            default:
                throw new RuntimeException("clientRegistrationId not found");
        }

        if (externalAccountDetail == null)
            throw new RuntimeException("Cannot log in! Cannot found external account ID in Google OAuth2/Oidc resource.");


        UserEmailAddress userEmailAddressFromRequestTriedToLoadFromDB = load_UserEmailAddressFromRequest_FromDB_IfPresent(emailAddressFromRequest);


        UserAcc userAccEntity = null;

        switch (clientRegistrationId) {
            case "google":
                Optional<GoogleExternalAccountDetail> googleExternalAccountDetailOptional =
                        googleExternalAccountDetailRepository.findBySub(((GoogleExternalAccountDetail) externalAccountDetail).getSub());

                if (googleExternalAccountDetailOptional.isPresent()) {
                    userAccEntity = googleExternalAccountDetailOptional.get().getUserAcc();
                    logger.debug("Logged in existing user by matching externalAccountDetail from: " + clientRegistrationId);
                } else {
                    if (userEmailAddressFromRequestTriedToLoadFromDB != null) {
                        //The e-mail from the request is belonging to a registered account. Adding externalAccountDetail to that account.
                        userAccEntity = userEmailAddressFromRequestTriedToLoadFromDB.getUserAcc();

                        externalAccountDetail.setUserAcc(userAccEntity);
                    } else {
                        //Couldn't found anything to merge accounts by. Creating new account
                        userAccEntity = registerNewUser();

                        externalAccountDetail.setUserAcc(userAccEntity);
                        googleExternalAccountDetailRepository.save((GoogleExternalAccountDetail) externalAccountDetail);

                        logger.info("Registered new user from: " + clientRegistrationId);
                    }
                }
                break;
            case "github":
                Optional<GithubExternalAccountDetail> githubExternalAccountDetailOptional =
                        githubExternalAccountDetailRepository.findByGithubId(((GithubExternalAccountDetail) externalAccountDetail).getGithubId());

                if (githubExternalAccountDetailOptional.isPresent()) {
                    userAccEntity = githubExternalAccountDetailOptional.get().getUserAcc();
                    logger.debug("Logged in existing user by matching externalAccountDetail from: " + clientRegistrationId);
                } else {
                    if (userEmailAddressFromRequestTriedToLoadFromDB != null) {
                        //The e-mail from the request is belonging to a registered account. Adding externalAccountDetail to that account.
                        userAccEntity = userEmailAddressFromRequestTriedToLoadFromDB.getUserAcc();

                        externalAccountDetail.setUserAcc(userAccEntity);
                        githubExternalAccountDetailRepository.save((GithubExternalAccountDetail) externalAccountDetail);
                    } else {
                        //Couldn't found anything to merge accounts by. Creating new account
                        userAccEntity = registerNewUser();

                        externalAccountDetail.setUserAcc(userAccEntity);
                        githubExternalAccountDetailRepository.save((GithubExternalAccountDetail) externalAccountDetail);

                        logger.info("Registered new user from: " + clientRegistrationId);
                    }
                }
                break;
        }

        if (userAccEntity == null)
            throw new RuntimeException("Cannot log in! 'userEntity' is null but it should have been created or loaded already.");

        if (!userAccEntity.getEnabled())
            throw new RuntimeException("Account is disabled.");

        if (userEmailAddressFromRequestTriedToLoadFromDB == null) {
            //Saving the new e-mail address to the loaded/registered userEntity

            UserEmailAddress newUserEmailAddress = new UserEmailAddress();
            newUserEmailAddress.setEmail(emailAddressFromRequest);
            newUserEmailAddress.setUserAcc(userAccEntity);
            userEmailAddressRepository.save(newUserEmailAddress);
            userAccEntity.getUserEmailAddresses().add(newUserEmailAddress);
        }

        customOauth2User.setUserAccEntity(userAccEntity);

        return customOauth2User;
    }

    private UserAcc registerNewUser() {
        UserAcc newUserAcc = new UserAcc();

        newUserAcc.setEnabled(true);
        newUserAcc.setAuthorities_FromEnumBasedAuthority(Set.of(new EnumBasedAuthority(Authority.User)));

        userAccRepository.save(newUserAcc);
        return newUserAcc;
    }

    /**
     * IF the returned value is not null, then this e-mail address is already belonging to a registered account.
     * So IF the externalAccountDetail is not found in DB then we should add it to the account that this e-mail belongs to.
     *
     * @return {@link UserEmailAddress}UserEmailAddress if the given email is present in DB, or <code>null</code> if not
     **/
    private UserEmailAddress load_UserEmailAddressFromRequest_FromDB_IfPresent(String emailAddressFromRequest) {
        if (emailAddressFromRequest != null) {
            Optional<UserEmailAddress> userEmailAddressOptional = userEmailAddressRepository.findByEmail(emailAddressFromRequest);
            if (userEmailAddressOptional.isPresent()) {
                return userEmailAddressOptional.get();
            }
        }
        return null;
    }

    private String tryToGetGoogleEmail(Map<String, Object> claims) {
        try {
            Object buff = claims.get("email");
            if (buff instanceof String && !((String) buff).isBlank())
                return (String) buff;
        } catch (Exception ignored) {
        }
        return null;
    }

    private String tryToGetGithubEmail(JsonObject gitHubResponseJsonObject) {
        try {
            if (gitHubResponseJsonObject.has("email")) {
                String githubEmail = gitHubResponseJsonObject.get("email").getAsString();
                if (!githubEmail.isBlank()) {
                    return githubEmail;
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private JsonObject sendGitHubApiRequestForUserScope(OAuth2AccessToken accessToken) {
        try {

            MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            headers.add("Authorization", accessToken.getTokenType().getValue() + " " + accessToken.getTokenValue());
            RequestEntity requestEntity = new RequestEntity(headers, HttpMethod.GET, new URI(gitHubUserInfoUri));

            ResponseEntity<String> response = restTemplate.exchange(requestEntity, String.class);
            return new GsonBuilder().create().fromJson(response.getBody(), JsonObject.class);
            // return response.getBody();
        } catch (URISyntaxException e) {
            throw new RuntimeException("BAD Uri Syntax for github User-Info-Uri", e);
        }
    }
}
