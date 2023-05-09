package nz.ac.canterbury.seng302.tab.controller;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.tab.entity.Activity;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.service.ActivityService;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
public class ViewActivities {
    private static int maxPageSize = 10;
    Logger logger = LoggerFactory.getLogger(ViewAllTeamsController.class);

    @Autowired
    private UserService userService;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private TeamService teamService;

    /**
     * Gets viewAllActivities doc with required attributes. Reroutes if page out of available range
     *
     * @param pageNum integer corresponding page to be displayed
     * @param model  (map-like) representation of name, language and isJava boolean for use in thymeleaf
     * @return thymeleaf viewAllTeams
     */
    public String viewPageOfActivities(@RequestParam(name = "name", required = false, defaultValue = "-1") int userId,
                                       @RequestParam(value = "page", defaultValue = "-1") int pageNum,
                                       Model model, HttpServletRequest request) {
        Optional<User> user = userService.getCurrentUser();
        User currentUser = user.get();

        // If page num out of range then set page num to 1 or max
        if (pageNum < 1) {
            pageNum = 1;
        }

        Pageable pageable = PageRequest.of(pageNum-1, maxPageSize);
        Page<Activity> activitiesPage = activityService.getPaginatedActivities(pageable, currentUser);

        if (pageNum > activitiesPage.getTotalPages() && activitiesPage.getTotalPages() > 0) {
            pageNum = activitiesPage.getTotalPages();
            pageable = PageRequest.of(pageNum-1, maxPageSize);
            activitiesPage = activityService.getPaginatedActivities(pageable, currentUser);
        }

        List<Activity> activityList = activitiesPage.getContent();
        model.addAttribute("firstName", user.get().getFirstName());
        model.addAttribute("lastName", user.get().getLastName());
        model.addAttribute("displayPicture", user.get().getPictureString());
        model.addAttribute("navTeams", teamService.getTeamList());
        model.addAttribute("page", pageNum);
        model.addAttribute("totalPages", activitiesPage.getTotalPages());
        model.addAttribute("totalItems", activitiesPage.getTotalElements());
        model.addAttribute("activities", activityList);
        return "viewActivities";
    }

}

