package nz.ac.canterbury.seng302.tab.controller;

import nz.ac.canterbury.seng302.tab.entity.Sport;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.repository.SportRepository;
import nz.ac.canterbury.seng302.tab.service.SportService;
import nz.ac.canterbury.seng302.tab.service.UserService;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestPopulateUsersController {
    Logger logger = LoggerFactory.getLogger(TestPopulateUsersController.class);

    @Autowired
    UserService userService;

    @Autowired
    SportRepository sportRepository;

    private final Random random = ThreadLocalRandom.current();

    static final int N_USERS_TO_ADD = 30;

    // Sourced from https://gist.github.com/ruanbekker/a1506f06aa1df06c5a9501cb393626ea
    static final String[] RANDOM_NAMES = {
            "Arved", "Brehme", "Chrismedi", "Dre", "Edwin", "Farren", "Georgy", "Harris", "Ishwar", "Justinas", "Kit",
            "Lael", "Mackie", "Nick", "Orrick", "Paul", "Qasim", "Raphael", "Stevie", "Tyler", "Ubayd", "Vincenzo",
            "Wiktor", "Xander", "Yahya", "Zaine"
    };
    static final String[] RANDOM_SPORTS = {
            "Hockey", "Rugby", "E-Sports", "Cheese eating (personal fave)", "Boot throwing", "Driving with screaming kids in the back"
    };

    /**
     * Generates a user with a random name, email, and DOB.
     * In future, we might consider making this its own class for testing purposes
     * @return A randomly generated user.
     */
    public User createRandomUser() {
        // Generate random sports
        if (sportRepository.count() == 0) {
            for (String sportName: RANDOM_SPORTS) {
                Sport sport = new Sport(sportName);
                sportRepository.save(sport);
            }
        }
        // Add random sports
        
        String firstName = RANDOM_NAMES[random.nextInt(0, RANDOM_NAMES.length)];
        String lastName = RANDOM_NAMES[random.nextInt(0, RANDOM_NAMES.length)];
        String email = UUID.randomUUID().toString() + "@email.com";
        List<Sport> allSports = sportRepository.findAll();
        Collections.shuffle(allSports, random);
        List<Sport> ourSports = allSports.subList(0, random.nextInt(allSports.size()-1));
        logger.info("Gave '{} {}' {} sport(s)", firstName, lastName, ourSports.size());
        long startDate = new Date(1980, 1, 1).getTime();
        long endDate = new Date(2005, 12, 31).getTime();
        Date dob = new Date(random.nextLong(startDate, endDate));
            
        return new User(firstName, lastName, dob, email, "abc123", ourSports);

    }

    /**
     * This method populates our database with 10 random user accounts
     * This is used for testing purposes
     * @return redirects back to the home page
     */
    @GetMapping("/populate_database")
    public String populateDatabaseWithDummyUsers() {
        logger.warn("DEBUG ENDPOINT /populate_database - TESTING PURPOSES ONLY");

        for (int i = 0; i < N_USERS_TO_ADD; i++) {
            userService.updateOrAddUser(createRandomUser());
        }

        logger.warn("/populate_database - {} users added", N_USERS_TO_ADD);

        return "redirect:./";
    }
}
