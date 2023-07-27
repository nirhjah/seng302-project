package nz.ac.canterbury.seng302.tab.authentication;

import nz.ac.canterbury.seng302.tab.entity.*;
import nz.ac.canterbury.seng302.tab.repository.FormationRepository;
import nz.ac.canterbury.seng302.tab.repository.TeamRepository;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.Calendar;
import java.util.GregorianCalendar;

@Component
public class AdminAccount implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
//
//    @Autowired
//    private TeamRepository teamRepository;
//
//    @Autowired
//    private FormationRepository formationRepository;


    // TODO: This SHOULD NOT be hard coded in. Either remove this account, or make it an env variable.
    private static final String ADMIN_PW = "1";

    private static final String EMAIL = "admin@gmail.com";

    @Autowired
    public AdminAccount(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Don't run if the user already exists
        if (userRepository.findByEmail(EMAIL).isPresent()) {
            return;
        }

        Location location = new Location("admin", "admin", "admin", "admin", "admin", "admin");

        User admin = new User("Admin", "Admin", new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime(), EMAIL, passwordEncoder.encode(ADMIN_PW), location);
//        Team team = new Team("Team", "Soccer", new Location("admin", "admin", "admin", "admin", "admin", "admin"), admin);
//        Formation formation = new Formation("1-4-4-2", team);
//        teamRepository.save(team);
//        formationRepository.save(formation);
        admin.confirmEmail();
        userRepository.save(admin);
    }
}
