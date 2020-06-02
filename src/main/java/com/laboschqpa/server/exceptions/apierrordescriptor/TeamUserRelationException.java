package com.laboschqpa.server.exceptions.apierrordescriptor;

import com.laboschqpa.server.enums.apierrordescriptor.TeamUserRelationApiError;

public class TeamUserRelationException extends ApiErrorDescriptorException {
    public TeamUserRelationException(TeamUserRelationApiError teamUserRelationApiError) {
        super(teamUserRelationApiError);
    }

    public TeamUserRelationException(TeamUserRelationApiError teamUserRelationApiError, String message) {
        super(teamUserRelationApiError, message);
    }
}
