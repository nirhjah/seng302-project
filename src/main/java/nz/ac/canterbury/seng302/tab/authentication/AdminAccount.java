package nz.ac.canterbury.seng302.tab.authentication;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;

import nz.ac.canterbury.seng302.tab.entity.Activity;
import nz.ac.canterbury.seng302.tab.entity.Fact.Goal;
import nz.ac.canterbury.seng302.tab.entity.Fact.Substitution;
import nz.ac.canterbury.seng302.tab.entity.Sport;
import nz.ac.canterbury.seng302.tab.enums.ActivityType;
import nz.ac.canterbury.seng302.tab.repository.ActivityRepository;
import org.springframework.beans.factory.annotation.Value;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.enums.AuthorityType;

/**
  Creates an admin user in the database. This class is automatically run on every startup.
 */
@Component
public class AdminAccount implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final ActivityRepository activityRepository;
    private String adminEmail;
    private String adminPassword;

    public AdminAccount(UserRepository userRepository,ActivityRepository activityRepository, PasswordEncoder passwordEncoder,
            @Value("${adminEmail:admin@gmail.com}") String adminEmail,
            @Value("${adminPassword:1}") String adminPassword) {
        this.userRepository = userRepository;
        this.activityRepository= activityRepository;
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
    private User createOrGetAdminAccount() throws Exception {
        // Don't create a duplicate admin
        Optional<User> currentAdmin = userRepository.findByEmail(adminEmail);
        if (currentAdmin.isPresent()) {
            return currentAdmin.get();
        }

        // Create admin
        Location location = new Location("adminAddr1", "adminAddr2", "adminSuburb", "adminCity", "4dm1n", "adminLand");
        User admin = new User("Admin", "Admin", new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime(),
                adminEmail, passwordEncoder.encode(adminPassword), location);

        Sport sport = new Sport("soccer");
        admin.setFavoriteSports(List.of(sport));
        // You need to confirm your email before you can log in.
        admin.confirmEmail();

        User sub = new User("Hee", "Account", "tab@gmail.com", "password",
                new Location("1 Place", "B", "Ilam", "CHCH", "808", "NZ"));
        User player = new User("Test", "Account", "tab.team900@gmail.com", "password",
                new Location("1 Place", "B", "Ilam", "CHCH", "808", "NZ"));
        Activity game = new Activity(ActivityType.Game, null, "A Test Game",
                LocalDateTime.of(2026, 1,1,6,30),
                LocalDateTime.of(2026, 1,1,8,30), admin,
                new Location("Jack Erskine", null, "Ilam", "Chch", "Test", "NZ"));
        game.addFactList(List.of(new Substitution("testing this", "1h 20m", game, player, sub), new Goal("Goal was scored", "1h 40m", game, player, 1)));
        activityRepository.save(game);

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
