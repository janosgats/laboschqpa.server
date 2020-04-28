package com.laboschqpa.server.service;

public interface EmailSenderService {
    void sendTestMail(String toEmail, String testMessage);
    void sendRegistrationRequestMail(String toEmail, Long registrationRequestId, String registrationRequestKey);
}
