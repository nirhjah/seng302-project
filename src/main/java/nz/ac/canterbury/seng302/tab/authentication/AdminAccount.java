package nz.ac.canterbury.seng302.tab.authentication;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
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

    @Override
    public void run(String... args) throws Exception {
        // Don't run if the user already exists
        if (userRepository.findByEmail(adminEmail).isPresent()) {
            return;
        }

        Location location = new Location("adminAddr1", "adminAddr2", "adminSuburb", "adminCity", "admin", "admin");

        User admin = new User("Admin", "Admin", new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime(),
                adminEmail, passwordEncoder.encode(adminPassword), location);
        admin.grantAuthority(AuthorityType.ADMIN);
        admin.grantAuthority(AuthorityType.FEDERATION_MANAGER);
        admin.confirmEmail();
        
        userRepository.save(admin);

    }
}
