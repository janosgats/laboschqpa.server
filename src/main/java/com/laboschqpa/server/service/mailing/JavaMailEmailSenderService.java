package com.laboschqpa.server.service.mailing;

import com.laboschqpa.server.exceptions.NotImplementedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.function.Function;
import java.util.function.Supplier;

@Log4j2
@RequiredArgsConstructor
@Service
public class JavaMailEmailSenderService implements EmailSenderService {
    private final JavaMailSender javaMailSender;

    @Override
    public void sendSync(Function<JavaMailSender, MimeMessageHelper> messageCreator) {
        javaMailSender.send(messageCreator.apply(javaMailSender).getMimeMessage());
    }

    @Override
    public void sendAsync(Function<JavaMailSender, MimeMessageHelper> messageCreator) {
        throw new NotImplementedException("sendAsync() is not implemented!");
    }

    @Override
    public void sendSyncSimple(Supplier<SimpleMailMessage> messageCreator) {
        javaMailSender.send(messageCreator.get());
    }

    @Override
    public void sendAsyncSimple(Supplier<SimpleMailMessage> messageCreator) {
        throw new NotImplementedException("sendAsyncSimple() is not implemented!");
    }
}
