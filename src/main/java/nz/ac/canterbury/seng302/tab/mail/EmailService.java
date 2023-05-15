package nz.ac.canterbury.seng302.tab.mail;

import nz.ac.canterbury.seng302.tab.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Email Service class manages sending the emails.
 */
@Service
public class EmailService {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private JavaMailSender javaMailSender;

    /**
     * Manual dependency injection for tests.
     * Following morgan's solution in origin/feat/morgan-fix.
     * NOTE: This ctor SHOULD NOT be called outside of tests!!!!
     * @param javaMailSender
     */
    @Autowired
    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    /**
     * Gets the sending email from the application properties
     */
    @Value("${spring.mail.username}") private String sender;

    /**
     *
     * @param emailDetails an entity of the details going into an email to be sent
     * Code adapted from stack overflow: <a href="https://stackoverflow.com/a/39359784">...</a>
     * @return A string describing the outcome of the send attempt
     */
    public String sendSimpleMail(EmailDetails emailDetails) {
        try {

            ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 10, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

                    simpleMailMessage.setFrom(sender);
                    simpleMailMessage.setTo(emailDetails.getRecipient());
                    simpleMailMessage.setText(emailDetails.getMsgBody());
                    simpleMailMessage.setSubject(emailDetails.getSubject());

                    javaMailSender.send(simpleMailMessage);
                }
            });
            return "Mail Sent Successfully";

        } catch (Exception e) {
            return e.getMessage();
        }
    }


    /**
     * Creates and sends email informing the user that their password has been updated.
     * TODO add the update functionality to this method as well.
     * @param user the user whose password was updated
     * @return the outcome of the email sending
     */
    public void updatePassword(User user) {
        EmailDetails details = new EmailDetails(user.getEmail(), EmailDetails.UPDATE_PASSWORD_BODY, EmailDetails.UPDATE_PASSWORD_HEADER);
        String outcome = this.sendSimpleMail(details);
        logger.info(outcome);
    }

    public void confirmationEmail(User user, String url){
        EmailDetails details = new EmailDetails(user.getEmail(),url, EmailDetails.CONFIRMATION_EMAIL_HEADER );
        String outcome= this.sendSimpleMail(details);
        logger.info(outcome);
    }
}
