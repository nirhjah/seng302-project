package nz.ac.canterbury.seng302.tab.repository;

import nz.ac.canterbury.seng302.tab.entity.Sport;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class UserRepositoryTest {

    @Autowired
    private UserService userService;

    static final int N_USERS_TO_ADD = 100;

    // Sourced from https://gist.github.com/ruanbekker/a1506f06aa1df06c5a9501cb393626ea
    static final String[] RANDOM_NAMES = {
            "Arved", "Brehme", "Chrismedi", "Dre", "Edwin", "Farren", "Georgy", "Harris", "Ishwar", "Justinas", "Kit",
            "Lael", "Mackie", "Nick", "Orrick", "Paul", "Qasim", "Raphael", "Stevie", "Tyler", "Ubayd", "Vincenzo",
            "Wiktor", "Xander", "Yahya", "Zaine"
    };

    static final String[] LOCATIONS = {
            "a", "b", "c", "d", "e", "f", "g", "h", "i", "j"
    };

    /**
     * Generates a user with a random name, email, and DOB.
     * In future, we might consider making this its own class for testing purposes
     * @return A randomly generated user.
     */
    public static User createRandomUser(String location) {
        Random random = new Random();
        long startDate = new Date(1980, 1, 1).getTime();
        long endDate = new Date(2005, 12, 31).getTime();

        String firstName = RANDOM_NAMES[random.nextInt(0, RANDOM_NAMES.length)];
        String lastName = RANDOM_NAMES[random.nextInt(0, RANDOM_NAMES.length)];
        Date dob = new Date(random.nextLong(startDate, endDate));
        String email = UUID.randomUUID().toString() + "@email.com";
        List<Sport> favSports = new ArrayList<>();

        return new User(firstName, lastName, dob, email, "abc123", favSports, location);
    }

    @BeforeEach
    public void setup() {
        userService.deleteAll();
        for (int i = 0; i < N_USERS_TO_ADD; i++) {
            userService.updateOrAddUser(createRandomUser(LOCATIONS[i % LOCATIONS.length]));
        }
    }


    @Test
    public void searchTeams_testSearch_withOneCity() {
        assertEquals(userService.findUsersByLocation(PageRequest.of(0, 0xffff), "a").toList().size(), 10);
        assertEquals(userService.findUsersByLocation(PageRequest.of(0, 0xffff), "b").toList().size(), 10);
        assertEquals(userService.findUsersByLocation(PageRequest.of(0, 0xffff), "c").toList().size(), 10);
    }

    @Test
    public void searchTeams_testSearch_withMultipleCities() {
        assertEquals(userService.findUsersByLocations(PageRequest.of(0, 0xffff), List.of("a", "b")).toList().size(), 20);
        assertEquals(userService.findUsersByLocations(PageRequest.of(0, 0xffff), List.of("b","c")).toList().size(), 20);
        assertEquals(userService.findUsersByLocations(PageRequest.of(0, 0xffff), List.of("c","d","e")).toList().size(), 30);
    }

    @Test
    public void searchTeams_testSearch_withNoCities() {
        assertEquals(userService.findUsersByLocations(PageRequest.of(0, 0xffff), List.of()).toList().size(), 0);
        // should return all users

    }

    @Test
    public void searchTeams_testSearch_withAllCities() {
        assertEquals(userService.findUsersByLocations(PageRequest.of(0, 0xffff), Arrays.asList(LOCATIONS)).toList().size(), N_USERS_TO_ADD);
        // should also return all teams

    }

}