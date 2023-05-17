package nz.ac.canterbury.seng302.tab.mail;

import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private JavaMailSender javaMailSender;

    /**
     * Gets the sending email from the application properties
     */
    private static final String sender = System.getenv().get("GMAIL_USERNAME");

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
}
