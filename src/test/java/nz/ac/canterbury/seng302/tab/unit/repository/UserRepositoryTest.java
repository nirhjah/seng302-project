package nz.ac.canterbury.seng302.tab.unit.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import nz.ac.canterbury.seng302.tab.entity.Sport;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.helper.GenerateRandomUsers;


@DataJpaTest
@Import(GenerateRandomUsers.class)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    // Helper class to make random users.
    @Autowired
    private GenerateRandomUsers generateRandomUsers;

    @Test
    void findByNameOrSport_whenParamsAreEmpty_returnAllUsers() throws IOException {
        final int N_USERS = 10;
        // Populate database
        for (int i = 0; i < N_USERS; i++) {
            userRepository.save(generateRandomUsers.createRandomUser());
        }

        Page<User> returnedUsers = userRepository.findAllFiltered(PageRequest.ofSize(N_USERS), List.of(), "");

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

        Page<User> returnedUsers = userRepository.findAllFiltered(PageRequest.ofSize(N_VALID_USERS + N_INVALID_USERS), List.of(), SEARCH);

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

        Page<User> returnedUsers = userRepository.findAllFiltered(PageRequest.ofSize(N_VALID_USERS + N_INVALID_USERS), List.of(), SEARCH);

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

        Page<User> returnedUsers = userRepository.findAllFiltered(PageRequest.ofSize(N_INVALID_USERS), List.of(), SEARCH);

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
        

        Page<User> returnedUsers = userRepository.findAllFiltered(PageRequest.ofSize(N_VALID_USERS + N_INVALID_USERS), List.of(), SEARCH);

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

        Page<User> returnedUsers = userRepository.findAllFiltered(PageRequest.ofSize(5), List.of(CORRECT_SPORT), "");

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

        Page<User> returnedUsers = userRepository.findAllFiltered(PageRequest.ofSize(500), List.of(S_RUGBY), "");

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
        Page<User> returnedUsers = userRepository.findAllFiltered(PageRequest.ofSize(500), List.of(S_RUGBY, S_HOCKEY), "");

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
        user = generateRandomUsers.createRandomUser();
        user.setFirstName(SEARCH_NAME);
        user.setFavoriteSports(List.of(hockey));
        userRepository.save(user);
        // User with incorrect name and wrong sport
        user = generateRandomUsers.createRandomUser();
        user.setFavoriteSports(List.of(rugby));
        userRepository.save(user);
        // Search with an additional sport that no one has.
        Page<User> returnedUsers = userRepository.findAllFiltered(PageRequest.ofSize(500), List.of(S_HOCKEY), SEARCH_NAME);

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
        user = generateRandomUsers.createRandomUser();
        user.setFirstName(SEARCH_NAME);
        user.setFavoriteSports(List.of(hockey));
        userRepository.save(user);
        // User with the correct name and two sports (one correct)
        user = generateRandomUsers.createRandomUser();
        user.setFirstName(SEARCH_NAME);
        user.setFavoriteSports(List.of(hockey, rugby));
        userRepository.save(user);
        // User with incorrect name and wrong sport
        user = generateRandomUsers.createRandomUser();
        user.setFavoriteSports(List.of(rugby));
        userRepository.save(user);
        // Search with an additional sport that no one has.
        Page<User> returnedUsers = userRepository.findAllFiltered(PageRequest.ofSize(500), List.of(S_HOCKEY), SEARCH_NAME);

        assertEquals(2, returnedUsers.getNumberOfElements());
    }

    @Test
    void findBySportAndName_whenNameMatchesButNotSport_returnNoUsers() throws IOException {
        User user;
        final String SEARCH_NAME = "TDDHater";
        final String S_HOCKEY = "Hockey";
        final String S_RUGBY = "Rugby";
        Sport hockey = new Sport(S_HOCKEY);
        Sport rugby = new Sport(S_RUGBY);

        // User with the correct name and but wrong sport
        user = generateRandomUsers.createRandomUser();
        user.setFirstName(SEARCH_NAME);
        user.setFavoriteSports(List.of(rugby));
        userRepository.save(user);
        // Search with an additional sport that no one has.
        Page<User> returnedUsers = userRepository.findAllFiltered(PageRequest.ofSize(500), List.of(S_HOCKEY), SEARCH_NAME);

        assertEquals(0, returnedUsers.getNumberOfElements());
    }
    @Test
    void findBySportAndName_whenSportMatchesButNotName_returnNoUsers() throws IOException {
        User user;
        final String SEARCH_NAME = "TDDHater";
        final String S_HOCKEY = "Hockey";
        final String S_RUGBY = "Rugby";
        Sport hockey = new Sport(S_HOCKEY);
        Sport rugby = new Sport(S_RUGBY);

        // User with the wrong name and but right sport
        user = generateRandomUsers.createRandomUser();
        user.setFirstName(S_RUGBY);
        user.setFavoriteSports(List.of(rugby));
        userRepository.save(user);

        // Search with an additional sport that no one has.
        Page<User> returnedUsers = userRepository.findAllFiltered(PageRequest.ofSize(500), List.of(S_RUGBY), SEARCH_NAME);

        assertEquals(0, returnedUsers.getNumberOfElements());
    }
}