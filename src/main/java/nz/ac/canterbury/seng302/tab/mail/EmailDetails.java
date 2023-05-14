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

    public EmailDetails(String recipient, String msgBody, String subject, String attachment) {
        this.recipient = recipient;
        this.msgBody = msgBody;
        this.subject = subject;
        this.attachment = attachment;
    }

    public EmailDetails(String recipient, String msgBody, String subject) {
        this.recipient = recipient;
        this.msgBody = msgBody;
        this.subject = subject;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getMsgBody() {
        return msgBody;
    }

    public void setMsgBody(String msgBody) {
        this.msgBody = msgBody;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }
}
