package nz.ac.canterbury.seng302.tab.unit.service;

import nz.ac.canterbury.seng302.tab.config.ThreadPoolTaskSchedulerConfig;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.enums.AuthorityType;
import nz.ac.canterbury.seng302.tab.mail.EmailService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.thymeleaf.spring6.SpringTemplateEngine;

import jakarta.mail.internet.MimeMessage;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DataJpaTest
@Import({UserService.class, ThreadPoolTaskSchedulerConfig.class, EmailService.class})
public class UserServiceTest {

    Logger logger = LoggerFactory.getLogger(UserServiceTest.class);

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private JavaMailSender mockJavaMailSender;
  
    // @Autowired
    // private EmailService emailService;


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
