package com.laboschqpa.server.api.service.admin;

import com.laboschqpa.server.service.mailing.QpaEmailDispatcher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SettingsAdminService {
    private final QpaEmailDispatcher qpaEmailDispatcher;

    public void sendTestEmail(String toEmail, String testMessage) {
        qpaEmailDispatcher.sendSyncTestMail(toEmail, testMessage);
    }
}
