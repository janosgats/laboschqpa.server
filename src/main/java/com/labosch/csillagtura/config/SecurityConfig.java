package com.labosch.csillagtura.config;

import com.labosch.csillagtura.config.auth.user.CustomOAuth2UserService;
import com.labosch.csillagtura.config.auth.user.CustomOidcUserService;
import com.labosch.csillagtura.service.SecretProviderService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Resource
    private Environment env;

    @Resource
    SecretProviderService secretProviderService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/", AppConstants.loginPageUrl, AppConstants.defaultLoginFailureUrl, AppConstants.oAuthAuthorizationRequestBaseUri + "**", AppConstants.errorPageUrl + "**")
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .oauth2Login()
                .loginPage(AppConstants.loginPageUrl)
                .authorizationEndpoint()
                .baseUri(AppConstants.oAuthAuthorizationRequestBaseUri)
                .authorizationRequestRepository(authorizationRequestRepository())
                .and()
                .userInfoEndpoint().userService(oauth2UserService()).oidcUserService(oidcUserService())
                .and()
                .tokenEndpoint()
                .accessTokenResponseClient(accessTokenResponseClient())
                .and()
                .defaultSuccessUrl(AppConstants.defaultLoginSuccessUrl)
                .failureUrl(AppConstants.defaultLoginFailureUrl)
                .and()
                .logout()
                .logoutUrl(AppConstants.logOutUrl)
                .logoutSuccessUrl(AppConstants.logOutSuccessUrl)
                .invalidateHttpSession(true);
    }

    @Bean
    public CustomOAuth2UserService oauth2UserService() {
        return new CustomOAuth2UserService();
    }

    @Bean
    public CustomOidcUserService oidcUserService() {
        return new CustomOidcUserService();
    }

    @Bean
    public AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository() {
        return new HttpSessionOAuth2AuthorizationRequestRepository();
    }

    @Bean
    public OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient() {
        return new DefaultAuthorizationCodeTokenResponseClient();
    }

    @Bean
    public OAuth2ClientContext oAuth2ClientContext() {
        return new DefaultOAuth2ClientContext();
    }


    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        List<ClientRegistration> registrations = AppConstants.oAuth2ProviderRegistrationIds.stream()
                .map(this::getRegistration)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return new InMemoryClientRegistrationRepository(registrations);
    }

    private ClientRegistration getRegistration(String client) {
        //String clientId = env.getProperty("oauth2.provider." + client + ".client.client-id");
        String clientId = secretProviderService.getSecret("oauth2.provider." + client + ".client.client-id");

        if (clientId == null) {
            return null;
        }

        //String clientSecret = env.getProperty("oauth2.provider." + client + ".client.client-secret");
        String clientSecret = secretProviderService.getSecret("oauth2.provider." + client + ".client.client-secret");

        if (client.equals("google")) {
            return CommonOAuth2Provider.GOOGLE.getBuilder(client)
                    .clientId(clientId)
                    .clientSecret(clientSecret)
                    .build();
        }

        if (client.equals("github")) {
            return CommonOAuth2Provider.GITHUB.getBuilder(client)
                    .clientId(clientId)
                    .clientSecret(clientSecret)
                    .build();
        }
//
//        if (client.equals("facebook")) {
//            return CommonOAuth2Provider.FACEBOOK.getBuilder(client)
//                    .clientId(clientId)
//                    .clientSecret(clientSecret)
//                    .build();
//        }
        return null;
    }
}