package softuniBlog.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import softuniBlog.entity.ConfirmationToken;
import softuniBlog.entity.User;
import softuniBlog.service.serviceInt.EmailSenderServiceInt;

@Service("emailSenderService")
public class EmailSenderService implements EmailSenderServiceInt {

    private JavaMailSender javaMailSender;

    @Autowired
    public EmailSenderService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Async
    public void sendEmail(SimpleMailMessage email) {
        javaMailSender.send(email);
    }

    @Override
    public SimpleMailMessage createEmail(User user, ConfirmationToken confirmationToken) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject("Complete Password Reset!");
        mailMessage.setFrom("Satagon123@mail.bg");
        mailMessage.setText("To complete the password reset process, please click here: "
                +"http://localhost:8080/confirm-reset?token="+confirmationToken.getConfirmationToken());

        return mailMessage;
    }
}