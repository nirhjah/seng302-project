package nz.ac.canterbury.seng302.tab.unit.service;

import nz.ac.canterbury.seng302.tab.authentication.UserPasswordEncoder;
import nz.ac.canterbury.seng302.tab.config.ThreadPoolTaskSchedulerConfig;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.enums.AuthorityType;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;

@DataJpaTest
@Import({UserService.class, ThreadPoolTaskSchedulerConfig.class, UserPasswordEncoder.class})
public class UserServiceTest {

    Logger logger = LoggerFactory.getLogger(UserServiceTest.class);

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;



    Location location = new Location("1 Test Lane", "", "Ilam", "Christchurch", "8041", "New Zealand");
    Location location2 = new Location("1 Test Lane", "", "Ilam", "Christchurch", "8041", "New Zealand");
    Location location3 = new Location("1 Test Lane", "", "Ilam", "Christchurch", "8041", "New Zealand");


    // @BeforeEach
    // public void beforeAll() throws IOException {
    //     userRepository.deleteAll();
    // }
    //
  //
    @Test
    void testGettingAllUsersWhoArentAFedMan() throws Exception {
        userRepository.deleteAll();
        User user = new User("Hee", "Account", "tab@gmail.com", "password", location);
        userRepository.save(user);
        User user2 = new User("test", "test", "test@gmail.com", "1", location);
        userRepository.save(user2);

        user.grantAuthority(AuthorityType.FEDERATION_MANAGER);
        userService.updateOrAddUser(user);
        
        Pageable pageable = PageRequest.of(0, 10);
        List<User> actualUsers = userService.getAllUsersNotFedMans(pageable).getContent();
        
        assertFalse(actualUsers.stream().anyMatch(u -> u == user));
    }


}
