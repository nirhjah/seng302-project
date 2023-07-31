package nz.ac.canterbury.seng302.tab.mail;

/**
 * Email details contains all the information going into an email to be sent
 */
public class EmailDetails {

    private String recipient;
    private String msgBody;
    private String subject;
    private String attachment;

    public static final String UPDATE_PASSWORD_BODY = "Your password has been updated.";

    public static final String UPDATE_PASSWORD_HEADER = "Password Updated";

    public static final String RESET_PASSWORD_HEADER = "Link to reset password";

    public static final String CONFIRMATION_EMAIL_HEADER = "Confirm your registration";

    public static final String JOIN_FEDERATION_MANAGER = "Become a Federation Manager";

    public EmailDetails(String recipient, String msgBody, String subject) {
        this.recipient = recipient;
        this.msgBody = msgBody;
        this.subject = subject;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getMsgBody() {
        return msgBody;
    }

    public String getSubject() {
        return subject;
    }

}
