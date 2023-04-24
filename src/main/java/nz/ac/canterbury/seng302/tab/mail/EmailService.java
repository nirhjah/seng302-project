package nz.ac.canterbury.seng302.tab.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Email Service class manages sending the emails.
 */
@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    /**
     * Gets the sending email from the application properties
     */
    @Value("${spring.mail.username}") private String sender;

    /**
     *
     * @param emailDetails an entity of the details going into an email to be sent
     * @return A string describing the outcome of the send attempt
     */
    public String sendSimpleMail(EmailDetails emailDetails) {
        try {
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

            simpleMailMessage.setFrom(sender);
            simpleMailMessage.setTo(emailDetails.getRecipient());
            simpleMailMessage.setText(emailDetails.getMsgBody());
            simpleMailMessage.setSubject(emailDetails.getSubject());

            javaMailSender.send(simpleMailMessage);
            return "Mail Sent Successfully";
        } catch (Exception e) {
            return e.getMessage();
        }
    };
}
