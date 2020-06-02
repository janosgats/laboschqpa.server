package com.laboschqpa.server.exceptions.apierrordescriptor;

import com.laboschqpa.server.enums.apierrordescriptor.TeamScoreApiError;

public class TeamScoreException extends ApiErrorDescriptorException {
    public TeamScoreException(TeamScoreApiError teamScoreApiError) {
        super(teamScoreApiError);
    }

    public TeamScoreException(TeamScoreApiError teamScoreApiError, String message) {
        super(teamScoreApiError, message);
    }
}
