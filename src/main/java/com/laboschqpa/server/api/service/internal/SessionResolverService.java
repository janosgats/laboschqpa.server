package com.laboschqpa.server.api.service.internal;

import com.laboschqpa.server.api.dto.internal.IsUserAuthorizedToResourceRequest;
import com.laboschqpa.server.api.dto.internal.IsUserAuthorizedToResourceResponse;
import com.laboschqpa.server.config.helper.AppConstants;
import com.laboschqpa.server.config.userservice.CustomOauth2User;
import com.laboschqpa.server.entity.account.UserAcc;
import com.laboschqpa.server.enums.filehost.FileAccessType;
import com.laboschqpa.server.repo.UserAccRepository;
import com.laboschqpa.server.service.fileaccess.FileAccessAuthorizer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;
import org.springframework.session.Session;
import org.springframework.session.jdbc.JdbcIndexedSessionRepository;
import org.springframework.stereotype.Service;

@Log4j2
@RequiredArgsConstructor
@Service
public class SessionResolverService {
    private final JdbcIndexedSessionRepository sessionRepository;
    private final UserAccRepository userAccRepository;
    private final FileAccessAuthorizer fileAccessAuthorizer;

    public IsUserAuthorizedToResourceResponse getIsUserAuthorizedToResource(IsUserAuthorizedToResourceRequest request) {
        log.trace("Authorizing with IsUserAuthorizedToResourceRequest.");

        final LoadUserReturn loadUserReturn = loadUser(request);

        if (loadUserReturn.isUltimateReturnValue()) {
            return loadUserReturn.getUltimateResponseDto();
        }

        return authorizeUser(request, loadUserReturn);
    }

    IsUserAuthorizedToResourceResponse authorizeUser(IsUserAuthorizedToResourceRequest request,
                                                     LoadUserReturn loadUserReturn) {
        final Session session = loadUserReturn.getSession();
        final CustomOauth2User authenticationPrincipal = loadUserReturn.getAuthenticationPrincipal();
        final UserAcc userAcc = authenticationPrincipal.getUserAccEntity();

        final IsUserAuthorizedToResourceResponse responseDto = new IsUserAuthorizedToResourceResponse();
        responseDto.setAuthorized(false);//Initially false
        responseDto.setAuthenticated(true);
        responseDto.setLoggedInUserId(userAcc.getId());

        final boolean userIsInATeam;
        if (userAcc.isMemberOrLeaderOfAnyTeam()) {
            responseDto.setLoggedInUserTeamId(userAcc.getTeam().getId());
            userIsInATeam = true;
        } else {
            userIsInATeam = false;
        }

        if (isCsrfTokenValiditySufficient(request, session)) {
            responseDto.setCsrfValid(true);
            switch (request.getFileAccessType()) {
                case READ:
                    FileAccessAuthorizer.File file = new FileAccessAuthorizer.File();
                    file.setId(request.getIndexedFileId());
                    file.setOwnerUserId(request.getIndexedFileOwnerUserId());
                    file.setOwnerTeamId(request.getIndexedFileOwnerTeamId());
                    responseDto.setAuthorized(fileAccessAuthorizer.canUserReadFile(userAcc, file));
                    break;
                case CREATE_NEW:
                    responseDto.setAuthorized(userIsInATeam);//Everyone who is in a team can upload.
                    break;
                default:
                    throw new UnsupportedOperationException("Only READ and CREATE_NEW FileAccessType requests should be authed by this endpoint!");
            }
        } else {
            responseDto.setCsrfValid(false);
            responseDto.setAuthorized(false);
        }
        log.trace("Resource authorization response DTO: {}", responseDto::toString);
        return responseDto;
    }

    LoadUserReturn loadUser(IsUserAuthorizedToResourceRequest request) {
        final Session session = sessionRepository.findById(request.getSessionId());

        if (session == null || session.isExpired()) {
            return new LoadUserReturn(true, unauthenticatedResponseDto(), null, null);
        }

        final SecurityContext securityContext
                = session.getAttribute(WebSessionServerSecurityContextRepository.DEFAULT_SPRING_SECURITY_CONTEXT_ATTR_NAME);
        if (securityContext == null || securityContext.getAuthentication() == null) {
            return new LoadUserReturn(true, unauthenticatedResponseDto(), null, null);
        }

        final Object loadedAuthenticationPrincipal = securityContext.getAuthentication().getPrincipal();
        if (loadedAuthenticationPrincipal == null) {
            return new LoadUserReturn(true, unauthenticatedResponseDto(), null, null);
        }
        if (!(loadedAuthenticationPrincipal instanceof CustomOauth2User)) {
            throw new UnsupportedOperationException("Loaded AuthenticationPrincipal is not instance of CustomOauth2User. ClassName: "
                    + loadedAuthenticationPrincipal.getClass().getName());
        }

        final CustomOauth2User authenticationPrincipal = (CustomOauth2User) loadedAuthenticationPrincipal;
        authenticationPrincipal.refreshUserAccEntityFromDB(userAccRepository);

        final UserAcc userAcc = authenticationPrincipal.getUserAccEntity();
        if (userAcc == null) {
            return new LoadUserReturn(true, unauthenticatedResponseDto(), null, null);
        }

        if (!userAcc.getEnabled()) {
            return new LoadUserReturn(true, unauthenticatedResponseDto(), null, null);
        }

        return new LoadUserReturn(false, null, session, authenticationPrincipal);
    }

    @Getter
    @AllArgsConstructor
    static class LoadUserReturn {
        boolean ultimateReturnValue;
        IsUserAuthorizedToResourceResponse ultimateResponseDto;
        Session session;
        CustomOauth2User authenticationPrincipal;
    }

    boolean isCsrfTokenValiditySufficient(IsUserAuthorizedToResourceRequest request, Session session) {
        if (request.getFileAccessType() == FileAccessType.READ) {
            return true;
        }

        final String csrfTokenToValidate = request.getCsrfToken();

        final Object readRealCsrfToken = session.getAttribute(AppConstants.sessionAttributeNameCsrfToken);
        if (!(readRealCsrfToken instanceof CsrfToken)) {
            return false;
        }
        final CsrfToken realCsrfToken = (CsrfToken) readRealCsrfToken;

        return csrfTokenToValidate != null && csrfTokenToValidate.equals(realCsrfToken.getToken());
    }

    IsUserAuthorizedToResourceResponse unauthenticatedResponseDto() {
        return IsUserAuthorizedToResourceResponse.builder()
                .authenticated(false)
                .authorized(false)
                .csrfValid(false)
                .build();
    }
}
