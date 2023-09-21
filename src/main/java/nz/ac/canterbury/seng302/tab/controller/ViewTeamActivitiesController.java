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
            @RequestParam(value = "teamID") Long teamID,
            HttpServletRequest request) {
        logger.info("/viewTeamActivities");
        model.addAttribute("httpServletRequest", request);
        Team selectedTeam = teamService.getTeam(teamID);

        if (selectedTeam != null) {
            model.addAttribute("displayName", selectedTeam.getName());
            model.addAttribute("selectedTeam", selectedTeam);
            logger.info("TEAM IS: " + selectedTeam.getName());
        } else {
            logger.error("No Team Found");
            return "redirect:./home";
        }

        Optional<User> user = userService.getCurrentUser();
        if (user.isEmpty()) {
            logger.error("No User Found");
            return "redirect:./home";
        }

        Integer totalPages = activityService.getAllTeamActivitiesPage(selectedTeam, pageNo, maxPageSize).getTotalPages();
        // If page number outside of page range then reloads page with appropriate number
        if (pageNo < 1 || pageNo > totalPages && totalPages > 0) {
            pageNo = pageNo < 1 ? 1: totalPages;
            return "redirect:/viewTeamActivities?page=" + pageNo +"&teamID="+teamID;
        }

        Page<Activity> page = activityService.getAllTeamActivitiesPage(selectedTeam, pageNo, maxPageSize);
        List<Activity> listActivities = page.getContent();
        model.addAttribute("httpServletRequest", request);
        model.addAttribute("page", pageNo);
        model.addAttribute("totalPages", page.getTotalPages() == 0 ? 1 : page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("activities", listActivities);
        model.addAttribute("currentUser", user);
        logger.info("page number" + pageNo);
        logger.info("total pages" + page.getTotalPages());

        return "viewActivities";
    }
}
