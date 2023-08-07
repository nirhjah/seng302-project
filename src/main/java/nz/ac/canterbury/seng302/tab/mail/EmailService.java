package nz.ac.canterbury.seng302.tab.mail;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.tab.entity.User;

/**
 * Email Service class manages sending the emails.
 */
@Service
public class EmailService {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final SpringTemplateEngine templateEngine;

    private final JavaMailSender javaMailSender;

    /**
     * Manual dependency injection for tests.
     * Following morgan's solution in origin/feat/morgan-fix.
     * NOTE: This ctor SHOULD NOT be called outside of tests!!!!
     * @param javaMailSender this sends the emails
     */
    @Autowired
    public EmailService(JavaMailSender javaMailSender, SpringTemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
        this.javaMailSender = javaMailSender;
    }

    /**
     * Returns the string of the current base URL
     * @param request
     * @return
     */
    private String getBaseUrl(HttpServletRequest request) {
        // We should probably have a global BASE_URL variables
        if (request.getRequestURL().toString().contains("test")) {
            return "https://csse-s302g9.canterbury.ac.nz/test";
        } else if (request.getRequestURL().toString().contains("prod")) {
            return "https://csse-s302g9.canterbury.ac.nz/prod";
        } else {
            return request.getRequestURL().toString().replace(request.getServletPath(), "");
        }
    }

    /**
     * Creates and sends email informing the user that their password has been updated.
     * @param user the user whose password was updated
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

    /**
     * Creates a reset password link with unique token for the user and sends it to their email
     * @param user      user to send reset password link to
     * @param request   to get current url to create the link
     */
    public void resetPasswordEmail(User user, HttpServletRequest request) throws MessagingException {

        String tokenVerificationLink = getBaseUrl(request) + "/reset-password?token=" + user.getToken();
        EmailDetails email = new EmailDetails(user.getEmail(), null,
            EmailDetails.RESET_PASSWORD_HEADER, "mail/resetPasswordEmail.html");
        
        Map<String, Object> model = Map.of(
            "name", user.getFirstName(),
            "linkUrl", tokenVerificationLink
        );
        email.setProperties(model);

        sendHtmlMessage(email);
    }

    /**
     * This method creates the confirm account email, and calls the method to send it.
     * @param user the receiver of the email
     * @param request the HTTPRequest, so the correct link will be sent
     */
    public void confirmationEmail(User user, HttpServletRequest request) throws MessagingException {
        String tokenVerificationLink = getBaseUrl(request) + "/confirm?token=" + user.getToken();
        EmailDetails email = new EmailDetails(user.getEmail(), null,
                EmailDetails.CONFIRMATION_EMAIL_HEADER, "mail/confirmAccount.html");

        Map<String, Object> model = Map.of(
                "name", user.getFirstName(),
                "linkUrl", tokenVerificationLink
        );
        email.setProperties(model);
        
        sendHtmlMessage(email);
    }

    /**
     * This method creates and calls the method to send the invitation to become a federation manager
     * @param user the receiver of the email
     * @param request the HTTPRequest, so the correct link will be sent
     */
    public void federationManagerInvite(User user, HttpServletRequest request) throws MessagingException {
        String tokenVerificationLink = getBaseUrl(request) + "/federationManager?token=" + user.getToken();
        EmailDetails email = new EmailDetails(user.getEmail(), null,
                EmailDetails.FEDERATION_MANAGER_INVITE, "mail/federationManagerInvite.html");

        Map<String, Object> model = Map.of(
                "name", user.getFirstName(),
                "linkUrl", tokenVerificationLink
        );
        email.setProperties(model);
        
        sendHtmlMessage(email);
    }

    /**
     * This method takes the email details and puts it together and sends it via threading
     * @param email the email details
     * @throws MessagingException - error from
     */
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
        try {
            ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 10, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
            executor.execute(() -> javaMailSender.send(message));
        } catch (Exception e) {
            logger.error("MAIL NOT SENT");
        }
    }
}
