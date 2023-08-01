package nz.ac.canterbury.seng302.tab.mail;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import nz.ac.canterbury.seng302.tab.entity.User;

/**
 * Email Service class manages sending the emails.
 */
@Service
public class EmailService {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final SpringTemplateEngine templateEngine;

    private final JavaMailSender javaMailSender;

    private static final String sender = System.getenv().get("GMAIL_USERNAME");

    /**
     * Manual dependency injection for tests.
     * Following morgan's solution in origin/feat/morgan-fix.
     * NOTE: This ctor SHOULD NOT be called outside of tests!!!!
     * @param javaMailSender
     */
    @Autowired
    public EmailService(JavaMailSender javaMailSender, SpringTemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
        this.javaMailSender = javaMailSender;
    }

    /**
     *
     * @param emailDetails an entity of the details going into an email to be sent
     * Code adapted from stack overflow: <a href="https://stackoverflow.com/a/39359784">...</a>
     * @return A string describing the outcome of the send attempt
     */
    public String sendSimpleMail(EmailDetails emailDetails) {
        try {

            ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 10, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
            executor.execute(() -> {
                SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

                simpleMailMessage.setFrom(sender);
                simpleMailMessage.setTo(emailDetails.getRecipient());
                simpleMailMessage.setText(emailDetails.getMsgBody());
                simpleMailMessage.setSubject(emailDetails.getSubject());

                javaMailSender.send(simpleMailMessage);
            });
            return "Mail Sent Successfully";

        } catch (Exception e) {
            return e.getMessage();
        }
    }


    /**
     * Creates and sends email informing the user that their password has been updated.
     * @param user the user whose password was updated
     * @return the outcome of the email sending
     */
    public void updatePassword(User user) throws MessagingException {
        EmailDetails email = new EmailDetails(user.getEmail(), null,
            EmailDetails.UPDATE_PASSWORD_HEADER, "mail/updatePasswordConfirmationEmail.html");
        
        Map<String, Object> model = Map.of(
            "name", user.getFirstName()
        );
        email.setProperties(model);
        sendHtmlMessage(email);
    }

    public void confirmationEmail(User user, String url){
        EmailDetails details = new EmailDetails(user.getEmail(),url, EmailDetails.CONFIRMATION_EMAIL_HEADER );
        String outcome= this.sendSimpleMail(details);
        logger.info(outcome);
    }

    public void testHTMLEmail(User user) throws MessagingException {
        EmailDetails email = new EmailDetails(user.getEmail(), null, "Test", "mail/testEmail.html");
        Map<String, Object> properties = Map.of(
                "name", user.getFirstName(),
                "subscriptionDate", user.getDateOfBirth()
        );
        email.setProperties(properties);
        sendHtmlMessage(email);
    }

    public void sendHtmlMessage(EmailDetails email) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
        Context context = new Context();
        context.setVariables(email.getProperties());
        helper.setFrom("team900.tab@gmail.com");
        helper.setTo(email.getRecipient());
        helper.setSubject(email.getSubject());
        String html = templateEngine.process(email.getTemplate(), context);
        helper.setText(html, true);
        javaMailSender.send(message);
    }
}
