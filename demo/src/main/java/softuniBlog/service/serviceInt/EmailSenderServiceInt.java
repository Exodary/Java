package softuniBlog.service.serviceInt;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import softuniBlog.entity.ConfirmationToken;
import softuniBlog.entity.User;

public interface EmailSenderServiceInt {

    void sendEmail(SimpleMailMessage email);

    SimpleMailMessage createEmail(User user, ConfirmationToken confirmationToken);
}
