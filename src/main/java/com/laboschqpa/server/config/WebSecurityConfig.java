package com.laboschqpa.server.config;

import com.laboschqpa.server.config.authprovider.OAuth2ProviderRegistrationFactory;
import com.laboschqpa.server.config.filterchain.extension.CustomAuthenticationFailureHandler;
import com.laboschqpa.server.config.filterchain.extension.CustomAuthenticationSuccessHandler;
import com.laboschqpa.server.config.filterchain.extension.ReloadUserPerRequestHttpSessionSecurityContextRepository;
import com.laboschqpa.server.config.filterchain.filter.AddLoginMethodFilter;
import com.laboschqpa.server.config.filterchain.filter.ApiInternalAuthInterServiceFilter;
import com.laboschqpa.server.config.filterchain.filter.ApiRedirectionOAuth2AuthorizationRequestRedirectFilter;
import com.laboschqpa.server.config.filterchain.filter.RequestCounterFilter;
import com.laboschqpa.server.config.helper.AppConstants;
import com.laboschqpa.server.config.userservice.CustomOAuth2UserService;
import com.laboschqpa.server.config.userservice.CustomOidcUserService;
import com.laboschqpa.server.enums.auth.Authority;
import com.laboschqpa.server.enums.auth.OAuth2ProviderRegistrations;
import com.laboschqpa.server.repo.UserAccRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.security.web.csrf.LazyCsrfTokenRepository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@EnableWebSecurity
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(WebSecurityConfig.class);

    @Resource
    UserAccRepository userAccRepository;

    @Resource
    ApplicationContext applicationContext;

    @Value("${management.endpoints.web.base-path}")
    private String springBootActuatorBaseUrl;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers(AppConstants.adminBaseUrl + "**", springBootActuatorBaseUrl + "/**", springBootActuatorBaseUrl + "**")
                .hasAuthority(Authority.Admin.getStringValue())
                .antMatchers("/", AppConstants.apiNoAuthRequiredUrl + "**", AppConstants.loginPageUrl, AppConstants.defaultLoginFailureUrl, AppConstants.oAuth2AuthorizationRequestBaseUri + "**", AppConstants.errorPageUrl + "**")
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
                .ignoringAntMatchers(AppConstants.apiNoAuthRequiredUrl + "**")

                .and()
                .cors()

                .and()
                .logout()
                .logoutUrl(AppConstants.logOutUrl)
                .logoutSuccessUrl(AppConstants.logOutSuccessUrl)
                .invalidateHttpSession(true);

        insertCustomFilters(http);
    }

    private void insertCustomFilters(HttpSecurity http) {
        http.addFilterAfter(applicationContext.getBean(RequestCounterFilter.class), WebAsyncManagerIntegrationFilter.class);

        http.addFilterAfter(applicationContext.getBean((ApiInternalAuthInterServiceFilter.class)), RequestCounterFilter.class);

        http.addFilterBefore(new SecurityContextPersistenceFilter(new ReloadUserPerRequestHttpSessionSecurityContextRepository(userAccRepository)),
                SecurityContextPersistenceFilter.class);//Replacing original SecurityContextPersistenceFilter (by using FILTER_APPLIED flag with the same key as the original filter)

        http.addFilterBefore(applicationContext.getBean(AddLoginMethodFilter.class), OAuth2AuthorizationRequestRedirectFilter.class);

        http.addFilterBefore(new ApiRedirectionOAuth2AuthorizationRequestRedirectFilter(
                applicationContext.getBean((ClientRegistrationRepository.class)),
                AppConstants.oAuth2AuthorizationRequestBaseUri
        ), OAuth2AuthorizationRequestRedirectFilter.class);
        // TODO: The original filter is also called during the FilterChain execution, so this isn't the best solution.
        //  The (original) OAuth2AuthorizationRequestRedirectFilter should be completely replaced with the new filter!
    }

    @Bean
    public CsrfTokenRepository csrfTokenRepository() {
        return new LazyCsrfTokenRepository(new HttpSessionCsrfTokenRepository());
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository(OAuth2ProviderRegistrationFactory oAuth2ProviderRegistrationFactory) {
        List<ClientRegistration> registrations = new ArrayList<>();
        registrations.add(oAuth2ProviderRegistrationFactory.createProviderRegistration(OAuth2ProviderRegistrations.Google));
        registrations.add(oAuth2ProviderRegistrationFactory.createProviderRegistration(OAuth2ProviderRegistrations.GitHub));

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
        return new DefaultOAuth2ClientContext();
    }
}