package com.laboschqpa.server.api.service.admin;

import com.laboschqpa.server.config.auth.user.CustomOauth2User;
import com.laboschqpa.server.entity.account.UserAcc;
import com.laboschqpa.server.exceptions.UsersAdminException;
import com.laboschqpa.server.repo.UserAccRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class UsersAdminService {
    private final UserAccRepository userAccRepository;

    public Long loginAsUser(Long userAccIdToLogInAs) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication originalAuthentication = securityContext.getAuthentication();
        UserAcc originalUserAccEntity = ((CustomOauth2User) originalAuthentication.getPrincipal()).getUserAccEntity();

        CustomOauth2User newCustomOauth2User = new CustomOauth2User();
        newCustomOauth2User.setUserIdAndLoadFromDb(userAccIdToLogInAs, userAccRepository);

        Authentication newAuthentication;
        if (originalAuthentication instanceof UsernamePasswordAuthenticationToken) {
            newAuthentication = new UsernamePasswordAuthenticationToken(newCustomOauth2User, originalAuthentication.getCredentials(), newCustomOauth2User.getAuthorities());
        } else if (originalAuthentication instanceof OAuth2AuthenticationToken) {
            newAuthentication = new OAuth2AuthenticationToken(newCustomOauth2User, newCustomOauth2User.getAuthorities(), ((OAuth2AuthenticationToken) originalAuthentication).getAuthorizedClientRegistrationId());
        } else {
            throw new UsersAdminException("Unexpected authentication type of originalAuthentication: " + originalAuthentication.getClass().getName());
        }
        securityContext.setAuthentication(newAuthentication);
        log.info("Logged in as a different user. original userAccId: {} new (logged in as) userAccId: {}", originalUserAccEntity.getId(), userAccIdToLogInAs);
        return newCustomOauth2User.getUserAccEntity().getId();
    }
}
