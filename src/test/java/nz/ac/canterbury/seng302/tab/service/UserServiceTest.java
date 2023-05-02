package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.mail.EmailService;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The reason these tests are disabled is that it takes
 * 2 HOURS to run.
 * So we disable the tests.
 */
@Disabled
@DataJpaTest
@Import(UserService.class)
class UserServiceTest {

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void givenUserCreated_test1MinuteRemoval() throws IOException, InterruptedException {
        User user = User.defaultDummyUser();
        userService.updateOrAddUser(user);
        var leighwayMilliseconds = 100; // We want to avoid failing tests due
        // to race conditions, so we add a few seconds of leighway.

        // After waiting 2 seconds, we expect the user to no longer be present.
        assertTrue(userRepository.findById(user.getUserId()).isPresent());
        Thread.sleep(UserService.getEmailTokenExpiry().toMillis() + leighwayMilliseconds);
        assertFalse(userRepository.findById(user.getUserId()).isPresent());
    }
}
