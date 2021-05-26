package com.laboschqpa.server.service.mailing;

import com.laboschqpa.server.exceptions.EmailSendingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;

@Slf4j
@RequiredArgsConstructor
@Service
public class QpaEmailDispatcher {
    private static final String EMAIL_VERIFICATION_FRONTEND_PATH = "/emailVerification/verifyEmail";

    private final EmailSenderService emailSenderService;
    private final EmailMessageFactory emailMessageFactory;

    @Value("${email.links.baseUrl}")
    private String emailLinksBaseUrl;

    public void sendSyncTestMail(String toEmail, String testMessage) {
        log.info("Sending (dispatching) test e-mail to: {}. EmailSenderService: {}", toEmail, emailSenderService.getClass().getName());
        emailSenderService.sendSync(javaMailSender -> {
            final MimeMessageHelper helper = emailMessageFactory.withJavaMailSender(javaMailSender).createTestMessage(testMessage);
            setRecipient(helper, toEmail);
            return helper;
        });
    }

    public void sendSyncEmailVerificationRequestMail(String toEmail, Long verificationRequestId, String verificationKey) {
        emailSenderService.sendSync(javaMailSender -> {
            String emailVerificationUrl = String.format("%s%s?id=%s&key=%s", emailLinksBaseUrl, EMAIL_VERIFICATION_FRONTEND_PATH, verificationRequestId, verificationKey);
            final MimeMessageHelper helper = emailMessageFactory.withJavaMailSender(javaMailSender).createEmailVerificationRequestMessage(emailVerificationUrl);
            setRecipient(helper, toEmail);
            return helper;
        });
    }

    private void setRecipient(MimeMessageHelper helper, String toEmail) {
        try {
            helper.setTo(toEmail);
        } catch (MessagingException e) {
            throw new EmailSendingException("Error while setting recipient", e);
        }
    }
}
