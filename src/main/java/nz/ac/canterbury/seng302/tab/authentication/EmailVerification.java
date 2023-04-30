package nz.ac.canterbury.seng302.tab.authentication;

import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;

public class EmailVerification implements Runnable{

    private final User user;

    private final UserRepository userRepository;

    public EmailVerification(User user, UserRepository userRepository) {
        this.user = user;
        this.userRepository = userRepository;
    }

    @Override
    public void run() {
        if (!user.isEmailConfirmed()) {
            userRepository.delete(user);
        }
    }
}
