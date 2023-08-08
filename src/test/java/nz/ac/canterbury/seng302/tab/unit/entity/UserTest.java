package nz.ac.canterbury.seng302.tab.unit.entity;

import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.enums.AuthorityType;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class UserTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @BeforeEach
    public void beforeEach() {
        userRepository.deleteAll();
    }

    @Test
    public void GivenUserUpdatesPassword_ThenPasswordUpdatedSuccessfully() throws IOException {

        User user = new User("Alice", "Smith", new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime(), "alice@test.com", "Password123!", new Location(null, null, null, "Christchurch", null, "New Zealand"));
        userService.updateOrAddUser(user);
        user.setPassword("Hello69!");
        assertEquals("Hello69!", user.getPassword());
    }

    @Test
    public void grantAuthorityGetGrantedAuthTest() {
        User user = new User("Alice", "Smith", new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime(), "alice@test.com", "Password123!", new Location(null, null, null, "Christchurch", null, "New Zealand"));
        user.grantAuthority(AuthorityType.FEDERATION_MANAGER);
        Assertions.assertTrue(user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_FEDERATION_MANAGER")));
    }

}
