package com.labosch.csillagtura.config.auth.filter;

import com.labosch.csillagtura.config.AppConstants;
import com.labosch.csillagtura.config.auth.user.CustomOauth2User;
import com.labosch.csillagtura.entity.UserAcc;
import com.labosch.csillagtura.repo.UserAccRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Optional;


public class PrincipalUserEntityRefresherFromDBFilter extends GenericFilterBean {
    Logger logger = LoggerFactory.getLogger(PrincipalUserEntityRefresherFromDBFilter.class);

    private UserAccRepository userAccRepository;


    @Override
    /**
     * Logs out the users if they are not present/enabled in DB.
     * Refreshes the User entity in session if they are.
     */
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        SecurityContext sc = SecurityContextHolder.getContext();
        Object principal = sc.getAuthentication().getPrincipal();

        boolean shouldBeUnauthenticated = true;

        if (principal instanceof CustomOauth2User) {
            CustomOauth2User customOauth2User = (CustomOauth2User) principal;
            Long userIdFromSession = customOauth2User.getUserId();

            if (userIdFromSession != null) {
                Optional<UserAcc> userFromDBOptional = userAccRepository.findById(userIdFromSession);

                if (userFromDBOptional.isPresent()) {
                    UserAcc userAccFromDB = userFromDBOptional.get();

                    if (userAccFromDB.getEnabled()) {
                        customOauth2User.setUserAccEntity(userAccFromDB);
                        shouldBeUnauthenticated = false;
                    }
                }
            }
        } else {
            shouldBeUnauthenticated = false;//User isn't logged in
        }

        if (shouldBeUnauthenticated) {
            logger.info("Unauthenticating user.");
            sc.setAuthentication(null);

            if (servletRequest instanceof HttpServletRequest) {
                HttpSession session = ((HttpServletRequest) servletRequest).getSession();
                if (session != null)
                    session.invalidate();
            }

            servletRequest.getRequestDispatcher(AppConstants.loginPageUrl).forward(servletRequest, servletResponse);
        } else
            filterChain.doFilter(servletRequest, servletResponse);
    }

    public void setUserAccRepository(UserAccRepository userAccRepository) {
        this.userAccRepository = userAccRepository;
    }
}
