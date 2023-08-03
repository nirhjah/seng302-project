package nz.ac.canterbury.seng302.tab.authentication;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.enums.AuthorityType;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;

@Component
public class AdminAccount implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private String adminEmail;
    private String adminPassword;

    public AdminAccount(UserRepository userRepository, PasswordEncoder passwordEncoder,
            @Value("${adminEmail:admin@gmail.com}") String adminEmail,
            @Value("${adminPassword:1}") String adminPassword) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
    }

    /**
     * <p>
     *  Give the current user a role if they don't already have it.
     * </p>
     * 
     * This exists so we can add roles easily to the production admin (Currently federation manager).
     * @param admin The user we're giving the role to
     * @param role The role we're assigning
     */
    private void giveRoleIfNotPresent(User admin, AuthorityType role) {
        // Don't add duplicate roles
        for (GrantedAuthority existingRole : admin.getAuthorities()) {
            if (existingRole.getAuthority().equals(role.role())) {
                return;
            }
        }

        admin.grantAuthority(role);
    }

    /**
     * Creates an admin account if one doesn't exist, or returns the existing one.
     */
    private User createOrGetAdminAccount() throws IOException {
        // Don't create a duplicate admin
        Optional<User> currentAdmin = userRepository.findByEmail(adminEmail);
        if (currentAdmin.isPresent()) {
            return currentAdmin.get();
        }

        // Create admin
        Location location = new Location("adminAddr1", "adminAddr2", "adminSuburb", "adminCity", "4dm1n", "adminLand");
        User admin = new User("Admin", "Admin", new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime(),
                adminEmail, passwordEncoder.encode(adminPassword), location);
        
        // You need to confirm your email before you can log in.
        admin.confirmEmail();
        
        return admin;
    }

    @Override
    public void run(String... args) throws Exception {
        // Create the admin if it doesn't exist
        // (Prevents duplicate admins on every reboot)
        User admin = createOrGetAdminAccount();
        // Give role if they don't exist
        // (Same reason as above, don't want duplicate roles)
        giveRoleIfNotPresent(admin, AuthorityType.ADMIN);
        giveRoleIfNotPresent(admin, AuthorityType.FEDERATION_MANAGER);

        // If anyone could find a way to prevent this class from being
        // run during tests that'd be great.
        userRepository.save(admin);
    }
}
