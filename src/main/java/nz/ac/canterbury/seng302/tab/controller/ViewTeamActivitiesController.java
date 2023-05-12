package nz.ac.canterbury.seng302.tab.controller;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.tab.entity.Activity;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
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
import org.springframework.data.domain.Page;

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

    private static int maxPageSize = 10;

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
    public String viewTeamActivities(@RequestParam(value = "page", defaultValue = "-1") int pageNo, Model model,
            @RequestParam(value = "teamID", required = false) Long teamID,
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
        Page<Activity> teamActivities = activityService.getAllTeamActivitiesPage(selectedTeam, pageNo, maxPageSize);

        // If page number outside of page then reloads page with appropriate number
        if (pageNo < 1 || pageNo > teamActivities.getTotalPages()
                && teamService.findPaginated(pageNo, maxPageSize).getTotalPages() > 0) {
            pageNo = pageNo < 1 ? 1 : teamActivities.getTotalPages();
            return "redirect:/view-teams?page=" + pageNo;
        }

        logger.info("activity is: " + activity.getDescription());
        logger.info("team activities is " + teamActivities);
        model.addAttribute("page", pageNo);
        model.addAttribute("totalPages", teamActivities.getTotalPages());
        model.addAttribute("totalItems", teamActivities.getTotalElements());

        model.addAttribute("displayPicture", user.get().getPictureString());
        model.addAttribute("firstName", user.get().getFirstName());
        model.addAttribute("lastName", user.get().getLastName());
        model.addAttribute("teamID", teamID);
        model.addAttribute("selectedTeam", selectedTeam);
        model.addAttribute("isUserManager", teamService.isUserManagerOfTeam(user.get().getUserId(), teamId));
        model.addAttribute("activities", teamActivities);

        logger.info("boolean manager is: " + teamService.isUserManagerOfTeam(user.get().getUserId(), teamId));

        return "viewActivities";
    }
}
