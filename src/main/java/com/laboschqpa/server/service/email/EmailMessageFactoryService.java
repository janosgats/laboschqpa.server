package com.laboschqpa.server.service.email;

import com.laboschqpa.server.exceptions.EmailSendingException;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class EmailMessageFactoryService {
    final TemplateEngine templateEngine;

    public WithJavaMailSender withJavaMailSender(JavaMailSender javaMailSender) {
        return new WithJavaMailSender(javaMailSender);
    }

    public SimpleMailMessage createSimpleTestMessage() {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setSubject("Qpa web test message");
        message.setText("Test message from qpa web.\n"
                + "time: " + Instant.now().atZone(ZoneId.of("UTC")).toString() + "\n"
                + "test characters: aáeéiíoóöőuúüű");

        return message;
    }

    @RequiredArgsConstructor
    public class WithJavaMailSender {
        private static final String FROM_ADDRESS_PERSONAL = "Schönherz Qpa";
        private static final String FROM_ADDRESS_EMAIL = "schociqpa@gmail.com";
        private static final String REPLY_TO_ADDRESS_PERSONAL = "Lábosch";
        private static final String REPLY_TO_ADDRESS_EMAIL = "labosch@sch.bme.hu";

        private final JavaMailSender javaMailSender;

        public MimeMessageHelper createTestMessage(String testMessage) {
            final Context ctx = new Context();
            ctx.setVariable("testMessage", testMessage);
            ctx.setVariable("time", Instant.now().atZone(ZoneId.of("UTC")).toString());

            final MimeMessageHelper helper = createCommonMimeMessageWithHelper();
            setBodyAndSubjectFromThymeleafTemplate(helper, "mail/predefined/testEmail", ctx);
            return helper;
        }

        public MimeMessageHelper createRegistrationRequestMessage(String emailVerificationUrl) {
            final Context ctx = new Context();
            ctx.setVariable("emailVerificationUrl", emailVerificationUrl);

            final MimeMessageHelper helper = createCommonMimeMessageWithHelper();
            setBodyAndSubjectFromThymeleafTemplate(helper, "mail/predefined/registrationRequest", ctx);
            return helper;
        }

        private void setBodyAndSubjectFromThymeleafTemplate(final MimeMessageHelper mimeMessageHelper, final String template, final Context ctx) {
            try {
                final String subject = templateEngine.process(template, Set.of("mailSubject"), ctx);
                final String content = templateEngine.process(template, ctx);
                mimeMessageHelper.setSubject(subject);
                mimeMessageHelper.setText(content, true);
            } catch (MessagingException e) {
                throw new EmailSendingException("Error in setBodyAndSubjectFromThymeleafTemplate()", e);
            }
        }

        private MimeMessageHelper createCommonMimeMessageWithHelper() {
            final MimeMessage message = javaMailSender.createMimeMessage();
            final MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
            try {
                setCommonParams(helper);
                return helper;
            } catch (UnsupportedEncodingException | MessagingException e) {
                throw new EmailSendingException("Error in createCommonMimeMessageWithHelper()", e);
            }
        }

        private void setCommonParams(final MimeMessageHelper mimeMessageHelper) throws
                UnsupportedEncodingException, MessagingException {
            mimeMessageHelper.setFrom(new InternetAddress(FROM_ADDRESS_EMAIL, FROM_ADDRESS_PERSONAL, "UTF-8"));
            mimeMessageHelper.setReplyTo(new InternetAddress(REPLY_TO_ADDRESS_EMAIL, REPLY_TO_ADDRESS_PERSONAL, "UTF-8"));
        }
    }
}
