package nz.ac.canterbury.seng302.tab.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.helper.GenerateRandomUsers;


@SpringBootTest
@AutoConfigureTestDatabase
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SportRepository sportRepository;

    @Autowired
    private GenerateRandomUsers generateRandomUsers;

    @AfterEach
    void afterEach() {
        sportRepository.deleteAll();
        userRepository.deleteAll();
    }

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
    void findByNameOrSport_whenParamsAreNull_returnAllUsers() throws IOException {
        final int N_USERS = 10;
        // Populate database
        for (int i = 0; i < N_USERS; i++) {
            userRepository.save(generateRandomUsers.createRandomUser());
        }

        Page<User> returnedUsers = userRepository.findAllFiltered(PageRequest.ofSize(N_USERS), List.of(), null);

        assertEquals(N_USERS, returnedUsers.getSize());
    }

    @Test
    void findByNameOrSport_whenFirstNameIsMatched_returnCorrectUsers() throws IOException {
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
    void findByNameOrSport_whenLastNameIsMatched_returnCorrectUsers() throws IOException {
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

    /**
     * NOTE: Right now, the database doesn't care about name order.
     * e.g. searching "Davis Miles" matches User(Miles, Davis).
     * This test purposefully provides them in the correct order, so that isn't tested... */
    @Test
    void findByNameOrSport_whenFirstOrLastNameIsMatched_returnCorrectUsers() throws IOException {
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
}