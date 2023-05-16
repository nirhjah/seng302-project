package nz.ac.canterbury.seng302.tab.authentication;

import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.service.UserService;


/**
 * Class that should be called 1 hour after a reset password token is generated
 * and the token will delete/be set to null so that it is invalid.
 */
public class TokenVerification implements Runnable{

    private final User user;


    private final UserService userService;


    public TokenVerification(User user, UserService userService) {
        this.user = user;
        this.userService = userService;
    }

    @Override
    public void run() {
        user.setToken(null);
        userService.updateOrAddUser(user);
    }
}
