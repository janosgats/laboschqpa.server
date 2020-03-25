package com.laboschqpa.server.exceptions;

import com.laboschqpa.server.enums.TeamUserRelationError;

public class TeamUserRelationException extends RuntimeException {
    private TeamUserRelationError teamUserRelationError = null;

    public TeamUserRelationException(TeamUserRelationError teamUserRelationError) {
        this.teamUserRelationError = teamUserRelationError;
    }

    public TeamUserRelationException(String message, TeamUserRelationError teamUserRelationError) {
        super(message);
        this.teamUserRelationError = teamUserRelationError;
    }

    public TeamUserRelationException(String message, Throwable cause) {
        super(message, cause);
    }

    public TeamUserRelationException(Throwable cause) {
        super(cause);
    }

    public TeamUserRelationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public TeamUserRelationError getTeamUserRelationError() {
        return teamUserRelationError;
    }
}
