package com.laboschqpa.server.exceptions.apierrordescriptor;

import com.laboschqpa.server.enums.apierrordescriptor.TeamLifecycleApiError;

public class TeamLifecycleException extends ApiErrorDescriptorException {
    public TeamLifecycleException(TeamLifecycleApiError teamLifecycleApiError) {
        super(teamLifecycleApiError);
    }

    public TeamLifecycleException(TeamLifecycleApiError teamLifecycleApiError, String message) {
        super(teamLifecycleApiError, message);
    }
}
