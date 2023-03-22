package nz.ac.canterbury.seng302.tab.controller;

import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.service.UserService;

import java.io.IOException;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

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

    final int N_USERS_TO_ADD = 10;

    // Sourced from https://gist.github.com/ruanbekker/a1506f06aa1df06c5a9501cb393626ea
    static final String[] RANDOM_NAMES = {
            "Arved", "Brehme", "Chrismedi", "Dre", "Edwin", "Farren", "Georgy", "Harris", "Ishwar", "Justinas", "Kit",
            "Lael", "Mackie", "Nick", "Orrick", "Paul", "Qasim", "Raphael", "Stevie", "Tyler", "Ubayd", "Vincenzo",
            "Wiktor", "Xander", "Yahya", "Zaine"
    };

    /**
     * Generates a user with a random name, email, and DOB.
     * In future, we might consider making this its own class for testing purposes
     * @return A randomly generated user.
     */
    public static User createRandomUser() throws IOException {
        Random random = new Random();
        long startDate = new Date(1980, 1, 1).getTime();
        long endDate = new Date(2005, 12, 31).getTime();

        String firstName = RANDOM_NAMES[random.nextInt(0, RANDOM_NAMES.length)];
        String lastName = RANDOM_NAMES[random.nextInt(0, RANDOM_NAMES.length)];
        Date dob = new Date(random.nextLong(startDate, endDate));
        String email = UUID.randomUUID().toString() + "@email.com";
        
        User user = User.defaultDummyUser();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
            
        return user;
    }

    /**
     * This method populates our database with 10 random user accounts
     * This is used for testing purposes
     * @return redirects back to the home page
     */
    @GetMapping("/populate_database")
    public String populateDatabaseWithDummyUsers() throws IOException {
        logger.warn("DEBUG ENDPOINT /populate_database - TESTING PURPOSES ONLY");

        for (int i = 0; i < N_USERS_TO_ADD; i++) {
            userService.updateOrAddUser(createRandomUser());
        }

        logger.warn("/populate_database - {} users added", N_USERS_TO_ADD);

        return "redirect:./";
    }
}

