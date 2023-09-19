package nz.ac.canterbury.seng302.tab.controller;

import jakarta.servlet.http.HttpServletRequest;

import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.service.ActivityService;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.image.UserImageService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Optional;

/**
 * Spring Boot Controller class for the Home Form class.
 */
@Controller
public class HomeFormController {
    Logger logger = LoggerFactory.getLogger(HomeFormController.class);

    @Autowired
    private TeamService teamService;

    @Autowired
    private UserService userService;

    @Autowired
    private ActivityService activityService;

    @Autowired
    public HomeFormController(UserService userService, TeamService teamService, ActivityService activityService) {
        this.userService = userService;
        this.teamService = teamService;
        this.activityService = activityService;
    }

    /**
     * Redirects GET default url '/' to '/home'
     *
     * @return redirect to /home
     */
    @GetMapping("/")
    public String home() {
        logger.info("GET /homeForm");
        return "redirect:./login";
    }

    @Autowired
    private UserImageService userImageService;

    /**
     * Gets the thymeleaf page representing the /home page (a basic welcome screen with nav bar)
     *
     * @param model (map-like) representation of data to be used in thymeleaf display
     * @return thymeleaf homeForm
     */
    @GetMapping("/home")
    public String getTemplate(Model model, HttpServletRequest request) {
        logger.info("GET /homeForm");
        model.addAttribute("httpServletRequest", request);
        model.addAttribute("navTeams", teamService.getTeamList());

        Optional<User> optUser = userService.getCurrentUser();
        if (optUser.isPresent()) {
            model.addAttribute("userTeams", optUser.get().getJoinedTeams());
            model.addAttribute("userActivities", activityService.getAllFutureActivitiesForUser(optUser.get()));
            model.addAttribute("user", optUser.get());
        }

        return "homeForm";
    }
}

