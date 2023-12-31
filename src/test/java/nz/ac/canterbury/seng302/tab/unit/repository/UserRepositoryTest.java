package nz.ac.canterbury.seng302.tab.unit.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import nz.ac.canterbury.seng302.tab.repository.LocationRepository;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import nz.ac.canterbury.seng302.tab.service.UserService;
import nz.ac.canterbury.seng302.tab.entity.Location;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;

import nz.ac.canterbury.seng302.tab.entity.Sport;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.helper.GenerateRandomUsers;


@DataJpaTest
@Import(GenerateRandomUsers.class)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LocationRepository locationRepository;

    // Helper class to make random users.
    @Autowired
    private GenerateRandomUsers generateRandomUsers;

    private User createUserWithName(@Nullable String firstName, @Nullable String lastName) throws IOException {
        User user = generateRandomUsers.createRandomUser();
        if (firstName != null) {
            user.setFirstName(firstName);
        }
        if (lastName != null) {
            user.setLastName(lastName);
        }

        return user;
    }

    @Test
    void findByNameOrSport_whenParamsAreEmpty_returnAllUsers() throws IOException {
        final int N_USERS = 10;
        // Populate database
        for (int i = 0; i < N_USERS; i++) {
            userRepository.save(generateRandomUsers.createRandomUser());
        }


        Page<User> returnedUsers = userRepository.findUserByFilteredLocationsAndSports(PageRequest.ofSize(N_USERS),new ArrayList<>(), new ArrayList<>(), "");

        assertEquals(N_USERS, returnedUsers.getSize());
    }

    @Test
    void findByName_whenFirstNameIsMatched_returnCorrectUsers() throws IOException {
        final int N_VALID_USERS = 5;
        final int N_INVALID_USERS = 10;
        final String SEARCH = "TestUserTheThird";
        User user;
        // Populate database
        for (int i = 0; i < N_VALID_USERS; i++) {
            user = generateRandomUsers.createRandomUser();
            user.setFirstName(SEARCH);
            userRepository.save(user);
        }
        for (int i = 0; i < N_INVALID_USERS; i++) {
            user = generateRandomUsers.createRandomUser();
            userRepository.save(user);
        }

        Page<User> returnedUsers = userRepository.findUserByFilteredLocationsAndSports(PageRequest.ofSize(N_VALID_USERS + N_INVALID_USERS), new ArrayList<>(), new ArrayList<>(), SEARCH);

        assertEquals(N_VALID_USERS, returnedUsers.getNumberOfElements());
    }

    @Test
    void findByName_whenLastNameIsMatched_returnCorrectUsers() throws IOException {
        final int N_VALID_USERS = 5;
        final int N_INVALID_USERS = 10;
        final String SEARCH = "TestUserTheThird";
        User user;
        // Populate database
        for (int i = 0; i < N_VALID_USERS; i++) {
            user = generateRandomUsers.createRandomUser();
            user.setLastName(SEARCH);
            userRepository.save(user);
        }
        for (int i = 0; i < N_INVALID_USERS; i++) {
            user = generateRandomUsers.createRandomUser();
            userRepository.save(user);
        }

        Page<User> returnedUsers = userRepository.findUserByFilteredLocationsAndSports(PageRequest.ofSize(N_VALID_USERS + N_INVALID_USERS), List.of(), List.of(), SEARCH);

        assertEquals(N_VALID_USERS, returnedUsers.getNumberOfElements());
    }

    @Test
    void findByName_whenNamesMatchNoOne_returnNoUsers() throws IOException {
        final int N_INVALID_USERS = 10;
        final String SEARCH = "1234567890";
        User user;
        // Populate database
        for (int i = 0; i < N_INVALID_USERS; i++) {
            user = generateRandomUsers.createRandomUser();
            userRepository.save(user);
        }

        Page<User> returnedUsers = userRepository.findUserByFilteredLocationsAndSports(PageRequest.ofSize(N_INVALID_USERS), List.of(), List.of(), SEARCH);

        assertEquals(0, returnedUsers.getNumberOfElements());
    }

    /**
     * NOTE: Right now, the database doesn't care about name order.
     * e.g. searching "Davis Miles" matches User(Miles, Davis).
     * This test purposefully provides them in the correct order, so that isn't tested... */
    @Test
    void findByName_whenFirstOrLastNameIsMatched_returnCorrectUsers() throws IOException {
        final int N_VALID_USERS = 3;
        final int N_INVALID_USERS = 10;
        final String FIRST_NAME = "TestUserTheThird";
        final String LAST_NAME = "OfHouseCheeseburgerLovers";
        final String SEARCH = FIRST_NAME + ' ' + LAST_NAME;
        User user;
        ArrayList<String> correctUsersEmails = new ArrayList<>();   // Saving their emails, as they're unique identifiers 
        // Populate database
        // Matches first name
        user = generateRandomUsers.createRandomUser();
        user.setFirstName(FIRST_NAME);
        correctUsersEmails.add(userRepository.save(user).getEmail());
        // Matches last name
        user = generateRandomUsers.createRandomUser();
        user.setLastName(LAST_NAME);
        correctUsersEmails.add(userRepository.save(user).getEmail());
        // Matches first AND last name
        user = generateRandomUsers.createRandomUser();
        user.setFirstName(FIRST_NAME);
        user.setLastName(LAST_NAME);
        correctUsersEmails.add(userRepository.save(user).getEmail());
        

        Page<User> returnedUsers = userRepository.findUserByFilteredLocationsAndSports(PageRequest.ofSize(N_VALID_USERS + N_INVALID_USERS), List.of(), List.of(), SEARCH);

        assertEquals(N_VALID_USERS, returnedUsers.getNumberOfElements());
        var returnedUsersEmails = returnedUsers.stream().map(User::getEmail).toList();
        for (int i = 0; i < correctUsersEmails.size(); i++) {
            assertEquals(correctUsersEmails.get(i), returnedUsersEmails.get(i));
        }
    }

    @Test
    void findBySport_whenSportIsMatched_returnCorrectUsers() throws IOException {
        final String CORRECT_SPORT = "Hockey";
        final String OTHER_SPORT = "Rugby";
        Sport hockey = new Sport(CORRECT_SPORT);
        Sport rugby = new Sport(OTHER_SPORT);

        // Generate users with rugby or hockey
        User hockeyPlayer = generateRandomUsers.createRandomUser();
        User rugbyPlayer = generateRandomUsers.createRandomUser();
        hockeyPlayer.setFavoriteSports(List.of(hockey));
        rugbyPlayer.setFavoriteSports(List.of(rugby));
        userRepository.save(hockeyPlayer);
        userRepository.save(rugbyPlayer);

        Page<User> returnedUsers = userRepository.findUserByFilteredLocationsAndSports(PageRequest.ofSize(5), List.of(), List.of(CORRECT_SPORT), "");

        assertEquals(1, returnedUsers.getNumberOfElements());
    }

    @Test
    void findBySport_whenUsersHaveMultipleSports_returnCorrectUsers() throws IOException {
        User user;
        final int N_JUST_HOCKEY = 7;
        final int N_HOCKEY_AND_RUGBY = 11;
        final String S_HOCKEY = "Hockey";
        final String S_RUGBY = "Rugby";
        Sport hockey = new Sport(S_HOCKEY);
        Sport rugby = new Sport(S_RUGBY);

        // Generate users with just hockey
        for (int i = 0; i < N_JUST_HOCKEY; i++) {
            user = generateRandomUsers.createRandomUser();
            user.setFavoriteSports(List.of(hockey));
            userRepository.save(user);
        }
        // Generate users with rugby AND hockey
        for (int i = 0; i < N_HOCKEY_AND_RUGBY; i++) {
            user = generateRandomUsers.createRandomUser();
            user.setFavoriteSports(List.of(hockey, rugby));
            userRepository.save(user);
        }

        Page<User> returnedUsers = userRepository.findUserByFilteredLocationsAndSports(PageRequest.ofSize(500), List.of(), List.of(S_RUGBY), "");

        assertEquals(N_HOCKEY_AND_RUGBY , returnedUsers.getNumberOfElements());
    }

    @Test
    void findBySport_whenSearchingByMultipleSports_returnCorrectUsers() throws IOException {
        User user;
        final int N_ZERO_SPORTS = 7;
        final int N_RUGBY = 11;
        final String S_HOCKEY = "Hockey";
        final String S_RUGBY = "Rugby";
        Sport rugby = new Sport(S_RUGBY);

        // Generate users with no sports
        for (int i = 0; i < N_ZERO_SPORTS; i++) {
            user = generateRandomUsers.createRandomUser();
            userRepository.save(user);
        }
        // Generate users with rugby
        for (int i = 0; i < N_RUGBY; i++) {
            user = generateRandomUsers.createRandomUser();
            user.setFavoriteSports(List.of(rugby));
            userRepository.save(user);
        }
        // Search with an additional sport that no one has.
        Page<User> returnedUsers = userRepository.findUserByFilteredLocationsAndSports(PageRequest.ofSize(500), List.of(), List.of(S_RUGBY, S_HOCKEY), "");

        assertEquals(N_RUGBY , returnedUsers.getNumberOfElements());
    }

    @Test
    void findBySportAndName_whenSearchingByNameAndSports_returnCorrectUser() throws IOException {
        User user;
        final String SEARCH_NAME = "TDDHater";
        final String S_HOCKEY = "Hockey";
        final String S_RUGBY = "Rugby";
        Sport hockey = new Sport(S_HOCKEY);
        Sport rugby = new Sport(S_RUGBY);

        // User with the correct name and sport
        user = createUserWithName(SEARCH_NAME, null);
        user.setFavoriteSports(List.of(hockey));
        userRepository.save(user);
        // User with incorrect name and wrong sport
        user = generateRandomUsers.createRandomUser();
        user.setFavoriteSports(List.of(rugby));
        userRepository.save(user);
        // Search with an additional sport that no one has.
        Page<User> returnedUsers = userRepository.findUserByFilteredLocationsAndSports(PageRequest.ofSize(500), List.of() ,List.of(S_HOCKEY), SEARCH_NAME);

        assertEquals(1, returnedUsers.getNumberOfElements());
    }
    @Test
    void findBySportAndName_whenSearchingByNameAndMultipleSports_returnCorrectUser() throws IOException {
        User user;
        final String SEARCH_NAME = "TDDHater";
        final String S_HOCKEY = "Hockey";
        final String S_RUGBY = "Rugby";
        Sport hockey = new Sport(S_HOCKEY);
        Sport rugby = new Sport(S_RUGBY);

        // User with the correct name and sport
        user = createUserWithName(SEARCH_NAME, null);
        user.setFavoriteSports(List.of(hockey));
        userRepository.save(user);
        // User with the correct name and two sports (one correct)
        user = createUserWithName(SEARCH_NAME, null);
        user.setFavoriteSports(List.of(hockey, rugby));
        userRepository.save(user);
        // User with incorrect name and wrong sport
        user = generateRandomUsers.createRandomUser();
        user.setFavoriteSports(List.of(rugby));
        userRepository.save(user);
        // Search with an additional sport that no one has.
        Page<User> returnedUsers = userRepository.findUserByFilteredLocationsAndSports(PageRequest.ofSize(500), List.of(), List.of(S_HOCKEY), SEARCH_NAME);

        assertEquals(2, returnedUsers.getNumberOfElements());
    }

    @Test
    void findBySportAndName_whenNameMatchesButNotSport_returnNoUsers() throws IOException {
        User user;
        final String SEARCH_NAME = "TDDHater";
        final String S_HOCKEY = "Hockey";
        final String S_RUGBY = "Rugby";
        Sport rugby = new Sport(S_RUGBY);

        // User with the correct name and but wrong sport
        user = generateRandomUsers.createRandomUser();
        user.setFirstName(SEARCH_NAME);
        user.setFavoriteSports(List.of(rugby));
        userRepository.save(user);
        // Search with an additional sport that no one has.
        Page<User> returnedUsers = userRepository.findUserByFilteredLocationsAndSports(PageRequest.ofSize(500), List.of(), List.of(S_HOCKEY), SEARCH_NAME);

        assertEquals(0, returnedUsers.getNumberOfElements());
    }
    @Test
    void findBySportAndName_whenSportMatchesButNotName_returnNoUsers() throws IOException {
        User user;
        final String USER_NAME = "LovesJUnit";
        final String SEARCH_NAME = "TDDHater";
        final String S_RUGBY = "Rugby";
        Sport rugby = new Sport(S_RUGBY);

        // User with the wrong name but right sport
        user = generateRandomUsers.createRandomUser();
        user.setFirstName(USER_NAME);
        user.setFavoriteSports(List.of(rugby));
        userRepository.save(user);

        // Search with an additional sport that no one has.
        Page<User> returnedUsers = userRepository.findUserByFilteredLocationsAndSports(PageRequest.ofSize(500), List.of(), List.of(S_RUGBY), SEARCH_NAME);

        assertEquals(0, returnedUsers.getNumberOfElements());
    }

    /**HERE**/

    @Test
    void findByLocation_whenLocationIsMatched_returnCorrectUsers() throws IOException {
        Location nelson = new Location(null, null, null, "Nelson", "8000", "New Zealand");
        User nelsonUser = generateRandomUsers.createRandomUser();
        nelsonUser.setLocation(nelson);
        User christchurchUser = generateRandomUsers.createRandomUser();
        userRepository.save(nelsonUser);
        userRepository.save(christchurchUser);

        Page<User> returnedUsers = userRepository.findUserByFilteredLocationsAndSports(PageRequest.ofSize(5), List.of(nelson.getCity()), List.of(), "");

        assertEquals(returnedUsers.toList(), List.of(nelsonUser));
    }

    @Test
    void findByLocation_whenSearchingByMultipleLocations_returnCorrectUsers() throws IOException {
        User nelsonUser = generateRandomUsers.createRandomUser();
        Location nelson = new Location(null, null, null, "Nelson", null, "NZ");
        nelsonUser.setLocation(nelson);
        User aucklandUser = generateRandomUsers.createRandomUser();
        Location auckland = new Location(null, null, null, "Auckland", null, "NZ");
        aucklandUser.setLocation(auckland);
        User christchurchUser = generateRandomUsers.createRandomUser();

        userRepository.save(nelsonUser);
        userRepository.save(aucklandUser);
        userRepository.save(christchurchUser);

        // Search with an additional sport that no one has.
        Page<User> returnedUsers = userRepository.findUserByFilteredLocationsAndSports(PageRequest.ofSize(500), List.of(auckland.getCity(), nelson.getCity()), List.of(), "");
        assertEquals(returnedUsers.toList(), List.of(nelsonUser, aucklandUser));
    }

    @Test
    void findByLocationAndName_whenSearchingByNameAndLocation_returnCorrectUser() throws IOException {
        User nelsonUser = generateRandomUsers.createRandomUser();
        Location nelson = new Location(null, null, null, "Nelson", null, "NZ");
        nelsonUser.setLocation(nelson);
        User aucklandUser = generateRandomUsers.createRandomUser();
        Location auckland = new Location(null, null, null, "Auckland", null, "NZ");
        aucklandUser.setLocation(auckland);
        aucklandUser.setFirstName("Test");
        User christchurchUser = generateRandomUsers.createRandomUser();

        userRepository.save(nelsonUser);
        userRepository.save(aucklandUser);
        userRepository.save(christchurchUser);

        Page<User> returnedUsers = userRepository.findUserByFilteredLocationsAndSports(PageRequest.ofSize(500), List.of(auckland.getCity()), List.of(), aucklandUser.getFirstName());
        assertEquals(List.of(aucklandUser), returnedUsers.toList());
    }
    @Test
    void findByLocationAndName_whenSearchingByNameAndMultipleLocations_returnCorrectUser() throws IOException {
        User nelsonUser = generateRandomUsers.createRandomUser();
        Location nelson = new Location(null, null, null, "Nelson", null, "NZ");
        nelsonUser.setLocation(nelson);
        User aucklandUser = generateRandomUsers.createRandomUser();
        Location auckland = new Location(null, null, null, "Auckland", null, "NZ");
        aucklandUser.setLocation(auckland);
        aucklandUser.setFirstName("Test");
        User christchurchUser = generateRandomUsers.createRandomUser();

        userRepository.save(nelsonUser);
        userRepository.save(aucklandUser);
        userRepository.save(christchurchUser);

        Page<User> returnedUsers = userRepository.findUserByFilteredLocationsAndSports(PageRequest.ofSize(500), List.of(auckland.getCity(), nelson.getCity()), List.of(), aucklandUser.getFirstName());
        assertEquals(List.of(aucklandUser), returnedUsers.toList());
    }

    @Test
    void findByLocationAndName_whenNameMatchesButNotLocation_returnNoUsers() throws IOException {
        User nelsonUser = generateRandomUsers.createRandomUser();
        Location nelson = new Location(null, null, null, "Nelson", null, "NZ");
        nelsonUser.setLocation(nelson);
        User aucklandUser = generateRandomUsers.createRandomUser();
        Location auckland = new Location(null, null, null, "Auckland", null, "NZ");
        aucklandUser.setLocation(auckland);
        aucklandUser.setFirstName("Test");
        User christchurchUser = generateRandomUsers.createRandomUser();
        Location sanJose = new Location(null, null, null, "San Jose", null, "Costa Rica");

        userRepository.save(nelsonUser);
        userRepository.save(aucklandUser);
        userRepository.save(christchurchUser);

        Page<User> returnedUsers = userRepository.findUserByFilteredLocationsAndSports(PageRequest.ofSize(500), List.of(sanJose.getCity()), List.of(), aucklandUser.getFirstName());
        assertTrue(returnedUsers.isEmpty());
    }
    @Test
    void findByLocationAndName_whenLocationMatchesButNotName_returnNoUsers() throws IOException {
        User nelsonUser = generateRandomUsers.createRandomUser();
        Location nelson = new Location(null, null, null, "Nelson", null, "NZ");
        nelsonUser.setLocation(nelson);
        User aucklandUser = generateRandomUsers.createRandomUser();
        Location auckland = new Location(null, null, null, "Auckland", null, "NZ");
        aucklandUser.setLocation(auckland);
        aucklandUser.setFirstName("Test");
        User christchurchUser = generateRandomUsers.createRandomUser();

        userRepository.save(nelsonUser);
        userRepository.save(aucklandUser);
        userRepository.save(christchurchUser);

        Page<User> returnedUsers = userRepository.findUserByFilteredLocationsAndSports(PageRequest.ofSize(500), List.of(auckland.getCity()), List.of(), "SENG");
        assertTrue(returnedUsers.isEmpty());
    }

    @Test
    void findByLocationAndSport_whenSportMatchAndLocationMatch_returnCorrectUser() throws IOException {
        Location nelson = new Location(null, null, null, "Nelson", null, "NZ");
        locationRepository.save(nelson);
        User nelsonRugby = generateRandomUsers.createRandomUser();
        nelsonRugby.setLocation(nelson);
        nelsonRugby.setFavoriteSports(List.of(new Sport("Rugby")));
        User nelsonHockey = generateRandomUsers.createRandomUser();
        nelsonHockey.setLocation(nelson);
        nelsonHockey.setFavoriteSports(List.of(new Sport("Hockey")));
        User aucklandHockey = generateRandomUsers.createRandomUser();
        Location auckland = new Location(null, null, null, "Auckland", null, "NZ");
        aucklandHockey.setLocation(auckland);
        aucklandHockey.setFavoriteSports(List.of(new Sport("Hockey")));
        User christchurchHockey = generateRandomUsers.createRandomUser();
        christchurchHockey.setFavoriteSports(List.of(new Sport("Hockey")));

        userRepository.save(nelsonHockey);
        userRepository.save(nelsonRugby);
        userRepository.save(aucklandHockey);
        userRepository.save(christchurchHockey);

        Page<User> returnedUsers = userRepository.findUserByFilteredLocationsAndSports(PageRequest.ofSize(500), List.of(nelson.getCity()), List.of("Hockey"), "");
        assertEquals(Set.of(nelsonHockey), returnedUsers.toSet());
    }

    @Test
    void findByLocationAndSport_whenMultipleSportMatchAndLocationMatch_returnCorrectUser() throws IOException {
        Location nelson = new Location(null, null, null, "Nelson", null, "NZ");
        locationRepository.save(nelson);
        User nelsonRugby = generateRandomUsers.createRandomUser();
        nelsonRugby.setLocation(nelson);
        nelsonRugby.setFavoriteSports(List.of(new Sport("Rugby")));
        User nelsonHockey = generateRandomUsers.createRandomUser();
        nelsonHockey.setLocation(nelson);
        nelsonHockey.setFavoriteSports(List.of(new Sport("Hockey")));
        User aucklandHockey = generateRandomUsers.createRandomUser();
        Location auckland = new Location(null, null, null, "Auckland", null, "NZ");
        aucklandHockey.setLocation(auckland);
        aucklandHockey.setFavoriteSports(List.of(new Sport("Hockey")));
        User christchurchHockey = generateRandomUsers.createRandomUser();
        christchurchHockey.setFavoriteSports(List.of(new Sport("Hockey")));

        userRepository.save(nelsonHockey);
        userRepository.save(nelsonRugby);
        userRepository.save(aucklandHockey);
        userRepository.save(christchurchHockey);

        Page<User> returnedUsers = userRepository.findUserByFilteredLocationsAndSports(PageRequest.ofSize(500), List.of(nelson.getCity()), List.of("Hockey", "Rugby"), "");
        assertEquals(2, returnedUsers.getNumberOfElements());
        assertEquals(Set.of(nelsonHockey, nelsonRugby), returnedUsers.toSet());
    }

    @Test
    void findByLocationAndSport_whenSportMatchAndMultipleLocationMatch_returnCorrectUser() throws IOException {
        Location nelson = new Location(null, null, null, "Nelson", null, "NZ");
        locationRepository.save(nelson);
        User nelsonRugby = generateRandomUsers.createRandomUser();
        nelsonRugby.setLocation(nelson);
        nelsonRugby.setFavoriteSports(List.of(new Sport("Rugby")));
        User nelsonHockey = generateRandomUsers.createRandomUser();
        nelsonHockey.setLocation(nelson);
        nelsonHockey.setFavoriteSports(List.of(new Sport("Hockey")));
        User aucklandHockey = generateRandomUsers.createRandomUser();
        Location auckland = new Location(null, null, null, "Auckland", null, "NZ");
        aucklandHockey.setLocation(auckland);
        aucklandHockey.setFavoriteSports(List.of(new Sport("Hockey")));
        User christchurchHockey = generateRandomUsers.createRandomUser();
        christchurchHockey.setFavoriteSports(List.of(new Sport("Hockey")));

        userRepository.save(nelsonHockey);
        userRepository.save(nelsonRugby);
        userRepository.save(aucklandHockey);
        userRepository.save(christchurchHockey);

        Page<User> returnedUsers = userRepository.findUserByFilteredLocationsAndSports(PageRequest.ofSize(500), List.of(nelson.getCity(), auckland.getCity()), List.of("Hockey"), "");
        assertEquals(2, returnedUsers.getNumberOfElements());
        assertEquals(Set.of(aucklandHockey, nelsonHockey), returnedUsers.toSet());
    }

    @Test
    void findByLocationAndSport_whenSportMatchButNotLocationMatch_returnNoUser() throws IOException {
        Location nelson = new Location(null, null, null, "Nelson", null, "NZ");
        User nelsonHockey = generateRandomUsers.createRandomUser();
        nelsonHockey.setLocation(nelson);
        nelsonHockey.setFavoriteSports(List.of(new Sport("Hockey")));
        User aucklandHockey = generateRandomUsers.createRandomUser();
        Location auckland = new Location(null, null, null, "Auckland", null, "NZ");
        aucklandHockey.setLocation(auckland);
        aucklandHockey.setFavoriteSports(List.of(new Sport("Hockey")));
        User christchurchHockey = generateRandomUsers.createRandomUser();
        christchurchHockey.setFavoriteSports(List.of(new Sport("Hockey")));

        userRepository.save(nelsonHockey);
        userRepository.save(aucklandHockey);
        userRepository.save(christchurchHockey);

        Location sanJose = new Location(null, null, null, "San Jose", null, "Costa Rica");

        Page<User> returnedUsers = userRepository.findUserByFilteredLocationsAndSports(PageRequest.ofSize(500), List.of(sanJose.getCity()), List.of("Hockey"), "");
        assertTrue(returnedUsers.isEmpty());
    }

    @Test
    void findByLocationAndSport_whenNoSportMatchAndLocationMatch_returnNoUser() throws IOException {
        Location nelson = new Location(null, null, null, "Nelson", null, "NZ");
        User nelsonHockey = generateRandomUsers.createRandomUser();
        nelsonHockey.setLocation(nelson);
        nelsonHockey.setFavoriteSports(List.of(new Sport("Hockey")));
        User christchurchHockey = generateRandomUsers.createRandomUser();
        christchurchHockey.setFavoriteSports(List.of(new Sport("Hockey")));

        userRepository.save(nelsonHockey);
        userRepository.save(christchurchHockey);

        Page<User> returnedUsers = userRepository.findUserByFilteredLocationsAndSports(PageRequest.ofSize(500), List.of(nelson.getCity()), List.of("Football"), "");
        assertTrue(returnedUsers.isEmpty());
    }

    @Test
    void findByLocationAndSport_whenNoSportMatchAndNoLocationMatch_returnNoUser() throws IOException {
        Location nelson = new Location(null, null, null, "Nelson", null, "NZ");
        User nelsonHockey = generateRandomUsers.createRandomUser();
        nelsonHockey.setLocation(nelson);
        nelsonHockey.setFavoriteSports(List.of(new Sport("Hockey")));

        User christchurchHockey = generateRandomUsers.createRandomUser();
        christchurchHockey.setFavoriteSports(List.of(new Sport("Hockey")));

        userRepository.save(nelsonHockey);
        userRepository.save(christchurchHockey);

        Location sanJose = new Location(null, null, null, "San Jose", null, "Costa Rica");

        Page<User> returnedUsers = userRepository.findUserByFilteredLocationsAndSports(PageRequest.ofSize(500), List.of(sanJose.getCity()), List.of("Football"), "");
        assertTrue(returnedUsers.isEmpty());
    }

    @Test
    void findByLocationAndSportAndName_whenSportMatchAndLocationMatchAndName_returnCorrectUser() throws IOException {
        Location nelson = new Location(null, null, null, "Nelson", null, "NZ");
        locationRepository.save(nelson);
        User nelsonRugby = generateRandomUsers.createRandomUser();
        nelsonRugby.setLocation(nelson);
        nelsonRugby.setFavoriteSports(List.of(new Sport("Rugby")));
        nelsonRugby.setFirstName("NelsonRugby");
        User nelsonHockey = generateRandomUsers.createRandomUser();
        nelsonHockey.setLocation(nelson);
        nelsonHockey.setFavoriteSports(List.of(new Sport("Hockey")));
        User aucklandHockey = generateRandomUsers.createRandomUser();
        Location auckland = new Location(null, null, null, "Auckland", null, "NZ");
        aucklandHockey.setLocation(auckland);
        aucklandHockey.setFavoriteSports(List.of(new Sport("Hockey")));
        User christchurchHockey = generateRandomUsers.createRandomUser();
        christchurchHockey.setFavoriteSports(List.of(new Sport("Hockey")));

        userRepository.save(nelsonHockey);
        userRepository.save(nelsonRugby);
        userRepository.save(aucklandHockey);
        userRepository.save(christchurchHockey);

        Page<User> returnedUsers = userRepository.findUserByFilteredLocationsAndSports(PageRequest.ofSize(500), List.of(nelson.getCity()), List.of("Rugby"), "NelsonRugby");
        assertEquals(Set.of(nelsonRugby), returnedUsers.toSet());
    }

    @Test
    void findByLocationAndSportAndName_whenMultipleSportMatchAndLocationMatchAndName_returnCorrectUser() throws IOException {
        Location nelson = new Location(null, null, null, "Nelson", null, "NZ");
        locationRepository.save(nelson);
        User nelsonRugby = generateRandomUsers.createRandomUser();
        nelsonRugby.setLocation(nelson);
        nelsonRugby.setFavoriteSports(List.of(new Sport("Rugby")));
        nelsonRugby.setFirstName("NelsonSport");
        User nelsonHockey = generateRandomUsers.createRandomUser();
        nelsonHockey.setLocation(nelson);
        nelsonHockey.setFavoriteSports(List.of(new Sport("Hockey")));
        nelsonHockey.setFirstName("NelsonSport");
        User aucklandHockey = generateRandomUsers.createRandomUser();
        Location auckland = new Location(null, null, null, "Auckland", null, "NZ");
        aucklandHockey.setLocation(auckland);
        aucklandHockey.setFavoriteSports(List.of(new Sport("Hockey")));
        User christchurchHockey = generateRandomUsers.createRandomUser();
        christchurchHockey.setFavoriteSports(List.of(new Sport("Hockey")));

        userRepository.save(nelsonHockey);
        userRepository.save(nelsonRugby);
        userRepository.save(aucklandHockey);
        userRepository.save(christchurchHockey);

        Page<User> returnedUsers = userRepository.findUserByFilteredLocationsAndSports(PageRequest.ofSize(500), List.of(nelson.getCity()), List.of("Rugby", "Hockey"), "NelsonSport");
        assertEquals(Set.of(nelsonHockey, nelsonRugby), returnedUsers.toSet());
    }

    @Test
    void findByLocationAndSportAndName_whenSportMatchButNotLocationMatchAndNameMatch_returnNoUser() throws IOException {
        Location nelson = new Location(null, null, null, "Nelson", null, "NZ");
        locationRepository.save(nelson);
        User nelsonRugby = generateRandomUsers.createRandomUser();
        nelsonRugby.setLocation(nelson);
        nelsonRugby.setFavoriteSports(List.of(new Sport("Rugby")));
        nelsonRugby.setFirstName("NelsonRugby");
        User nelsonHockey = generateRandomUsers.createRandomUser();
        nelsonHockey.setLocation(nelson);
        nelsonHockey.setFavoriteSports(List.of(new Sport("Hockey")));
        User aucklandHockey = generateRandomUsers.createRandomUser();
        Location auckland = new Location(null, null, null, "Auckland", null, "NZ");
        aucklandHockey.setLocation(auckland);
        aucklandHockey.setFavoriteSports(List.of(new Sport("Hockey")));
        User christchurchHockey = generateRandomUsers.createRandomUser();
        christchurchHockey.setFavoriteSports(List.of(new Sport("Hockey")));

        userRepository.save(nelsonHockey);
        userRepository.save(nelsonRugby);
        userRepository.save(aucklandHockey);
        userRepository.save(christchurchHockey);

        Location sanJose = new Location(null, null, null, "San Jose", null, "Costa Rica");

        Page<User> returnedUsers = userRepository.findUserByFilteredLocationsAndSports(PageRequest.ofSize(500), List.of(sanJose.getCity()), List.of("Hockey"), "NelsonRugby");
        assertTrue(returnedUsers.isEmpty());
    }

    @Test
    void findByLocationAndSportAndName_whenNoSportMatchAndLocationMatchAndNameMatch_returnNoUser() throws IOException {
        Location nelson = new Location(null, null, null, "Nelson", null, "NZ");
        locationRepository.save(nelson);
        User nelsonRugby = generateRandomUsers.createRandomUser();
        nelsonRugby.setLocation(nelson);
        nelsonRugby.setFavoriteSports(List.of(new Sport("Rugby")));
        nelsonRugby.setFirstName("NelsonRugby");
        User nelsonHockey = generateRandomUsers.createRandomUser();
        nelsonHockey.setLocation(nelson);
        nelsonHockey.setFavoriteSports(List.of(new Sport("Hockey")));
        User aucklandHockey = generateRandomUsers.createRandomUser();
        Location auckland = new Location(null, null, null, "Auckland", null, "NZ");
        aucklandHockey.setLocation(auckland);
        aucklandHockey.setFavoriteSports(List.of(new Sport("Hockey")));
        User christchurchHockey = generateRandomUsers.createRandomUser();
        christchurchHockey.setFavoriteSports(List.of(new Sport("Hockey")));

        userRepository.save(nelsonHockey);
        userRepository.save(nelsonRugby);
        userRepository.save(aucklandHockey);
        userRepository.save(christchurchHockey);

        Page<User> returnedUsers = userRepository.findUserByFilteredLocationsAndSports(PageRequest.ofSize(500), List.of(nelson.getCity()), List.of("Football"), "NelsonRugby");
        assertTrue(returnedUsers.isEmpty());
    }

    @Test
    void findByLocationAndSportAndName_whenSportMatchButNotLocationMatchAndNoNameMatch_returnNoUser() throws IOException {
        Location nelson = new Location(null, null, null, "Nelson", null, "NZ");
        locationRepository.save(nelson);
        User nelsonRugby = generateRandomUsers.createRandomUser();
        nelsonRugby.setLocation(nelson);
        nelsonRugby.setFavoriteSports(List.of(new Sport("Rugby")));
        User nelsonHockey = generateRandomUsers.createRandomUser();
        nelsonHockey.setLocation(nelson);
        nelsonHockey.setFavoriteSports(List.of(new Sport("Hockey")));
        User aucklandHockey = generateRandomUsers.createRandomUser();
        Location auckland = new Location(null, null, null, "Auckland", null, "NZ");
        aucklandHockey.setLocation(auckland);
        aucklandHockey.setFavoriteSports(List.of(new Sport("Hockey")));
        User christchurchHockey = generateRandomUsers.createRandomUser();
        christchurchHockey.setFavoriteSports(List.of(new Sport("Hockey")));

        userRepository.save(nelsonHockey);
        userRepository.save(nelsonRugby);
        userRepository.save(aucklandHockey);
        userRepository.save(christchurchHockey);

        Location sanJose = new Location(null, null, null, "San Jose", null, "Costa Rica");

        Page<User> returnedUsers = userRepository.findUserByFilteredLocationsAndSports(PageRequest.ofSize(500), List.of(sanJose.getCity()), List.of("Hockey"), "SENG");
        assertTrue(returnedUsers.isEmpty());
    }

    @Test
    void findByLocationAndSportAndName_whenNoSportMatchAndLocationMatchAndNoNameMatch_returnNoUser() throws IOException {
        Location nelson = new Location(null, null, null, "Nelson", null, "NZ");
        locationRepository.save(nelson);
        User nelsonRugby = generateRandomUsers.createRandomUser();
        nelsonRugby.setLocation(nelson);
        nelsonRugby.setFavoriteSports(List.of(new Sport("Rugby")));
        User nelsonHockey = generateRandomUsers.createRandomUser();
        nelsonHockey.setLocation(nelson);
        nelsonHockey.setFavoriteSports(List.of(new Sport("Hockey")));
        User aucklandHockey = generateRandomUsers.createRandomUser();
        Location auckland = new Location(null, null, null, "Auckland", null, "NZ");
        aucklandHockey.setLocation(auckland);
        aucklandHockey.setFavoriteSports(List.of(new Sport("Hockey")));
        User christchurchHockey = generateRandomUsers.createRandomUser();
        christchurchHockey.setFavoriteSports(List.of(new Sport("Hockey")));

        userRepository.save(nelsonHockey);
        userRepository.save(nelsonRugby);
        userRepository.save(aucklandHockey);
        userRepository.save(christchurchHockey);

        Page<User> returnedUsers = userRepository.findUserByFilteredLocationsAndSports(PageRequest.ofSize(500), List.of(nelson.getCity()), List.of("Football"), "SENG");
        assertTrue(returnedUsers.isEmpty());
    }

    @Test
    void findByLocationAndSportAndName_whenNoSportMatchAndNoLocationMatchAndNoNameMatch_returnNoUser() throws IOException {
        Location nelson = new Location(null, null, null, "Nelson", null, "NZ");
        locationRepository.save(nelson);
        User nelsonRugby = generateRandomUsers.createRandomUser();
        nelsonRugby.setLocation(nelson);
        nelsonRugby.setFavoriteSports(List.of(new Sport("Rugby")));
        User nelsonHockey = generateRandomUsers.createRandomUser();
        nelsonHockey.setLocation(nelson);
        nelsonHockey.setFavoriteSports(List.of(new Sport("Hockey")));
        User aucklandHockey = generateRandomUsers.createRandomUser();
        Location auckland = new Location(null, null, null, "Auckland", null, "NZ");
        aucklandHockey.setLocation(auckland);
        aucklandHockey.setFavoriteSports(List.of(new Sport("Hockey")));
        User christchurchHockey = generateRandomUsers.createRandomUser();
        christchurchHockey.setFavoriteSports(List.of(new Sport("Hockey")));

        userRepository.save(nelsonHockey);
        userRepository.save(nelsonRugby);
        userRepository.save(aucklandHockey);
        userRepository.save(christchurchHockey);

        Location sanJose = new Location(null, null, null, "San Jose", null, "Costa Rica");

        Page<User> returnedUsers = userRepository.findUserByFilteredLocationsAndSports(PageRequest.ofSize(500), List.of(sanJose.getCity()), List.of("Football"), "SENG");
        assertTrue(returnedUsers.isEmpty());
    }

    /*
        The following tests exist for the `UserService.SORT_BY_LAST_AND_FIRST_NAME` attribute
    */
    @Test
    void findAll_usersAreSortedByLastName() throws IOException {
        User leonAardvark = createUserWithName("Leon","Aardvark");
        User adamCharlie = createUserWithName("Adam","Charlie");
        User cheeseDolphin = createUserWithName("Cheese","Dolphin");

        userRepository.save(adamCharlie);
        userRepository.save(cheeseDolphin);
        userRepository.save(leonAardvark);

        Pageable pageable = PageRequest.of(0, 10, UserService.SORT_BY_LAST_AND_FIRST_NAME);
        var returnedUsers = userRepository.findAll(pageable);

        assertEquals(3, returnedUsers.getNumberOfElements());
        assertEquals(List.of(leonAardvark, adamCharlie, cheeseDolphin), returnedUsers.toList());
    }

    @Test
    void findAll_usersAreSortedByLastNameThenFirstName() throws IOException {
        User leonAardvark = createUserWithName("Leon","Aardvark");
        User adamAardvark = createUserWithName("Adam","Aardvark");
        User cheeseAardvark = createUserWithName("Cheese","Aardvark");

        userRepository.save(adamAardvark);
        userRepository.save(cheeseAardvark);
        userRepository.save(leonAardvark);

        Pageable pageable = PageRequest.of(0, 10, UserService.SORT_BY_LAST_AND_FIRST_NAME);
        var returnedUsers = userRepository.findAll(pageable);

        assertEquals(3, returnedUsers.getNumberOfElements());
        assertEquals(List.of(adamAardvark, cheeseAardvark, leonAardvark), returnedUsers.toList());

    }

    @Test
    void findAll_usersAreSortedByLastNameThenFirstNameCaseInsensitive() throws IOException {
        // Items are lowercase in this pattern so we'll know if it puts A-Z/a-z first, and 
        // therefore isn't case insensitive
        User andyLowerAardvark = createUserWithName("Andy", "aardvark");
        User mattAardvark = createUserWithName("Matt", "Aardvark");
        User adamBob = createUserWithName("Adam", "Bob");
        User cheeseLowerBob = createUserWithName("Cheese", "bob");
        User leonBob = createUserWithName("Leon", "Bob");
        User katBurgler = createUserWithName("Kat", "Burgler");
        User neilLowerBurgler = createUserWithName("Neil", "burgler");

        // Insert in incorrect order
        userRepository.save(andyLowerAardvark);
        userRepository.save(adamBob);
        userRepository.save(cheeseLowerBob);
        userRepository.save(katBurgler);
        userRepository.save(leonBob);
        userRepository.save(mattAardvark);
        userRepository.save(neilLowerBurgler);

        Pageable pageable = PageRequest.of(0, 10, UserService.SORT_BY_LAST_AND_FIRST_NAME);
        var returnedUsers = userRepository.findAll(pageable);

        assertEquals(7, returnedUsers.getNumberOfElements());
        assertEquals(List.of(andyLowerAardvark,
                mattAardvark,
                adamBob,
                cheeseLowerBob,
                leonBob,
                katBurgler,
                neilLowerBurgler), returnedUsers.toList());
    }

    /** This test is a copy of the above method, but calling a different method.
     *  As both methods sort with the same system, if the above tests pass this should pass,
     *  but you never know...
     */
    @Test
    void findBySportAndName_usersAreSortedByLastNameThenFirstNameCaseInsensitive() throws IOException {
        // Items are lowercase in this pattern so we'll know if it puts A-Z/a-z first, and 
        // therefore isn't case insensitive
        User andyLowerAardvark = createUserWithName("Andy", "aardvark");
        User mattAardvark = createUserWithName("Matt", "Aardvark");
        User adamBob = createUserWithName("Adam", "Bob");
        User cheeseLowerBob = createUserWithName("Cheese", "bob");
        User leonBob = createUserWithName("Leon", "Bob");
        User katBurgler = createUserWithName("Kat", "Burgler");
        User neilLowerBurgler = createUserWithName("Neil", "burgler");

        // Insert in incorrect order
        userRepository.save(andyLowerAardvark);
        userRepository.save(adamBob);
        userRepository.save(cheeseLowerBob);
        userRepository.save(katBurgler);
        userRepository.save(leonBob);
        userRepository.save(mattAardvark);
        userRepository.save(neilLowerBurgler);

        Pageable pageable = PageRequest.of(0, 10, UserService.SORT_BY_LAST_AND_FIRST_NAME);
        var returnedUsers = userRepository.findUserByFilteredLocationsAndSports(pageable, List.of(), List.of(), "");

        assertEquals(7, returnedUsers.getNumberOfElements());
        assertEquals(List.of(andyLowerAardvark,
                mattAardvark,
                adamBob,
                cheeseLowerBob,
                leonBob,
                katBurgler,
                neilLowerBurgler), returnedUsers.toList());
    }
}