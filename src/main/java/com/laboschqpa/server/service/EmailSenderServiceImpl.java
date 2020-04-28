package com.laboschqpa.server.service;

import com.laboschqpa.server.exceptions.EmailSendingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
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

    public void sendTestMail(String toEmail, String testMessage) {
        log.info("Sending test e-mail to: {}", toEmail);
        final MimeMessageHelper helper = emailMessageFactoryService.withJavaMailSender(javaMailSender).createTestMessage(testMessage);
        try {
            helper.setTo(toEmail);
        } catch (MessagingException e) {
            throw new EmailSendingException("Error while setting recipient", e);
        }
        javaMailSender.send(helper.getMimeMessage());
    }

    @Override
    public void sendRegistrationRequestMail(String toEmail, Long registrationRequestId, String registrationRequestKey) {
        log.error("REMOVE THIS INSECURE LOG ASAP! email should be sent: toEmail:{} registrationRequestId:{} registrationRequestKey:{}", toEmail, registrationRequestId, registrationRequestKey);
    }
}
