package com.laboschqpa.server.api.controller;

import com.laboschqpa.server.api.dto.event.GetPersonalEventForUserResponse;
import com.laboschqpa.server.api.dto.event.GetTeamEventForUserResponse;
import com.laboschqpa.server.api.dto.team.GetTeamResponse;
import com.laboschqpa.server.api.dto.user.UserInfoResponse;
import com.laboschqpa.server.api.service.event.EventService;
import com.laboschqpa.server.api.service.event.registration.PersonalEventRegistrationService;
import com.laboschqpa.server.api.service.event.registration.TeamEventRegistrationService;
import com.laboschqpa.server.config.userservice.CustomOauth2User;
import com.laboschqpa.server.entity.account.UserAcc;
import com.laboschqpa.server.enums.TeamRole;
import com.laboschqpa.server.enums.apierrordescriptor.EventApiError;
import com.laboschqpa.server.enums.auth.Authority;
import com.laboschqpa.server.exceptions.apierrordescriptor.EventException;
import com.laboschqpa.server.util.PrincipalAuthorizationHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/event")
public class EventController {
    private final EventService eventService;
    private final PersonalEventRegistrationService personalEventRegistrationService;
    private final TeamEventRegistrationService teamEventRegistrationService;

    @GetMapping("/listPersonalEventsForUser")
    public List<GetPersonalEventForUserResponse> getListPersonalEventsForUser(@AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        long userId = ControllerHelpers.getUserId(authenticationPrincipal);

        return eventService.listPersonalEventsFor(userId).stream()
                .map(GetPersonalEventForUserResponse::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/listTeamEventsForUser")
    public List<GetTeamEventForUserResponse> getListTeamEventsForUser(@AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        long teamId = ControllerHelpers.getTeamId(authenticationPrincipal);

        return eventService.listTeamEventsFor(teamId).stream()
                .map(GetTeamEventForUserResponse::new)
                .collect(Collectors.toList());
    }


    @GetMapping("/listAllRegisteredUsers")
    public List<UserInfoResponse> getListAllRegisteredUsers(@RequestParam("eventId") Long eventId,
                                                            @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        new PrincipalAuthorizationHelper(authenticationPrincipal).assertHasAuthority(Authority.EventEditor);

        return personalEventRegistrationService.listAllRegisteredUsers(eventId).stream()
                .map(u -> new UserInfoResponse(u, false, true))
                .collect(Collectors.toList());
    }

    @GetMapping("/listAllRegisteredTeams")
    public List<GetTeamResponse> getListAllRegisteredTeams(@RequestParam("eventId") Long eventId,
                                                           @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        new PrincipalAuthorizationHelper(authenticationPrincipal).assertHasAuthority(Authority.EventEditor);

        return teamEventRegistrationService.listAllRegisteredTeams(eventId).stream()
                .map(GetTeamResponse::new)
                .collect(Collectors.toList());
    }


    @PostMapping("/registration/personal/register")
    public void postRegisterPersonal(@RequestParam("eventId") Long eventId,
                                     @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        final UserAcc userAcc = ControllerHelpers.getUserAcc(authenticationPrincipal);
        personalEventRegistrationService.register(userAcc, eventId);
    }

    @PostMapping("/registration/personal/deRegister")
    public void postDeRegisterPersonal(@RequestParam("eventId") Long eventId,
                                       @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        final UserAcc userAcc = ControllerHelpers.getUserAcc(authenticationPrincipal);
        personalEventRegistrationService.deRegister(userAcc, eventId);
    }

    @PostMapping("/registration/team/register")
    public void postRegisterTeam(@RequestParam("eventId") Long eventId,
                                 @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        final long teamId = ControllerHelpers.getTeamId(authenticationPrincipal);
        if (ControllerHelpers.getUserAcc(authenticationPrincipal).getTeamRole() != TeamRole.LEADER) {
            throw new EventException(EventApiError.ONLY_LEADERS_CAN_MANAGE_TEAM_EVENT_REGISTRATIONS);
        }
        teamEventRegistrationService.register(teamId, eventId);
    }

    @PostMapping("/registration/team/deRegister")
    public void postDeRegisterTeam(@RequestParam("eventId") Long eventId,
                                   @AuthenticationPrincipal CustomOauth2User authenticationPrincipal) {
        final long teamId = ControllerHelpers.getTeamId(authenticationPrincipal);
        if (ControllerHelpers.getUserAcc(authenticationPrincipal).getTeamRole() != TeamRole.LEADER) {
            throw new EventException(EventApiError.ONLY_LEADERS_CAN_MANAGE_TEAM_EVENT_REGISTRATIONS);
        }
        teamEventRegistrationService.deRegister(teamId, eventId);
    }
}
