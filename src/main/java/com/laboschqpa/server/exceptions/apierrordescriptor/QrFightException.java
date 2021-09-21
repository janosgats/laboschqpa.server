package com.laboschqpa.server.exceptions.apierrordescriptor;

import com.laboschqpa.server.enums.apierrordescriptor.QrFightApiError;

public class QrFightException extends ApiErrorDescriptorException {
    public QrFightException(QrFightApiError qrFightApiError) {
        super(qrFightApiError);
    }

    public QrFightException(QrFightApiError qrFightApiError, String message) {
        super(qrFightApiError, message);
    }
}
