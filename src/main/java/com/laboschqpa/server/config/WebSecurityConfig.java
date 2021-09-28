package com.laboschqpa.server.config;

import com.laboschqpa.server.config.authprovider.OAuth2ProviderRegistrationFactory;
import com.laboschqpa.server.config.filterchain.extension.*;
import com.laboschqpa.server.config.filterchain.filter.AddLoginMethodFilter;
import com.laboschqpa.server.config.filterchain.filter.ApiInternalAuthInterServiceFilter;
import com.laboschqpa.server.config.filterchain.filter.ApiRedirectionOAuth2AuthorizationRequestRedirectFilter;
import com.laboschqpa.server.config.helper.AppConstants;
import com.laboschqpa.server.config.userservice.CustomOAuth2UserService;
import com.laboschqpa.server.config.userservice.CustomOidcUserService;
import com.laboschqpa.server.enums.auth.Authority;
import com.laboschqpa.server.enums.auth.OAuth2ProviderRegistration;
import com.laboschqpa.server.repo.UserAccRepository;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
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
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.security.web.csrf.LazyCsrfTokenRepository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Log4j2
@EnableWebSecurity
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Resource
    UserAccRepository userAccRepository;

    @Resource
    ApplicationContext applicationContext;

    @Value("${management.endpoints.web.base-path}")
    private String springBootActuatorBaseUrl;

    @Value("#{T(com.laboschqpa.server.config.WebSecurityConfig).processAllowedOriginsProperty('${oauth2.allowedOverriddenRedirectionOrigins:}')}")
    private List<String> oauth2AllowedOverriddenRedirectionOrigins;

    public static List<String> processAllowedOriginsProperty(String allowedOriginsProperty) {
        if (StringUtils.isBlank(allowedOriginsProperty)) {
            return new ArrayList<>();
        }
        return Arrays.asList(StringUtils.split(allowedOriginsProperty, ','));
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        log.info("Allowed OAuth2 redirection origin overrides ({}pcs): {}",
                oauth2AllowedOverriddenRedirectionOrigins.size(), String.join(",", oauth2AllowedOverriddenRedirectionOrigins));

        http.authorizeRequests()
                .antMatchers(AppConstants.prometheusMetricsExposeUrl, AppConstants.apiInternalUrlAntPattern)
                .permitAll()
                .antMatchers(springBootActuatorBaseUrl + "/**", springBootActuatorBaseUrl + "**")
                .hasAuthority(Authority.Admin.getStringValue())
                .antMatchers("/", AppConstants.apiNoAuthRequiredUrlAntPattern, AppConstants.loginPageUrl, AppConstants.defaultLoginFailureUrl, AppConstants.oAuth2AuthorizationRequestBaseUri + "**", AppConstants.errorPageUrlAntPattern)
                .permitAll()
                .anyRequest()
                .hasAnyAuthority(Authority.User.getStringValue(), Authority.Admin.getStringValue())

                .and()
                .oauth2Login()
                .loginPage(AppConstants.loginPageUrl)
                .authorizationEndpoint()
                .baseUri(AppConstants.oAuth2AuthorizationRequestBaseUri)
                .authorizationRequestRepository(authorizationRequestRepository())

                .and()
                .userInfoEndpoint()
                .userService(applicationContext.getBean(CustomOAuth2UserService.class))
                .oidcUserService(applicationContext.getBean(CustomOidcUserService.class))

                .and()
                .tokenEndpoint()
                .accessTokenResponseClient(accessTokenResponseClient())

                .and()
                .successHandler(customAuthenticationSuccessHandler())
                .failureHandler(customAuthenticationFailureHandler())

                .and()
                .csrf()
                .csrfTokenRepository(csrfTokenRepository())
                .ignoringAntMatchers(AppConstants.apiNoAuthRequiredUrlAntPattern)

                .and()
                .cors()

                .and()
                .logout()
                .logoutUrl(AppConstants.logOutUrl)
                .logoutSuccessHandler(new CustomLogoutSuccessHandler())
                .invalidateHttpSession(true)

                .and()
                .exceptionHandling()
                .authenticationEntryPoint(new Http403ForbiddenEntryPoint());

        insertCustomFilters(http);
    }

    private void insertCustomFilters(HttpSecurity http) {
        http.addFilterAfter(applicationContext.getBean(ApiInternalAuthInterServiceFilter.class), WebAsyncManagerIntegrationFilter.class);

        http.addFilterBefore(new SecurityContextPersistenceFilter(new ReloadUserPerRequestHttpSessionSecurityContextRepository(userAccRepository)),
                SecurityContextPersistenceFilter.class);//Replacing original SecurityContextPersistenceFilter (by using FILTER_APPLIED flag with the same key as the original filter)

        http.addFilterBefore(applicationContext.getBean(AddLoginMethodFilter.class), OAuth2AuthorizationRequestRedirectFilter.class);

        ClientRegistrationRepository clientRegistrationRepository = applicationContext.getBean(ClientRegistrationRepository.class);
        http.addFilterBefore(new ApiRedirectionOAuth2AuthorizationRequestRedirectFilter(
                clientRegistrationRepository,
                new OriginOverridingOAuth2AuthorizationRequestResolver(
                        clientRegistrationRepository,
                        AppConstants.oAuth2AuthorizationRequestBaseUri,
                        oauth2AllowedOverriddenRedirectionOrigins
                ),
                AppConstants.oAuth2AuthorizationRequestBaseUri
        ), OAuth2AuthorizationRequestRedirectFilter.class);
        // TODO: The original filter is also called during the FilterChain execution, so this isn't the best solution.
        //  The (original) OAuth2AuthorizationRequestRedirectFilter should be completely replaced with the new filter!
    }

    @Bean
    public CsrfTokenRepository csrfTokenRepository() {
        HttpSessionCsrfTokenRepository httpSessionCsrfTokenRepository = new HttpSessionCsrfTokenRepository();
        httpSessionCsrfTokenRepository.setSessionAttributeName(AppConstants.sessionAttributeNameCsrfToken);

        return new LazyCsrfTokenRepository(httpSessionCsrfTokenRepository);
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository(OAuth2ProviderRegistrationFactory oAuth2ProviderRegistrationFactory) {
        List<ClientRegistration> registrations = new ArrayList<>();
        registrations.add(oAuth2ProviderRegistrationFactory.createProviderRegistration(OAuth2ProviderRegistration.Google));
        registrations.add(oAuth2ProviderRegistrationFactory.createProviderRegistration(OAuth2ProviderRegistration.GitHub));
        registrations.add(oAuth2ProviderRegistrationFactory.createProviderRegistration(OAuth2ProviderRegistration.AuthSch));

        return new InMemoryClientRegistrationRepository(registrations);
    }

    @Bean
    public CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return new CustomAuthenticationSuccessHandler(csrfTokenRepository());
    }

    @Bean
    public CustomAuthenticationFailureHandler customAuthenticationFailureHandler() {
        return new CustomAuthenticationFailureHandler();
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
        //TODO: Update this deprecated thing
        return new DefaultOAuth2ClientContext();
    }
}