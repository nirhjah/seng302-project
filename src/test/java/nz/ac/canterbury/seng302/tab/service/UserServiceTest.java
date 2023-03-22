package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.*;

import static org.springframework.test.util.AssertionErrors.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DataJpaTest
public class UserServiceTest {

    @Autowired
    UserRepository userRepository;

    Calendar calendar = new GregorianCalendar(2000, Calendar.JANUARY, 10);

    UserServiceTest() {
        calendar.setTimeZone(TimeZone.getTimeZone("Pacific/Auckland"));
        Date date = calendar.getTime();
        user1 = new User("Test", "User", date, "test@test.com", "password", new ArrayList<>(), "Christchurch");
        user2 = new User("Something", "Else", date, "agian@test.com", "password", new ArrayList<>(), "Nelson");
        user3 = new User("Another", "Test", date, "abc123@test.com", "password", new ArrayList<>(), "Christchurch");
        user4 = new User("John", "Smith", date, "something@test.com", "password", new ArrayList<>(), "Auckland");
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
        List<User> matchingUsers = userRepository.findUsersByLocationIn(new ArrayList<>());
        System.out.println(matchingUsers);
        assertEquals("", matchingUsers, Arrays.asList(user1, user2, user3, user4));

    }

    @Test
    public void filterUsersWithCity_chchSelected_showAllChchUsers() {
        List<User> matchingUsers = userRepository.findUsersByLocationIn(Arrays.asList("Christchurch"));
        System.out.println(matchingUsers);
        assertEquals("", matchingUsers, Arrays.asList(user1, user3));
    }

    @Test
    public void filterUsersWithCity_wellingtonSelected_showNoUsers() {
        List<User> matchingUsers = userRepository.findUsersByLocationIn(Arrays.asList("Wellington"));
        assertEquals("", matchingUsers, new ArrayList<>());
    }
}
