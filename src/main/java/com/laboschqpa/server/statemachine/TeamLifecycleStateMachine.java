package com.laboschqpa.server.statemachine;

import com.laboschqpa.server.api.dto.team.CreateNewTeamRequest;
import com.laboschqpa.server.entity.Team;
import com.laboschqpa.server.entity.account.UserAcc;
import com.laboschqpa.server.enums.TeamRole;
import com.laboschqpa.server.enums.apierrordescriptor.TeamLifecycleApiError;
import com.laboschqpa.server.exceptions.apierrordescriptor.TeamLifecycleException;
import com.laboschqpa.server.repo.UserAccRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class TeamLifecycleStateMachine {
    private final UserAcc alteredUserAcc;
    private final UserAcc initiatorUserAcc;
    private final UserAccRepository userAccRepository;

    public void createNewTeam(CreateNewTeamRequest createNewTeamRequest) {
        assertInitiatorIsSameAsAltered();

        if (alteredUserAcc.getTeamRole() != TeamRole.NOTHING || alteredUserAcc.getTeam() != null)
            throw new TeamLifecycleException(TeamLifecycleApiError.YOU_ARE_ALREADY_MEMBER_OR_APPLICANT_OF_A_TEAM,
                    "You can only create a new team if you aren't a member or applicant of any other team!");

        Team newTeam = new Team();
        newTeam.setName(createNewTeamRequest.getName());
        alteredUserAcc.setTeam(newTeam);
        alteredUserAcc.setTeamRole(TeamRole.LEADER);

        log.debug("UserAcc {} created new team: {}", alteredUserAcc.getId(), createNewTeamRequest.getName());
    }

    public void applyToTeam(Team team) {
        assertInitiatorIsSameAsAltered();

        if (alteredUserAcc.getTeamRole() != TeamRole.NOTHING || alteredUserAcc.getTeam() != null)
            throw new TeamLifecycleException(TeamLifecycleApiError.YOU_ARE_ALREADY_MEMBER_OR_APPLICANT_OF_A_TEAM,
                    "You can apply to a team only if you aren't a member or applicant of an other team!");

        alteredUserAcc.setTeam(team);
        alteredUserAcc.setTeamRole(TeamRole.APPLICANT);

        log.debug("UserAcc {} applied to Team {}.", alteredUserAcc.getId(), team.getId());

        if (team.getAutoApproveApplications()) {
            alteredUserAcc.setTeamRole(TeamRole.MEMBER);
            log.debug("Application of UserAcc {} to Team {} was automatically approved.", alteredUserAcc.getId(), team.getId());
        }
    }

    public void cancelApplicationToTeam() {
        assertInitiatorIsSameAsAltered();

        if (alteredUserAcc.getTeamRole() != TeamRole.APPLICANT)
            throw new TeamLifecycleException(TeamLifecycleApiError.OPERATION_IS_INVALID_FOR_TEAM_ROLE_OF_ALTERED, "You can cancel only an applied UserAcc application!");

        alteredUserAcc.setTeam(null);
        alteredUserAcc.setTeamRole(TeamRole.NOTHING);


        log.debug("Canceled application for UserAcc {}.", alteredUserAcc.getId());
    }

    public void declineApplicationToTeam() {
        assertInitiatorIsDifferentThanAltered_and_initiatorIsLeaderOfTeamOfTheAltered();

        if (alteredUserAcc.getTeamRole() != TeamRole.APPLICANT)
            throw new TeamLifecycleException(TeamLifecycleApiError.OPERATION_IS_INVALID_FOR_TEAM_ROLE_OF_ALTERED, "You can decline only an applied UserAcc application!");

        alteredUserAcc.setTeam(null);
        alteredUserAcc.setTeamRole(TeamRole.NOTHING);

        log.debug("Declined application for UserAcc {}.", alteredUserAcc.getId());
    }

    public void approveApplication() {
        assertInitiatorIsDifferentThanAltered_and_initiatorIsLeaderOfTeamOfTheAltered();

        if (alteredUserAcc.getTeamRole() == TeamRole.APPLICANT && alteredUserAcc.getTeam() != null) {
            alteredUserAcc.setTeamRole(TeamRole.MEMBER);
        } else {
            throw new TeamLifecycleException(TeamLifecycleApiError.OPERATION_IS_INVALID_FOR_TEAM_ROLE_OF_ALTERED, "The user you try to accept the application of isn't an applicant!");
        }

        log.debug("Approved application for UserAcc {}.", alteredUserAcc.getId());
    }

    public void leaveTeam() {
        assertInitiatorIsSameAsAltered();

        if (alteredUserAcc.getTeamRole() == TeamRole.MEMBER) {
            assertIfExitingIsAllowedFromTeam();
            leaveTeamAsMember();
        } else if (alteredUserAcc.getTeamRole() == TeamRole.LEADER) {
            assertIfExitingIsAllowedFromTeam();
            leaveTeamAsLeader();
        } else {
            throw new TeamLifecycleException(TeamLifecycleApiError.OPERATION_IS_INVALID_FOR_TEAM_ROLE_OF_ALTERED, "The user isn't member or leader!");
        }

        log.debug("UserAcc {} left its team.", alteredUserAcc.getId());
    }

    private void leaveTeamAsMember() {
        alteredUserAcc.setTeamRole(TeamRole.NOTHING);
        alteredUserAcc.setTeam(null);
    }

    private void leaveTeamAsLeader() {
        if (userAccRepository.getCountOfEnabledLeadersInTeam(initiatorUserAcc.getTeam()) > 1) {
            //There is at least one other Leader in the team
            alteredUserAcc.setTeamRole(TeamRole.NOTHING);
            alteredUserAcc.setTeam(null);
        } else {
            throw new TeamLifecycleException(TeamLifecycleApiError.THERE_IS_NO_OTHER_LEADER, "There is no other leader in the team. If you want to leave, make someone else leader or archive the team!");
        }
    }

    public void kickFromTeam() {
        assertInitiatorIsDifferentThanAltered_and_initiatorIsLeaderOfTeamOfTheAltered();

        if (alteredUserAcc.getTeamRole().isMemberOrLeader()) {
            alteredUserAcc.setTeamRole(TeamRole.NOTHING);
            alteredUserAcc.setTeam(null);
        } else {
            throw new TeamLifecycleException(TeamLifecycleApiError.OPERATION_IS_INVALID_FOR_TEAM_ROLE_OF_ALTERED, "The user isn't member or leader of the team!");
        }

        log.debug("UserAcc {} was kicked from its team.", alteredUserAcc.getId());
    }

    public void archiveAndLeaveTeam() {
        assertInitiatorIsSameAsAltered();

        if (initiatorUserAcc.getTeamRole() != TeamRole.LEADER)
            throw new TeamLifecycleException(TeamLifecycleApiError.YOU_HAVE_TO_BE_A_LEADER_TO_DO_THIS_OPERATION, "You have to be a leader of the team you want to archive!");

        assertIfExitingIsAllowedFromTeam();

        final Team team = initiatorUserAcc.getTeam();
        team.setArchived(true);
        team.setName(team.getName() + "_archive_" + team.getId());//This is to break the non-uniqueness of names between archived and non-archived teams
        userAccRepository.kickEveryoneFromTeam(team);

        log.debug("UserAcc {} archived and left its team ({}).", alteredUserAcc.getId(), team.getId());
    }

    public void giveLeaderRights() {
        assertInitiatorIsDifferentThanAltered_and_initiatorIsLeaderOfTeamOfTheAltered();

        if (alteredUserAcc.getTeamRole() != TeamRole.MEMBER)
            throw new TeamLifecycleException(TeamLifecycleApiError.OPERATION_IS_INVALID_FOR_TEAM_ROLE_OF_ALTERED,
                    "The user have to be a member of the team to give him leader rights!");

        alteredUserAcc.setTeamRole(TeamRole.LEADER);
    }

    public void takeAwayLeaderRights() {
        assertInitiatorIsDifferentThanAltered_and_initiatorIsLeaderOfTeamOfTheAltered();

        if (alteredUserAcc.getTeamRole() != TeamRole.LEADER)
            throw new TeamLifecycleException(TeamLifecycleApiError.OPERATION_IS_INVALID_FOR_TEAM_ROLE_OF_ALTERED,
                    "The user is not a leader so you can't take away leader rights!");

        alteredUserAcc.setTeamRole(TeamRole.MEMBER);
    }

    public void resignFromLeadership() {
        assertInitiatorIsSameAsAltered();

        if (alteredUserAcc.getTeamRole() != TeamRole.LEADER)
            throw new TeamLifecycleException(TeamLifecycleApiError.OPERATION_IS_INVALID_FOR_TEAM_ROLE_OF_ALTERED, "You aren't a leader!");

        if (userAccRepository.getCountOfEnabledLeadersInTeam(alteredUserAcc.getTeam()) > 1) {
            //There is at least one other Leader in the team
            alteredUserAcc.setTeamRole(TeamRole.MEMBER);
        } else {
            throw new TeamLifecycleException(TeamLifecycleApiError.THERE_IS_NO_OTHER_LEADER,
                    "There is no other leader in the team. If you want to resign, give leader rights to someone else!");
        }
    }

    void assertIfExitingIsAllowedFromTeam() {
        if (!alteredUserAcc.getTeam().getAllowMembersToExit()) {
            throw new TeamLifecycleException(TeamLifecycleApiError.EXITING_FROM_TEAM_IS_NOT_ALLOWED, "Exiting from this team is not allowed!");
        }
    }

    void assertInitiatorIsSameAsAltered() {
        if (!initiatorUserAcc.getId().equals(alteredUserAcc.getId()))
            throw new TeamLifecycleException(TeamLifecycleApiError.INITIATOR_IS_DIFFERENT_THAN_ALTERED, "You can do this operation only for you own account!");
    }

    void assertInitiatorIsDifferentThanAltered_and_initiatorIsLeaderOfTeamOfTheAltered() {
        if (initiatorUserAcc.getId().equals(alteredUserAcc.getId()))
            throw new TeamLifecycleException(TeamLifecycleApiError.INITIATOR_IS_SAME_AS_ALTERED, "You can't do this operation for you own account!");

        if (alteredUserAcc.getTeam() == null) {
            throw new TeamLifecycleException(TeamLifecycleApiError.OPERATION_IS_INVALID_FOR_TEAM_ROLE_OF_ALTERED,
                    "The altered user is not in any team!");
        }
        if (initiatorUserAcc.getTeam() == null) {
            throw new TeamLifecycleException(TeamLifecycleApiError.INITIATOR_IS_NOT_LEADER_OF_TEAM_OF_ALTERED,
                    "You have to be a leader of team of the altered account to do this operation!");
        }

        if (!(alteredUserAcc.getTeam().getId().equals(initiatorUserAcc.getTeam().getId()) && initiatorUserAcc.getTeamRole() == TeamRole.LEADER))
            throw new TeamLifecycleException(TeamLifecycleApiError.INITIATOR_IS_NOT_LEADER_OF_TEAM_OF_ALTERED,
                    "You have to be a leader of team of the altered account to do this operation!");
    }

    public UserAcc getAlteredUserAcc() {
        return alteredUserAcc;
    }
}
