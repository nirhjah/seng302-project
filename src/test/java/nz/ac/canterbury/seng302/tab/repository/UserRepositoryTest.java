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

    static final String[] LOCATIONS = {
            "a", "b", "c", "d", "e", "f", "g", "h", "i", "j"
    };

    static final PageRequest TAKE_ALL = PageRequest.of(0, 0xffff);

    public static User createUserAtLocation(String location) {
        Random random = new Random();
        long startDate = new Date(1980, 1, 1).getTime();
        long endDate = new Date(2005, 12, 31).getTime();

        String firstName = "john";
        String lastName = "doe";
        Date dob = new Date(random.nextLong(startDate, endDate));
        String email = UUID.randomUUID().toString() + "@email.com";
        List<Sport> favSports = List.of();

        return new User(firstName, lastName, dob, email, "abc123", favSports, location);
    }

    @BeforeEach
    public void setup() {
        userService.deleteAll();
        for (int i = 0; i < N_USERS_TO_ADD; i++) {
            userService.updateOrAddUser(createUserAtLocation(LOCATIONS[i % LOCATIONS.length]));
        }
    }


    @Test
    public void searchTeams_testSearch_withOneCity() {
        assertEquals(userService.findUsersByLocation(TAKE_ALL, "a").toList().size(), 10);
        assertEquals(userService.findUsersByLocation(TAKE_ALL, "b").toList().size(), 10);
        assertEquals(userService.findUsersByLocation(TAKE_ALL, "c").toList().size(), 10);
    }

    @Test
    public void searchTeams_testSearch_withMultipleCities() {
        assertEquals(userService.findUsersByLocations(TAKE_ALL, List.of("a", "b")).toList().size(), 20);
        assertEquals(userService.findUsersByLocations(TAKE_ALL, List.of("b","c")).toList().size(), 20);
        assertEquals(userService.findUsersByLocations(TAKE_ALL, List.of("c","d","e")).toList().size(), 30);
    }

    @Test
    public void searchTeams_testSearch_withNoCities() {
        assertEquals(userService.findUsersByLocations(TAKE_ALL, List.of()).toList().size(), 0);
        // should return all users

    }

    @Test
    public void searchTeams_testSearch_withAllCities() {
        assertEquals(userService.findUsersByLocations(TAKE_ALL, Arrays.asList(LOCATIONS)).toList().size(), N_USERS_TO_ADD);
        // should also return all teams
    }
}