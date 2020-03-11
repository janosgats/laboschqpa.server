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
    private UserAcc userAcc;

    public TeamUserRelationTransitionsStateMachine(UserAcc userAcc) {
        this.userAcc = userAcc;
    }

    public void createNewTeam(TeamDto teamDto) {
        if (userAcc.getTeamRole() != TeamRole.NOTHING)
            throw new TeamUserRelationException("You can create a new team only if you aren't a member or applicant of an other team!");

        Team newTeam = new Team();
        newTeam.setName(teamDto.getName());
        userAcc.setTeam(newTeam);
        userAcc.setTeamRole(TeamRole.LEADER);

        logger.debug("UserAcc {} created new team: {}", userAcc.getId(), teamDto.getName());
    }

    public void applyToTeam(Team team) {
        if (userAcc.getTeamRole() != TeamRole.NOTHING)
            throw new TeamUserRelationException("You can apply to a team only if you aren't a member or applicant of an other team!");

        userAcc.setTeam(team);
        userAcc.setTeamRole(TeamRole.APPLIED);

        logger.debug("UserAcc {} applied to Team {}.", userAcc.getId(), team.getId());
    }

    public void cancelApplicationToTeam() {
        if (userAcc.getTeamRole() != TeamRole.APPLIED)
            throw new TeamUserRelationException("You can cancel only an applied UserAcc application!");

        userAcc.setTeam(null);
        userAcc.setTeamRole(TeamRole.NOTHING);
        logger.debug("Canceled application for UserAcc {}.", userAcc.getId());
    }

    public UserAcc getUserAcc() {
        return userAcc;
    }
}
