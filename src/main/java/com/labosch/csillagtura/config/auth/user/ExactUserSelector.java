package com.labosch.csillagtura.config.auth.user;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.labosch.csillagtura.config.auth.authorities.Authority;
import com.labosch.csillagtura.config.auth.authorities.EnumBasedAuthority;
import com.labosch.csillagtura.entity.User;
import com.labosch.csillagtura.entity.UserEmailAddress;
import com.labosch.csillagtura.repo.UserEmailAddressRepository;
import com.labosch.csillagtura.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class ExactUserSelector {
    @Autowired
    UserEmailAddressRepository userEmailAddressRepository;

    @Autowired
    UserRepository userRepository;

    @Value("${oauth2.provider.github.resource.user-info-uri}")
    private String gitHubUserInfoUri;

    private RestTemplate restTemplate = new RestTemplate();

    public CustomOauth2User getExactUser(OAuth2UserRequest oAuth2UserRequest) {
        Assert.notNull(oAuth2UserRequest, "oAuth2UserRequest cannot be null!");

        CustomOauth2User customOauth2User = new CustomOauth2User();

        String emailAddressFromRequest = null;
        switch (oAuth2UserRequest.getClientRegistration().getRegistrationId()) {
            //Doing provider specific processing of 'oAuth2UserRequest'
            case "google":
                Object buff = ((OidcUserRequest) oAuth2UserRequest).getIdToken().getClaims().get("email");
                if (buff != null)
                    emailAddressFromRequest = (String) buff;
                break;
            case "github":
                JsonObject gitHubResponseJsonObject = sendGitHubApiRequestForUserScope(oAuth2UserRequest.getAccessToken());

                if (gitHubResponseJsonObject.has("email")) {
                    emailAddressFromRequest = gitHubResponseJsonObject.get("email").getAsString();
                }
                break;
        }

        if (emailAddressFromRequest == null || emailAddressFromRequest.isBlank())
            throw new RuntimeException("Cannot log in! E-mail address not found in OAuth2/Oidc resource.");

        Optional<UserEmailAddress> userEmailAddressOptional = userEmailAddressRepository.findByEmail(emailAddressFromRequest);

        User userEntity;

        if (userEmailAddressOptional.isPresent()) {
            //Already registered e-mail address
            UserEmailAddress userEmailAddress = userEmailAddressOptional.get();
            userEntity = userEmailAddress.getUser();
        } else {
            //New e-mal address, register new account
            userEntity = new User();
            userEntity.setEnabled(true);
            userRepository.save(userEntity);

            UserEmailAddress newUserEmailAddress = new UserEmailAddress();
            newUserEmailAddress.setEmail(emailAddressFromRequest);
            newUserEmailAddress.setUser(userEntity);
            userEmailAddressRepository.save(newUserEmailAddress);

            userEntity.getUserEmailAddresses().add(newUserEmailAddress);
        }

        customOauth2User.setUserEntity(userEntity);

        {//This is just dummy data for testing
            customOauth2User.setName("testname");

            Map<String, Object> attributes = new HashMap<>();
            attributes.put("key1", "val1");
            customOauth2User.setAttributes(attributes);

            ArrayList<GrantedAuthority> grantedAuthorities = new ArrayList<>();
            grantedAuthorities.add(new EnumBasedAuthority(Authority.Test1));
            grantedAuthorities.add(new EnumBasedAuthority(Authority.Test2));
            grantedAuthorities.add(new EnumBasedAuthority(Authority.Test3));
            customOauth2User.setAuthorities(grantedAuthorities);
        }

        return customOauth2User;
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
