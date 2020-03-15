package com.laboschcst.server.statemachine;

import com.laboschcst.server.api.dto.TeamDto;
import com.laboschcst.server.entity.Team;
import com.laboschcst.server.entity.account.UserAcc;
import com.laboschcst.server.enums.TeamRole;
import com.laboschcst.server.exceptions.TeamUserRelationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TeamUserRelationTransitionsStateMachine {
    private static final Logger logger = LoggerFactory.getLogger(TeamUserRelationTransitionsStateMachine.class);
    private UserAcc alteredUserAcc;
    private UserAcc initiatorUserAcc;

    public TeamUserRelationTransitionsStateMachine(UserAcc alteredUserAcc, UserAcc initiatorUserAcc) {
        this.alteredUserAcc = alteredUserAcc;
        this.initiatorUserAcc = initiatorUserAcc;
    }

    public void createNewTeam(TeamDto teamDto) {
        assertInitiatorIsSameAsAltered();

        if (alteredUserAcc.getTeamRole() != TeamRole.NOTHING || alteredUserAcc.getTeam() != null)
            throw new TeamUserRelationException("You can create a new team only if you aren't a member or applicant of an other team!");

        Team newTeam = new Team();
        newTeam.setName(teamDto.getName());
        alteredUserAcc.setTeam(newTeam);
        alteredUserAcc.setTeamRole(TeamRole.LEADER);

        logger.debug("UserAcc {} created new team: {}", alteredUserAcc.getId(), teamDto.getName());
    }

    public void applyToTeam(Team team) {
        assertInitiatorIsSameAsAltered();

        if (alteredUserAcc.getTeamRole() != TeamRole.NOTHING || alteredUserAcc.getTeam() != null)
            throw new TeamUserRelationException("You can apply to a team only if you aren't a member or applicant of an other team!");

        alteredUserAcc.setTeam(team);
        alteredUserAcc.setTeamRole(TeamRole.APPLIED);

        logger.debug("UserAcc {} applied to Team {}.", alteredUserAcc.getId(), team.getId());
    }

    public void cancelApplicationToTeam() {
        assertInitiatorIsSameAsAltered();

        if (alteredUserAcc.getTeamRole() != TeamRole.APPLIED)
            throw new TeamUserRelationException("You can cancel only an applied UserAcc application!");

        alteredUserAcc.setTeam(null);
        alteredUserAcc.setTeamRole(TeamRole.NOTHING);


        logger.debug("Canceled application for UserAcc {}.", alteredUserAcc.getId());
    }

    public void declineApplicationToTeam() {
        assertInitiatorIsLeaderOfTeamOfTheAltered();

        if (alteredUserAcc.getTeamRole() != TeamRole.APPLIED)
            throw new TeamUserRelationException("You can decline only an applied UserAcc application!");

        alteredUserAcc.setTeam(null);
        alteredUserAcc.setTeamRole(TeamRole.NOTHING);

        logger.debug("Declined application for UserAcc {}.", alteredUserAcc.getId());
    }

    public void approveApplication() {
        assertInitiatorIsLeaderOfTeamOfTheAltered();

        if (alteredUserAcc.getTeamRole() == TeamRole.APPLIED || alteredUserAcc.getTeam() != null) {
            alteredUserAcc.setTeamRole(TeamRole.MEMBER);
        } else {
            throw new TeamUserRelationException("The user you try to accept the application of isn't an applicant!");
        }

        logger.debug("Approved application for UserAcc {}.", alteredUserAcc.getId());
    }

    private void assertInitiatorIsSameAsAltered() {
        if (!initiatorUserAcc.getId().equals(alteredUserAcc.getId()))
            throw new TeamUserRelationException("You can only do this operation for you own account!");
    }

    private void assertInitiatorIsLeaderOfTeamOfTheAltered() {
        if (!(alteredUserAcc.getTeam().getId().equals(initiatorUserAcc.getTeam().getId()) && initiatorUserAcc.getTeamRole() == TeamRole.LEADER))
            throw new TeamUserRelationException("You have to be a leader of team of the altered account to do this operation!");
    }

    public UserAcc getAlteredUserAcc() {
        return alteredUserAcc;
    }
}
