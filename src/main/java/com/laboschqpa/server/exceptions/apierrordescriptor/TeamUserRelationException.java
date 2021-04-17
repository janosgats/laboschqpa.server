package com.laboschqpa.server.exceptions.apierrordescriptor;

import com.laboschqpa.server.enums.apierrordescriptor.TeamLifecycleApiError;

public class TeamUserRelationException extends ApiErrorDescriptorException {
    public TeamUserRelationException(TeamLifecycleApiError teamLifecycleApiError) {
        super(teamLifecycleApiError);
    }

    public TeamUserRelationException(TeamLifecycleApiError teamLifecycleApiError, String message) {
        super(teamLifecycleApiError, message);
    }
}
