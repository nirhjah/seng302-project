package nz.ac.canterbury.seng302.tab.controller;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import nz.ac.canterbury.seng302.tab.helper.GenerateRandomUsers;
import nz.ac.canterbury.seng302.tab.service.UserService;

@Controller
public class TestPopulateUsersController {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    UserService userService;

    @Autowired
    GenerateRandomUsers generateRandomUsers;

    static final int N_USERS_TO_ADD = 12;


    /**
     * This method populates our database with 10 random user accounts
     * This is used for testing purposes
     * @return redirects back to the home page
     */
    @GetMapping("/populate_database")
    public String populateDatabaseWithDummyUsers() throws IOException {
        logger.warn("DEBUG ENDPOINT /populate_database - TESTING PURPOSES ONLY");

        for (int i = 0; i < N_USERS_TO_ADD; i++) {
            userService.updateOrAddUser(generateRandomUsers.createRandomUserWithSports());
        }

        logger.warn("/populate_database - {} users added", N_USERS_TO_ADD);

        return "redirect:./";
    }
}
