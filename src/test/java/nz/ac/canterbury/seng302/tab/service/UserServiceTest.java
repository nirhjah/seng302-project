package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.util.*;

import static org.springframework.test.util.AssertionErrors.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DataJpaTest
@Import(UserService.class)
public class UserServiceTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    LocalDate date = LocalDate.of(2000, 10, 10);


    UserServiceTest() {
        user1 = new User("Test", "User", date, "test@test.com", "password", Collections.emptyList(), "Christchurch");
        user2 = new User("Something", "Else", date, "agian@test.com", "password", Collections.emptyList(), "Nelson");
        user3 = new User("Another", "Test", date, "abc123@test.com", "password", Collections.emptyList(), "Christchurch");
        user4 = new User("John", "Smith", date, "something@test.com", "password", Collections.emptyList(), "Auckland");
    }

    User user1;
    User user2;
    User user3;
    User user4;

    @BeforeAll
    public void addTestUsers() {
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        userRepository.save(user4);
    }

    @Test
    public void filterUsersWithCity_noCitiesSelected_showAllUsers() {
        List<User> matchingUsers = userService.filterUsersByLocation(Collections.emptyList());
        assertEquals("", Arrays.asList(user1, user2, user3, user4), matchingUsers);

    }

    @Test
    public void filterUsersWithCity_chchSelected_showAllChchUsers() {
        List<User> matchingUsers = userService.filterUsersByLocation(List.of("Christchurch"));
        assertEquals("", Arrays.asList(user1, user3), matchingUsers);
    }

    @Test
    public void filterUsersWithCity_wellingtonSelected_showNoUsers() {
        List<User> matchingUsers = userService.filterUsersByLocation(List.of("Wellington"));
        assertEquals("", Collections.emptyList(), matchingUsers);
    }
}
