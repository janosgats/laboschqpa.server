package com.labosch.csillagtura.config.auth.user;

import com.labosch.csillagtura.config.auth.authorities.Authority;
import com.labosch.csillagtura.config.auth.authorities.EnumBasedAuthority;
import com.labosch.csillagtura.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Component
public class ExactUserSelector {

    public CustomOauth2User getExactUser(OAuth2UserRequest oAuth2UserRequest) {
        Assert.notNull(oAuth2UserRequest, "oAuth2UserRequest cannot be null!");

        CustomOauth2User customOauth2User = new CustomOauth2User();

        switch (oAuth2UserRequest.getClientRegistration().getRegistrationId()){
            //Doing provider specific processing of 'oAuth2UserRequest'
            case "google":
                break;
            case "github":
                break;
        }

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

            customOauth2User.setUserEntity(new User());
            customOauth2User.getUserEntity().setId(7L);
            customOauth2User.getUserEntity().setEnabled(true);
            customOauth2User.getUserEntity().setEmail("test@test.test");
        }

        return customOauth2User;
    }
}
