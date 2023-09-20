package nz.ac.canterbury.seng302.tab.controller;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.servlet.http.HttpServletResponse;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.form.JoinTeamForm;
import nz.ac.canterbury.seng302.tab.service.ActivityService;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Calendar;
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
        model.addAttribute("joinTeamForm", new JoinTeamForm());

        Optional<User> optUser = userService.getCurrentUser();
        if (optUser.isPresent()) {
            int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
            String welcomeText;
            if (hour>=12) {
                welcomeText = "Good Afternoon, " + optUser.get().getFirstName();
            } else {
                welcomeText = "Good Morning, " + optUser.get().getFirstName();
            }

            if (model.asMap().containsKey("formBindingResult"))
            {
                model.addAttribute("org.springframework.validation.BindingResult.joinTeamForm",
                        model.asMap().get("formBindingResult"));
            }

            model.addAttribute("userTeams", optUser.get().getJoinedTeams());
            model.addAttribute("userPersonalActivities", activityService.getAllFuturePersonalActivitiesForUser(optUser.get()));
            model.addAttribute("userTeamActivities", activityService.getAllFutureTeamActivitiesForUser(optUser.get()));
            model.addAttribute("welcomeString", welcomeText);
            model.addAttribute("user", optUser.get());
        }

        return "homeForm";
    }

    /**
     * Post method to handle joining teams
     * @param token - unique team token
     * @param joinTeamForm form with token
     * @param bindingResult result of form
     * @param model mapping to pass to HTML
     * @param httpServletResponse response
     * @param request request
     * @param redirectAttributes redirect attributes
     * @return
     */
    @PostMapping("/home")
    public String joinTeamsForm(
            @RequestParam("token") String token,
            @Validated JoinTeamForm joinTeamForm,
            BindingResult bindingResult,
            Model model,
            HttpServletResponse httpServletResponse, HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        model.addAttribute("token", token);
        model.addAttribute("httpServletRequest", request);

        User user = userService.getCurrentUser().get();
        Optional<Team> team = teamService.findByToken(token);
        if (team.isEmpty()) {
            bindingResult.addError(new FieldError("joinTeamForm", "token", "Token is invalid"));
        }

        if (bindingResult.hasErrors()) {
            httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            redirectAttributes.addFlashAttribute("tokenInvalid", "Leave Modal Open");
            redirectAttributes.addFlashAttribute("formBindingResult", bindingResult);

            return "redirect:/home";
        }

        if (team.isPresent()) {
            userService.userJoinTeam(user, team.get());
        }

        return "homeForm";
    }

}

