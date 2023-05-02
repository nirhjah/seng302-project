package nz.ac.canterbury.seng302.tab.authentication;

import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import org.springframework.stereotype.Component;

/**
 * Class that should be called two hours after a user is created
 * to check if they;ve verified their email and delete the user if not
 */
public class EmailVerification implements Runnable{

    private final User user;

    private final UserRepository userRepository;

    public EmailVerification(User user, UserRepository userRepository) {
        this.user = user;
        this.userRepository = userRepository;
    }

    @Override
    public void run() {
        if (!user.getEmailConfirmed()) {
            userRepository.delete(user);
        }
    }
}
