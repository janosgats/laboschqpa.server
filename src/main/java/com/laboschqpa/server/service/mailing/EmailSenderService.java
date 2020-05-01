package com.laboschqpa.server.service.mailing;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.util.function.Function;
import java.util.function.Supplier;

public interface EmailSenderService {
    void sendSync(Function<JavaMailSender, MimeMessageHelper> messageCreator);

    void sendAsync(Function<JavaMailSender, MimeMessageHelper> messageCreator);

    void sendSyncSimple(Supplier<SimpleMailMessage> messageCreator);

    void sendAsyncSimple(Supplier<SimpleMailMessage> messageCreator);
}
