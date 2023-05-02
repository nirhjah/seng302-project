package nz.ac.canterbury.seng302.tab.authentication;

import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.Calendar;
import java.util.GregorianCalendar;

@Component
public class AdminAccount implements CommandLineRunner {
    private final UserRepository userRepository;
    @Autowired
    public AdminAccount(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Override
    public void run(String... args) throws Exception {
        Location location = new Location("admin", "admin", "admin", "admin", "admin", "admin");

        User admin = new User("Admin", "Admin", new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime(), "admin@gmail.com", "1", location);
        admin.confirmEmail();
        userRepository.save(admin);
    }
}

