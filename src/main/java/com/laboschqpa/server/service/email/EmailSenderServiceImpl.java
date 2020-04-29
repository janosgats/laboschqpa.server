package com.laboschqpa.server.service.email;

import com.laboschqpa.server.exceptions.EmailSendingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;

@Log4j2
@RequiredArgsConstructor
@Service
public class EmailSenderServiceImpl implements EmailSenderService {

    private final EmailMessageFactoryService emailMessageFactoryService;
    private final JavaMailSender javaMailSender;

    @Value("${email.links.baseUrl}")
    private String emailLinksBaseUrl;

    public void sendTestMail(String toEmail, String testMessage) {
        log.info("Sending test e-mail to: {}", toEmail);
        final MimeMessageHelper helper = emailMessageFactoryService.withJavaMailSender(javaMailSender).createTestMessage(testMessage);
        setRecipient(helper, toEmail);
        javaMailSender.send(helper.getMimeMessage());
    }

    @Override
    public void sendRegistrationRequestMail(String toEmail, Long registrationRequestId, String registrationRequestKey) {
        String emailVerificationUrl = String.format("%s/verifyEmail?id=%s&key=%s", emailLinksBaseUrl, registrationRequestId, registrationRequestKey);

        final MimeMessageHelper helper = emailMessageFactoryService.withJavaMailSender(javaMailSender).createRegistrationRequestMessage(emailVerificationUrl);
        setRecipient(helper, toEmail);
        javaMailSender.send(helper.getMimeMessage());
    }

    private void setRecipient(MimeMessageHelper helper, String toEmail) {
        try {
            helper.setTo(toEmail);
        } catch (MessagingException e) {
            throw new EmailSendingException("Error while setting recipient", e);
        }
    }
}
