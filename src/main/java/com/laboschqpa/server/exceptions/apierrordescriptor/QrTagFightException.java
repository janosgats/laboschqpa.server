package com.laboschqpa.server.exceptions.apierrordescriptor;

import com.laboschqpa.server.enums.apierrordescriptor.QrTagFightApiError;

public class QrTagFightException extends ApiErrorDescriptorException {
    public QrTagFightException(QrTagFightApiError qrTagFightApiError) {
        super(qrTagFightApiError);
    }

    public QrTagFightException(QrTagFightApiError qrTagFightApiError, String message) {
        super(qrTagFightApiError, message);
    }
}
