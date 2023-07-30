package nz.ac.canterbury.seng302.tab.entity;

import jakarta.persistence.*;

import java.util.UUID;

/**
 * This structure stores the user info and token for each user that's invited to become a federation manager
 */
@Entity(name="FederationManagers")
public class FederationManagerGenerator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long fedManId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column
    private String token;

    private static String generateToken() {
        final int FED_MANAGER_TOKEN_SIZE = 12;
        return UUID.randomUUID().toString().replaceAll("\\-*", "").substring(0, FED_MANAGER_TOKEN_SIZE);
    }
}
