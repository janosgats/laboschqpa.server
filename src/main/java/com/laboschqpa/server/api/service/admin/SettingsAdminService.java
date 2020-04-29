package com.laboschqpa.server.api.service.admin;

import com.laboschqpa.server.service.email.EmailSenderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SettingsAdminService {
    private final EmailSenderService emailSenderService;

    public void sendTestEmail(String toEmail, String testMessage) {
        emailSenderService.sendTestMail(toEmail, testMessage);
    }
}
