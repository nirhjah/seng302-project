package nz.ac.canterbury.seng302.tab.entity;

import jakarta.persistence.*;
import nz.ac.canterbury.seng302.tab.repository.VerificationTokenRepository;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 * Verification token entity that is associated with a user account and can be used for email verification and password reset.
 */
@Entity
public class VerificationToken {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    private String token;

    @OneToOne(targetEntity = User.class, fetch= FetchType.EAGER)
    @JoinColumn(nullable=false, name="userId")
    private User user;

    private Date expiryDate;

    /**
     * Constructs a new VerificationToken object with the specified expiry date.
     *
     * @param expiryDate the expiry date of the verification token
     */
    public VerificationToken(Date expiryDate){
        this.expiryDate=expiryDate;
    }

    public VerificationToken() {
        
    }
    /**
     * Calculates the expiry date of the verification token based on the current time and the specified expiry time in hours.
     *
     * @param expiryTimeInHours the expiry time in hours
     * @return the expiry date of the verification token
     */
    private Date calculateExpiryDate(int expiryTimeInHours){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Timestamp(calendar.getTime().getTime()));
        calendar.add(Calendar.HOUR, expiryTimeInHours);
        return new Date(calendar.getTime().getTime());

    }
    /**
     * Generates a random string of characters to be used as a verification token.
     *
     * @return a randomly generated verification token
     */
    private static String generateToken(){
        final int TOKEN_SIZE = 12;
        return UUID.randomUUID().toString().replaceAll("\\-*", "").substring(0, TOKEN_SIZE);
    }

    /**
     * Generates a unique verification token.
     *
     * @param verificationTokenRepository the repository used to check if the token is already in use
     * @return a unique verification token
     */
    public String generateUniqueToken(VerificationTokenRepository verificationTokenRepository) {
        String token = generateToken();
        while (verificationTokenRepository.findByToken(token)==null) {
            token = generateToken();
        }
        return token;
    }


}
