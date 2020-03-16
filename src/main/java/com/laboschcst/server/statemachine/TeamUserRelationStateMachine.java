package com.laboschcst.server.statemachine;

import com.laboschcst.server.api.dto.TeamDto;
import com.laboschcst.server.entity.Team;
import com.laboschcst.server.entity.account.UserAcc;
import com.laboschcst.server.enums.TeamRole;
import com.laboschcst.server.enums.TeamUserRelationError;
import com.laboschcst.server.exceptions.TeamUserRelationException;
import com.laboschcst.server.repo.Repos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TeamUserRelationStateMachine {
    private static final Logger logger = LoggerFactory.getLogger(TeamUserRelationStateMachine.class);
    private final UserAcc alteredUserAcc;
    private final UserAcc initiatorUserAcc;
    private final Repos repos;

    public TeamUserRelationStateMachine(UserAcc alteredUserAcc, UserAcc initiatorUserAcc, Repos repos) {
        this.alteredUserAcc = alteredUserAcc;
        this.initiatorUserAcc = initiatorUserAcc;
        this.repos = repos;
    }

    public void createNewTeam(TeamDto teamDto) {
        assertInitiatorIsSameAsAltered();

        if (alteredUserAcc.getTeamRole() != TeamRole.NOTHING || alteredUserAcc.getTeam() != null)
            throw new TeamUserRelationException("You can create a new team only if you aren't a member or applicant of an other team!", TeamUserRelationError.YOU_ARE_ALREADY_MEMBER_OF_A_TEAM);

        Team newTeam = new Team();
        newTeam.setName(teamDto.getName());
        alteredUserAcc.setTeam(newTeam);
        alteredUserAcc.setTeamRole(TeamRole.LEADER);

        logger.debug("UserAcc {} created new team: {}", alteredUserAcc.getId(), teamDto.getName());
    }

    public void applyToTeam(Team team) {
        assertInitiatorIsSameAsAltered();

        if (alteredUserAcc.getTeamRole() != TeamRole.NOTHING || alteredUserAcc.getTeam() != null)
            throw new TeamUserRelationException("You can apply to a team only if you aren't a member or applicant of an other team!", TeamUserRelationError.YOU_ARE_ALREADY_MEMBER_OF_A_TEAM);

        alteredUserAcc.setTeam(team);
        alteredUserAcc.setTeamRole(TeamRole.APPLIED);

        logger.debug("UserAcc {} applied to Team {}.", alteredUserAcc.getId(), team.getId());
    }

    public void cancelApplicationToTeam() {
        assertInitiatorIsSameAsAltered();

        if (alteredUserAcc.getTeamRole() != TeamRole.APPLIED)
            throw new TeamUserRelationException("You can cancel only an applied UserAcc application!", TeamUserRelationError.OPERATION_IS_INVALID_FOR_TEAM_ROLE_OF_ALTERED);

        alteredUserAcc.setTeam(null);
        alteredUserAcc.setTeamRole(TeamRole.NOTHING);


        logger.debug("Canceled application for UserAcc {}.", alteredUserAcc.getId());
    }

    public void declineApplicationToTeam() {
        assertInitiatorIsDifferentThanAltered_and_initiatorIsLeaderOfTeamOfTheAltered();

        if (alteredUserAcc.getTeamRole() != TeamRole.APPLIED)
            throw new TeamUserRelationException("You can decline only an applied UserAcc application!", TeamUserRelationError.OPERATION_IS_INVALID_FOR_TEAM_ROLE_OF_ALTERED);

        alteredUserAcc.setTeam(null);
        alteredUserAcc.setTeamRole(TeamRole.NOTHING);

        logger.debug("Declined application for UserAcc {}.", alteredUserAcc.getId());
    }

    public void approveApplication() {
        assertInitiatorIsDifferentThanAltered_and_initiatorIsLeaderOfTeamOfTheAltered();

        if (alteredUserAcc.getTeamRole() == TeamRole.APPLIED && alteredUserAcc.getTeam() != null) {
            alteredUserAcc.setTeamRole(TeamRole.MEMBER);
        } else {
            throw new TeamUserRelationException("The user you try to accept the application of isn't an applicant!", TeamUserRelationError.OPERATION_IS_INVALID_FOR_TEAM_ROLE_OF_ALTERED);
        }

        logger.debug("Approved application for UserAcc {}.", alteredUserAcc.getId());
    }

    public void leaveTeam() {
        assertInitiatorIsSameAsAltered();

        if (alteredUserAcc.getTeamRole() == TeamRole.MEMBER) {
            leaveTeamAsMember();
        } else if (alteredUserAcc.getTeamRole() == TeamRole.LEADER) {
            leaveTeamAsLeader();
        } else {
            throw new TeamUserRelationException("The user isn't member or leader!", TeamUserRelationError.OPERATION_IS_INVALID_FOR_TEAM_ROLE_OF_ALTERED);
        }

        logger.debug("UserAcc {} left its team.", alteredUserAcc.getId());
    }

    private void leaveTeamAsMember() {
        alteredUserAcc.setTeamRole(TeamRole.NOTHING);
        alteredUserAcc.setTeam(null);
    }

    private void leaveTeamAsLeader() {
        if (repos.userAccRepository.getCountOfEnabledLeadersInTeam(initiatorUserAcc.getTeam()) > 1) {
            //There is at least one other Leader in the team
            alteredUserAcc.setTeamRole(TeamRole.NOTHING);
            alteredUserAcc.setTeam(null);
        } else {
            throw new TeamUserRelationException("There is no other leader in the team. If you want to leave, make someone else leader or archive the team!", TeamUserRelationError.THERE_IS_NO_OTHER_LEADER);
        }
    }

    public void kickFromTeam() {
        assertInitiatorIsDifferentThanAltered_and_initiatorIsLeaderOfTeamOfTheAltered();

        if (alteredUserAcc.getTeamRole() == TeamRole.MEMBER || alteredUserAcc.getTeamRole() == TeamRole.LEADER) {
            alteredUserAcc.setTeamRole(TeamRole.NOTHING);
            alteredUserAcc.setTeam(null);
        } else {
            throw new TeamUserRelationException("The user isn't member or leader of the team!", TeamUserRelationError.OPERATION_IS_INVALID_FOR_TEAM_ROLE_OF_ALTERED);
        }

        logger.debug("UserAcc {} was kicked from its team.", alteredUserAcc.getId());
    }

    public void archiveAndLeaveTeam() {
        assertInitiatorIsSameAsAltered();

        if (initiatorUserAcc.getTeamRole() != TeamRole.LEADER)
            throw new TeamUserRelationException("You have to be a leader of the team you want to archive!", TeamUserRelationError.YOU_HAVE_TO_BE_A_LEADER_TO_DO_THIS_OPERATION);

        initiatorUserAcc.getTeam().setArchived(true);
        repos.userAccRepository.kickEveryoneFromTeam(initiatorUserAcc.getTeam());

        logger.debug("UserAcc {} left its team.", alteredUserAcc.getId());
    }

    public void giveLeaderRights() {
        assertInitiatorIsDifferentThanAltered_and_initiatorIsLeaderOfTeamOfTheAltered();

        if (alteredUserAcc.getTeamRole() != TeamRole.MEMBER)
            throw new TeamUserRelationException("The user have to be a member of the team to give him leader rights!", TeamUserRelationError.OPERATION_IS_INVALID_FOR_TEAM_ROLE_OF_ALTERED);

        alteredUserAcc.setTeamRole(TeamRole.LEADER);
    }

    public void takeAwayLeaderRights() {
        assertInitiatorIsDifferentThanAltered_and_initiatorIsLeaderOfTeamOfTheAltered();

        if (alteredUserAcc.getTeamRole() != TeamRole.LEADER)
            throw new TeamUserRelationException("The user is not a leader so you can't take away leader rights!", TeamUserRelationError.OPERATION_IS_INVALID_FOR_TEAM_ROLE_OF_ALTERED);

        alteredUserAcc.setTeamRole(TeamRole.MEMBER);
    }

    public void resignFromLeadership() {
        assertInitiatorIsSameAsAltered();

        if (alteredUserAcc.getTeamRole() != TeamRole.LEADER)
            throw new TeamUserRelationException("You aren't a leader!", TeamUserRelationError.OPERATION_IS_INVALID_FOR_TEAM_ROLE_OF_ALTERED);

        if (repos.userAccRepository.getCountOfEnabledLeadersInTeam(alteredUserAcc.getTeam()) > 1) {
            //There is at least one other Leader in the team
            alteredUserAcc.setTeamRole(TeamRole.MEMBER);
        } else {
            throw new TeamUserRelationException("There is no other leader in the team. If you want to resign, make someone else leader!", TeamUserRelationError.THERE_IS_NO_OTHER_LEADER);
        }
    }

    public void assertInitiatorIsSameAsAltered() {
        if (!initiatorUserAcc.getId().equals(alteredUserAcc.getId()))
            throw new TeamUserRelationException("You can only do this operation for you own account!", TeamUserRelationError.INITIATOR_IS_DIFFERENT_THAN_ALTERED);
    }

    public void assertInitiatorIsDifferentThanAltered_and_initiatorIsLeaderOfTeamOfTheAltered() {
        if (initiatorUserAcc.getId().equals(alteredUserAcc.getId()))
            throw new TeamUserRelationException("You can't do this operation for you own account!", TeamUserRelationError.INITIATOR_IS_SAME_AS_ALTERED);

        if (!(alteredUserAcc.getTeam().getId().equals(initiatorUserAcc.getTeam().getId()) && initiatorUserAcc.getTeamRole() == TeamRole.LEADER))
            throw new TeamUserRelationException("You have to be a leader of team of the altered account to do this operation!", TeamUserRelationError.INITIATOR_IS_NOT_LEADER_OF_TEAM_OF_ALTERED);
    }

    public UserAcc getAlteredUserAcc() {
        return alteredUserAcc;
    }
}
