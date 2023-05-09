package nz.ac.canterbury.seng302.tab.controller;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.tab.entity.Activity;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.entity.Activity.ActivityType;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import nz.ac.canterbury.seng302.tab.service.ActivityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Spring Boot Controller class for the ProfileForm
 */
@Controller
public class ViewTeamActivitiesController {

    Logger logger = LoggerFactory.getLogger(ViewTeamActivitiesController.class);
    public static long teamId;
    @Autowired
    private TeamService teamService;

    @Autowired
    private UserService userService;

    @Autowired
    private ActivityService activityService;

    /**
     * Gets form to be displayed, includes the ability to display results of
     * previous form when linked to from POST form
     *
     * @param teamID team for which the details are to be displayed
     * @param model  (map-like) representation of name, language and isJava boolean
     *               for use in thymeleaf
     * @return thymeleaf profileForm
     */
    @GetMapping("/viewTeamActivities")
    public String profileForm(Model model, @RequestParam(value = "teamID", required = false) Long teamID,
            HttpServletRequest request) {
        logger.info("/viewTeamActivities");

        // Retrieve the selected team from the list of available teams using the ID
        // If the name is null or empty, return null
        List<Team> teamList = teamService.getTeamList();
        ProfileFormController.teamId = teamID;
        model.addAttribute("httpServletRequest", request);

        Team selectedTeam;
        if (teamID != null) {
            // Find the selected team by its id
            selectedTeam = teamList.stream()
                    .filter(team -> team.getTeamId().equals(teamID))
                    .findFirst()
                    .orElse(null);
        } else {
            return "redirect:./home";
        }

        if (selectedTeam != null) {
            model.addAttribute("displayName", selectedTeam.getName());
            logger.info("TEAM IS: " + selectedTeam.getName());
            model.addAttribute("displayTeamPicture", selectedTeam.getPictureString());
        } else {
            return "redirect:./home";
        }

        Optional<User> user = userService.getCurrentUser();
        Activity activity = new Activity(Activity.ActivityType.Game, selectedTeam, "this is a test",
                LocalDateTime.of(2023, 12, 10, 14, 30), LocalDateTime.of(2023, 12, 11, 14, 30), user.get());
        activityService.updateOrAddActivity(activity);
        logger.info("activity is: " + activity.getDescription());
        List<Activity> teamActivities = activityService.getAllTeamActivities(selectedTeam);
        logger.info("team activities is " + teamActivities);
        model.addAttribute("firstName", user.get().getFirstName());
        model.addAttribute("lastName", user.get().getLastName());
        model.addAttribute("teamID", teamID);
        model.addAttribute("selectedTeam", selectedTeam);
        model.addAttribute("isUserManager", teamService.isUserManagerOfTeam(user.get().getUserId(), teamId));
        model.addAttribute("teamActivities", teamActivities);

        logger.info("boolean manager is: " + teamService.isUserManagerOfTeam(user.get().getUserId(), teamId));

        return "viewTeamActivities";
    }
}
