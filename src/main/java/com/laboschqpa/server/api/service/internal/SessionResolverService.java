package com.laboschqpa.server.api.service.internal;

import com.laboschqpa.server.api.dto.internal.IsUserAuthorizedToResourceRequestDto;
import com.laboschqpa.server.api.dto.internal.IsUserAuthorizedToResourceResponseDto;
import com.laboschqpa.server.config.userservice.CustomOauth2User;
import com.laboschqpa.server.entity.account.UserAcc;
import com.laboschqpa.server.enums.ugc.UserGeneratedContentType;
import com.laboschqpa.server.repo.usergeneratedcontent.RiddleRepository;
import com.laboschqpa.server.repo.usergeneratedcontent.UserGeneratedContentRepository;
import com.laboschqpa.server.service.PrincipalAuthorizationHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Log4j2
@RequiredArgsConstructor
@Service
public class SessionResolverService {
    private final CsrfTokenRepository csrfTokenRepository;
    private final UserGeneratedContentRepository userGeneratedContentRepository;
    private final RiddleRepository riddleRepository;

    public IsUserAuthorizedToResourceResponseDto getIsAuthorizedToResource(IsUserAuthorizedToResourceRequestDto isUserAuthorizedToResourceRequestDto,
                                                                           CustomOauth2User authenticationPrincipal,
                                                                           HttpServletRequest request) {
        isUserAuthorizedToResourceRequestDto.validateSelf();
        log.trace("Authorizing with IsUserAuthorizedToResourceRequestDto.");

        UserAcc userAcc = authenticationPrincipal.getUserAccEntity();
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

        if (isCsrfTokenValiditySufficient(isUserAuthorizedToResourceRequestDto, request)) {
            responseDto.setCsrfValid(true);
            switch (isUserAuthorizedToResourceRequestDto.getFileAccessType()) {
                case READ:
                    if (new PrincipalAuthorizationHelper(authenticationPrincipal).hasAdminAuthority()) {
                        responseDto.setAuthorized(true);
                    } else {
                        responseDto.setAuthorized(isReadRequestAuthorized(isUserAuthorizedToResourceRequestDto, userIsInATeam, userAcc));
                    }
                    break;
                case CREATE_NEW:
                    responseDto.setAuthorized(userIsInATeam);
                    break;
                default:
                    throw new IllegalStateException("Only READ and CREATE_NEW FileAccessType requests should be authed by this endpoint!");
            }
        } else {
            responseDto.setCsrfValid(false);
            responseDto.setAuthorized(false);
        }
        log.trace("Resource authorization response DTO: {}", responseDto::toString);
        return responseDto;
    }


    /**
     * BEHAVIOR: If the requested file is attached to riddles, it can only be accessed
     * if at least one of these riddles is accessible for the user's team.
     */
    private boolean isReadRequestAuthorized(final IsUserAuthorizedToResourceRequestDto requestDto,
                                            final boolean userIsInATeam, final UserAcc userAcc) {
        final Long requestedFileId = requestDto.getIndexedFileId();
        final List<Long> attachedToRiddleIds = userGeneratedContentRepository.getIdsForSpecificUgcTypeWithAttachedFile(UserGeneratedContentType.RIDDLE, requestedFileId);
        if (attachedToRiddleIds.size() == 0) {
            return true;
        } else {
            if (userIsInATeam) {
                final List<Long> accessibleRiddleIds = riddleRepository.findAccessibleRiddleIds(userAcc.getTeam().getId());
                return attachedToRiddleIds.stream().anyMatch(accessibleRiddleIds::contains);//returns if any of the attached riddles is accessible
            } else {
                return false;
            }
        }
    }

    private boolean isCsrfTokenValiditySufficient(IsUserAuthorizedToResourceRequestDto isUserAuthorizedToResourceRequestDto, HttpServletRequest request) {
        HttpMethod method = isUserAuthorizedToResourceRequestDto.getHttpMethod();
        if (method == HttpMethod.POST
                || method == HttpMethod.DELETE
                || method == HttpMethod.PUT
                || method == HttpMethod.PATCH) {
            String csrfTokenToValidate = isUserAuthorizedToResourceRequestDto.getCsrfToken();
            String realCsrfToken = csrfTokenRepository.loadToken(request).getToken();

            return csrfTokenToValidate != null && csrfTokenToValidate.equals(realCsrfToken);
        } else
            return true;
    }
}
