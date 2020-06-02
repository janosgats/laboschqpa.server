package com.laboschqpa.server.exceptions.apierrordescriptor;

import com.laboschqpa.server.enums.apierrordescriptor.ContentApiError;

public class ContentNotFoundException extends ApiErrorDescriptorException {
    public ContentNotFoundException(String message) {
        super(ContentApiError.CONTENT_IS_NOT_FOUND, message);
    }
}
