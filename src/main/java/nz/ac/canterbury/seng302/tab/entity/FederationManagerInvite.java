package nz.ac.canterbury.seng302.tab.entity;

import jakarta.persistence.*;

import java.util.UUID;

/**
 * This structure stores the user info and token for each user that's invited to become a federation manager
 */
@Entity(name="FederationManagers")
public class FederationManagerInvite {

    public FederationManagerInvite(User user) {
        this.user = user;
        this.token = generateToken();
    }

    public FederationManagerInvite() {}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long fedManId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column
    private String token;

    /**
     * Creates a unique token for each invitation for each user
     * @return a unique string comprised of numbers and letters
     */
    private static String generateToken() {
        final int FED_MANAGER_TOKEN_SIZE = 12;
        return UUID.randomUUID().toString().replaceAll("\\-*", "").substring(0, FED_MANAGER_TOKEN_SIZE);
    }

    public String getToken() {return token;}

    public User getUser() {return user;}
}
