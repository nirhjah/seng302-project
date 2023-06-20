package nz.ac.canterbury.seng302.tab.controller;

import java.util.List;
import java.util.Optional;

import nz.ac.canterbury.seng302.tab.entity.Activity;
import nz.ac.canterbury.seng302.tab.entity.Fact.Fact;
import nz.ac.canterbury.seng302.tab.enums.FactType;
import nz.ac.canterbury.seng302.tab.service.ActivityService;
import nz.ac.canterbury.seng302.tab.service.FactService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;

/**
 * Spring Boot Controller class for the View Activity Page
 */
@Controller
public class ViewActivityController {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private TeamService teamService;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private UserService userService;

    @Autowired
    private FactService factService;

    /**
     *
     * @param model      the model to be filled
     * @param activityID the activity ID of the activity to be displayed on the page
     * @param request    http request
     * @return           view activity page
     */
    @GetMapping("/view-activity")
    public String viewActivityPage(
            Model model,
            @RequestParam(value = "activityID") Long activityID,
            HttpServletRequest request) {
        logger.info("GET /profileForm");

        Activity activity = activityService.findActivityById(activityID);
        if (activity == null) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(404));
        }

        List<Fact> activityFacts = factService.getAllFactsOfGivenTypeForActivity(FactType.FACT.ordinal(), activity);
        List<Fact> activityGoals = factService.getAllFactsOfGivenTypeForActivity(FactType.GOAL.ordinal(), activity);
        List<Fact> activitySubstitutions = factService.getAllFactsOfGivenTypeForActivity(FactType.SUBSTITUTION.ordinal(), activity);


        model.addAttribute("activity", activity);
        model.addAttribute("activitySubstitutions", activitySubstitutions);
        model.addAttribute("activityFacts", activityFacts);
        model.addAttribute("activityGoals", activityGoals);


        Optional<User> oUser = userService.getCurrentUser();
        if (oUser.isEmpty()) {
            return "redirect:login";
        }
        User user = oUser.get();

        // Rambling that's required for navBar.html
        List<Team> teamList = teamService.getTeamList();
        model.addAttribute("firstName", user.getFirstName());
        model.addAttribute("lastName", user.getLastName());
        model.addAttribute("displayPicture", user.getPictureString());
        model.addAttribute("navTeams", teamList);
        model.addAttribute("httpServletRequest", request);

        return "viewActivity";
    }


}
