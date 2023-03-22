package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.springframework.test.util.AssertionErrors.assertEquals;

@SpringBootTest
public class UserServiceTest {

    @MockBean
    UserRepository userRepository;

    @Autowired
    UserService userService;

    User user1 = new User("Test", "User", new Date(2000, 10, 10), "test@test.com", "password", new ArrayList<>(), "Christchurch");
    User user2 = new User("Something", "Else", new Date(2000, 10, 10), "test@test.com", "password", new ArrayList<>(), "Nelson");
    User user3 = new User("Another", "Test", new Date(2000, 10, 10), "test@test.com", "password", new ArrayList<>(), "Christchurch");
    User user4 = new User("John", "Smith", new Date(2000, 10, 10), "test@test.com", "password", new ArrayList<>(), "Auckland");

    @BeforeAll
    public void addTestUsers() {

        Mockito.when(userRepository.findUsersByLocation(new ArrayList<>())).thenReturn(Arrays.asList(user1, user2, user3, user4));
        Mockito.when(userRepository.findUsersByLocation(Arrays.asList("Christchurch"))).thenReturn(Arrays.asList(user1, user2));
        Mockito.when(userRepository.findUsersByLocation(Arrays.asList("Wellington"))).thenReturn(new ArrayList<>());
    }

    @BeforeEach
    public void testSetUp() { userRepository.deleteAll();}

    @Test
    public void filterUsersWithCity_noCitiesSelected_showAllUsers() {
        List<User> matchingUsers = userService.filterUsersByLocation(new ArrayList<>());
        assertEquals("", matchingUsers, Arrays.asList(user1, user2, user3, user4));

    }

    @Test
    public void filterUsersWithCity_chchSelected_showAllChchUsers() {
        List<User> matchingUsers = userService.filterUsersByLoction(Arrays.asList("Christchurch"));
        assertEquals("", matchingUsers, Arrays.asList(user1, user3));
    }

    @Test
    public void filterUsersWithCity_wellingtonSelected_showNoUsers() {
        List<User> matchingUsers = userService.filterUsersByLoction(Arrays.asList("Wellington"));
        assertEquals("", matchingUsers, new ArrayList<>());
    }
}
