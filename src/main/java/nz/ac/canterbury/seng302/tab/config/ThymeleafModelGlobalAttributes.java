package nz.ac.canterbury.seng302.tab.config;

import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.service.TeamService;
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
    private TeamService teamService;

    public ThymeleafModelGlobalAttributes(UserService userService, TeamService teamService) {
        this.userService = userService;
        this.teamService = teamService;
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

    /**
     * Adds the list of all teams to the model, if the user is currently logged in.
     * 
     * This is used for the navBar, which has a dropdown of all teams.
     */
    @ModelAttribute("G_navTeams")
    public List<Team> populateNavBarTeams() {
        if (userService.getCurrentUser().isPresent()) {
            return teamService.getTeamList();
        } else {
            // Logged out users won't see this anyway
            return List.of();
        }
    }
}
