package nz.ac.canterbury.seng302.tab.controller;

import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import nz.ac.canterbury.seng302.tab.service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Calendar;
import java.util.GregorianCalendar;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class ViewAllUsersControllerTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    private static final Sort sort = Sort.by(
            new Sort.Order(Sort.Direction.ASC, "lastName"),
            new Sort.Order(Sort.Direction.ASC, "firstName")
    );

    /**
     * Tests that the order the users are displayed in is correct - alphabetically
     */
    @Test
    public void testUserListOrder() {
        User user1 = new User("John", "Doe", new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime(), "johndoe@example.com", "password");
        User user2 = new User("Jane", "Doe", new GregorianCalendar(1980, Calendar.JANUARY, 1).getTime(), "janedoe@example.com", "password");

        userService.updateOrAddUser(user1);
        userService.updateOrAddUser(user2);

        var pageable = PageRequest.of(0, 10, sort);

        var userList = userService.getPaginatedUsers(pageable);

        assertEquals(userList.size(), 2);
        assertEquals(userList.get(0), user2);
        assertEquals(userList.get(1), user1);
    }
}
