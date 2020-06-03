package com.laboschqpa.server.exceptions.apierrordescriptor;

import com.laboschqpa.server.enums.apierrordescriptor.AuthorityAdminApiError;

public class AuthorityAdminException extends ApiErrorDescriptorException {
    public AuthorityAdminException(AuthorityAdminApiError authorityAdminApiError) {
        super(authorityAdminApiError);
    }

    public AuthorityAdminException(AuthorityAdminApiError authorityAdminApiError, String message) {
        this(authorityAdminApiError, message, null);
    }

    public AuthorityAdminException(AuthorityAdminApiError authorityAdminApiError, String message, Object payload) {
        super(authorityAdminApiError, message, payload);
    }
}
