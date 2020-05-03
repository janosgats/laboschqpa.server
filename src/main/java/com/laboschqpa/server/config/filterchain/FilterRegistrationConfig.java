package com.laboschqpa.server.config.filterchain;

import com.laboschqpa.server.config.filterchain.filter.AddLoginMethodFilter;
import com.laboschqpa.server.config.filterchain.filter.ApiInternalAuthInterServiceFilter;
import com.laboschqpa.server.config.filterchain.filter.RequestCounterFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Forbidding Spring to automatically include Filters to wrong places (wrong orders)
 * in the FilterChain during ComponentScan.
 */
@Configuration
public class FilterRegistrationConfig {
    @Bean
    public FilterRegistrationBean<AddLoginMethodFilter> registrationAddLoginMethodFilter(AddLoginMethodFilter filter) {
        final FilterRegistrationBean<AddLoginMethodFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }

    @Bean
    public FilterRegistrationBean<ApiInternalAuthInterServiceFilter> registrationApiInternalAuthInterServiceFilter(ApiInternalAuthInterServiceFilter filter) {
        final FilterRegistrationBean<ApiInternalAuthInterServiceFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }

    @Bean
    public FilterRegistrationBean<RequestCounterFilter> registrationRequestCounterFilter(RequestCounterFilter filter) {
        final FilterRegistrationBean<RequestCounterFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }
}
