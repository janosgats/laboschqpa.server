package com.laboschqpa.server.exceptions.statemachine;

import com.laboschqpa.server.enums.errorkey.TeamUserRelationApiError;
import lombok.Getter;

public class TeamUserRelationException extends RuntimeException {
    @Getter
    private TeamUserRelationApiError teamUserRelationApiError = null;

    public TeamUserRelationException(TeamUserRelationApiError teamUserRelationApiError) {
        this.teamUserRelationApiError = teamUserRelationApiError;
    }

    public TeamUserRelationException(String message, TeamUserRelationApiError teamUserRelationApiError) {
        super(message);
        this.teamUserRelationApiError = teamUserRelationApiError;
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
}
