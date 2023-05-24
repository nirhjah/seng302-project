package nz.ac.canterbury.seng302.tab.config;

import java.util.Optional;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.service.UserService;

/**
 * A class for adding model values to every template.
 * 
 * If you're going to add values, prefix them with "G_"
 *   so they don't collide with a controller's attributes
 */
@ControllerAdvice
public class ThymeleafModelGlobalAttributes {

    private UserService userService;

    public ThymeleafModelGlobalAttributes(UserService userService) {
        this.userService = userService;
    }

    /**
     * Adds the currently logged in user to the model.
     * 
     * This is used for the navBar, which displays the current user's first/last name & profile pic
     */
    @ModelAttribute("G_optUser")
    public Optional<User> populateUser() {
        return userService.getCurrentUser();
    }
}
