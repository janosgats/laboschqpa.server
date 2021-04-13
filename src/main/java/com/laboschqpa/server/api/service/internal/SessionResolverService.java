package com.laboschqpa.server.api.service.internal;

import com.laboschqpa.server.api.dto.internal.IsUserAuthorizedToResourceRequestDto;
import com.laboschqpa.server.api.dto.internal.IsUserAuthorizedToResourceResponseDto;
import com.laboschqpa.server.config.helper.AppConstants;
import com.laboschqpa.server.config.userservice.CustomOauth2User;
import com.laboschqpa.server.entity.account.UserAcc;
import com.laboschqpa.server.enums.auth.Authority;
import com.laboschqpa.server.enums.filehost.FileAccessType;
import com.laboschqpa.server.enums.ugc.UserGeneratedContentType;
import com.laboschqpa.server.repo.UserAccRepository;
import com.laboschqpa.server.repo.usergeneratedcontent.RiddleRepository;
import com.laboschqpa.server.repo.usergeneratedcontent.UserGeneratedContentRepository;
import com.laboschqpa.server.util.PrincipalAuthorizationHelper;
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

import java.util.List;

@Log4j2
@RequiredArgsConstructor
@Service
public class SessionResolverService {
    private final JdbcIndexedSessionRepository sessionRepository;
    private final UserGeneratedContentRepository userGeneratedContentRepository;
    private final RiddleRepository riddleRepository;
    private final UserAccRepository userAccRepository;

    public IsUserAuthorizedToResourceResponseDto getIsUserAuthorizedToResource(IsUserAuthorizedToResourceRequestDto requestDto) {
        requestDto.validateSelf();
        log.trace("Authorizing with IsUserAuthorizedToResourceRequestDto.");

        final LoadUserReturn loadUserReturn = loadUser(requestDto);

        if (loadUserReturn.isUltimateReturnValue()) {
            return loadUserReturn.getUltimateResponseDto();
        }

        return authorizeUser(requestDto, loadUserReturn);
    }

    IsUserAuthorizedToResourceResponseDto authorizeUser(IsUserAuthorizedToResourceRequestDto requestDto,
                                                        LoadUserReturn loadUserReturn) {
        final Session session = loadUserReturn.getSession();
        final CustomOauth2User authenticationPrincipal = loadUserReturn.getAuthenticationPrincipal();
        final UserAcc userAcc = authenticationPrincipal.getUserAccEntity();

        final IsUserAuthorizedToResourceResponseDto responseDto = new IsUserAuthorizedToResourceResponseDto();
        responseDto.setAuthorized(false);//Initially false
        responseDto.setAuthenticated(true);
        responseDto.setLoggedInUserId(userAcc.getId());

        final boolean userIsInATeam;
        if (userAcc.getTeamRole().isMemberOrLeader() && userAcc.getTeam() != null) {
            responseDto.setLoggedInUserTeamId(userAcc.getTeam().getId());
            userIsInATeam = true;
        } else {
            userIsInATeam = false;
        }

        if (isCsrfTokenValiditySufficient(requestDto, session)) {
            responseDto.setCsrfValid(true);
            switch (requestDto.getFileAccessType()) {
                case READ:
                    responseDto.setAuthorized(isReadRequestAuthorized(requestDto, userIsInATeam, userAcc, authenticationPrincipal));
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

    LoadUserReturn loadUser(IsUserAuthorizedToResourceRequestDto requestDto) {
        final Session session = sessionRepository.findById(requestDto.getSessionId());

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
        IsUserAuthorizedToResourceResponseDto ultimateResponseDto;
        Session session;
        CustomOauth2User authenticationPrincipal;
    }

    /**
     * BEHAVIOR: If the requested file is attached to riddles, it can only be accessed
     * if at least one of these riddles is accessible for the user's team.
     */
    boolean isReadRequestAuthorized(final IsUserAuthorizedToResourceRequestDto requestDto, final boolean userIsInATeam,
                                    final UserAcc userAcc, final CustomOauth2User authenticationPrincipal) {
        if (new PrincipalAuthorizationHelper(authenticationPrincipal)
                .hasAnySufficientAuthority(Authority.RiddleEditor, Authority.Admin)) {
            return true;
        }

        final Long requestedFileId = requestDto.getIndexedFileId();
        final List<Long> attachedToRiddleIds = userGeneratedContentRepository.getIdsForSpecificUgcTypeWithAttachedFile(UserGeneratedContentType.RIDDLE, requestedFileId);
        if (attachedToRiddleIds.size() == 0) {
            return true;//The file is not a riddle attachment
        }

        if (userIsInATeam) {
            final List<Long> accessibleRiddleIds = riddleRepository.findAccessibleRiddleIds(userAcc.getTeam().getId());
            return attachedToRiddleIds.stream().anyMatch(accessibleRiddleIds::contains);//The attached riddles is accessible for the user
        }

        return false;
    }

    boolean isCsrfTokenValiditySufficient(IsUserAuthorizedToResourceRequestDto requestDto, Session session) {
        if (requestDto.getFileAccessType() == FileAccessType.READ) {
            return true;
        }

        final String csrfTokenToValidate = requestDto.getCsrfToken();

        final Object readRealCsrfToken = session.getAttribute(AppConstants.sessionAttributeNameCsrfToken);
        if (!(readRealCsrfToken instanceof CsrfToken)) {
            return false;
        }
        final CsrfToken realCsrfToken = (CsrfToken) readRealCsrfToken;

        return csrfTokenToValidate != null && csrfTokenToValidate.equals(realCsrfToken.getToken());
    }

    IsUserAuthorizedToResourceResponseDto unauthenticatedResponseDto() {
        return IsUserAuthorizedToResourceResponseDto.builder()
                .authenticated(false)
                .authorized(false)
                .csrfValid(false)
                .build();
    }
}
