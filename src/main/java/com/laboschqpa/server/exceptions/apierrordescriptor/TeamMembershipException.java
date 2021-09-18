package com.laboschqpa.server.exceptions.apierrordescriptor;

import com.laboschqpa.server.enums.apierrordescriptor.TeamMembershipApiError;

public class TeamMembershipException extends ApiErrorDescriptorException {
    public TeamMembershipException(TeamMembershipApiError teamMembershipApiError) {
        super(teamMembershipApiError);
    }

    public TeamMembershipException(TeamMembershipApiError teamMembershipApiError, String message) {
        super(teamMembershipApiError, message);
    }
}
