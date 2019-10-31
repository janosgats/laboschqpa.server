package com.labosch.csillagtura.config;

import com.labosch.csillagtura.accounts.extractor.GoogleAuthoritiesExtractor;
import com.labosch.csillagtura.accounts.extractor.GooglePrincipalExtractor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.AuthoritiesExtractor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.filter.CompositeFilter;

import javax.annotation.Resource;
import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Resource
    private Environment env;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
//        http.authorizeRequests()
//                .antMatchers("/oauth_login", "/loginFailure", "/login**")
//                .permitAll()
//                .anyRequest()
//                .authenticated()
//                .and()
//                .oauth2Login()
//                .loginPage("/oauth_login")
//                .authorizationEndpoint()
//                .baseUri("/login")
//                .authorizationRequestRepository(authorizationRequestRepository())
//                .and()
//                .tokenEndpoint()
//                .accessTokenResponseClient(accessTokenResponseClient())
//                .and()
//                .defaultSuccessUrl("/loginSuccess")
//                .failureUrl("/loginFailure");

        http.addFilterBefore(ssoFilter(), BasicAuthenticationFilter.class);
    }

    @Bean
    public AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository() {
        return new HttpSessionOAuth2AuthorizationRequestRepository();
    }

    @Bean
    public OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient() {
        DefaultAuthorizationCodeTokenResponseClient accessTokenResponseClient = new DefaultAuthorizationCodeTokenResponseClient();
        return accessTokenResponseClient;
    }

    @Bean
    public PrincipalExtractor googlePrincipalExtractor() {
        return new GooglePrincipalExtractor();
    }

    @Bean
    public AuthoritiesExtractor googleAuthoritiesExtractor() {
        return new GoogleAuthoritiesExtractor();
    }

    @Bean
    @ConfigurationProperties("google.client")
    public AuthorizationCodeResourceDetails google() {
        return new AuthorizationCodeResourceDetails();
    }

    @Bean
    @ConfigurationProperties("google.resource")
    public ResourceServerProperties googleResource() {
        return new ResourceServerProperties();
    }


    @Bean
    public OAuth2ClientContext oAuth2ClientContext() {
        return new DefaultOAuth2ClientContext();
    }

    public Filter ssoFilter() {
        CompositeFilter filter = new CompositeFilter();
        List<Filter> filters = new ArrayList<>();
        SimpleUrlAuthenticationSuccessHandler successHandler = new SimpleUrlAuthenticationSuccessHandler();
        successHandler.setAlwaysUseDefaultTargetUrl(true);
        successHandler.setDefaultTargetUrl("/loginSuccess");

//        // Facebook
//        OAuth2ClientAuthenticationProcessingFilter facebookFilter = new OAuth2ClientAuthenticationProcessingFilter("/login/facebook");
//        OAuth2RestTemplate facebookTemplate = new OAuth2RestTemplate(facebook(), oauth2ClientContext);
//        facebookFilter.setRestTemplate(facebookTemplate);
//        UserInfoTokenServices facebookTokenServices = new UserInfoTokenServices(facebookResource().getUserInfoUri(), facebook().getClientId());
//        facebookTokenServices.setRestTemplate(facebookTemplate);
//        facebookTokenServices.setPrincipalExtractor(facebookPrincipalExtractor);
//        facebookTokenServices.setAuthoritiesExtractor(facebookAuthoritiesExtractor);
//
//        facebookFilter.setTokenServices(facebookTokenServices);
//        facebookFilter.setAuthenticationSuccessHandler(successHandler);
//
//        filters.add(facebookFilter);

        // Google
        OAuth2ClientAuthenticationProcessingFilter googleFilter = new OAuth2ClientAuthenticationProcessingFilter("/login/oauth2/code/google");

        OAuth2RestTemplate googleTemplate = new OAuth2RestTemplate(google(), oAuth2ClientContext());
        googleFilter.setRestTemplate(googleTemplate);
        UserInfoTokenServices googleTokenServices = new UserInfoTokenServices(googleResource().getUserInfoUri(), google().getClientId());
        googleTokenServices.setRestTemplate(googleTemplate);
        googleTokenServices.setPrincipalExtractor(googlePrincipalExtractor());
        googleTokenServices.setAuthoritiesExtractor(googleAuthoritiesExtractor());

        googleFilter.setTokenServices(googleTokenServices);
        googleFilter.setAuthenticationSuccessHandler(successHandler);

        filters.add(googleFilter);

        //
        filter.setFilters(filters);
        return filter;
    }

    private static List<String> clients = Arrays.asList("google");

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        List<ClientRegistration> registrations = clients.stream()
                .map(c -> getRegistration(c))
                .filter(registration -> registration != null)
                .collect(Collectors.toList());

        return new InMemoryClientRegistrationRepository(registrations);
    }

    private ClientRegistration getRegistration(String client) {
        String clientId = env.getProperty(client + ".client.client-id");

        if (clientId == null) {
            return null;
        }

        String clientSecret = env.getProperty(client + ".client.client-secret");
        if (client.equals("google")) {
            return CommonOAuth2Provider.GOOGLE.getBuilder(client)
                    .clientId(clientId)
                    .clientSecret(clientSecret)
                    .build();
        }
//        if (client.equals("facebook")) {
//            return CommonOAuth2Provider.FACEBOOK.getBuilder(client)
//                    .clientId(clientId)
//                    .clientSecret(clientSecret)
//                    .build();
//        }
        return null;
    }
}