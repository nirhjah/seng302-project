package nz.ac.canterbury.seng302.tab.controller;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.tab.entity.Activity;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
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

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
public class ViewActivitiesController {
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
     * @param pageNo integer corresponding page to be displayed
     * @param model  (map-like) representation of name, language and isJava boolean for use in thymeleaf
     * @return thymeleaf viewAllTeams
     */
    @GetMapping("/view-activities")
    public String viewPageOfActivities(@RequestParam(value = "page", defaultValue = "-1") int pageNo,
                                       Model model, HttpServletRequest request) throws IOException {
        Optional<User> user = userService.getCurrentUser();
        User currentUser = user.get();

        Pageable pageable = PageRequest.of(0, maxPageSize);
        Integer totalPages = activityService.getPaginatedActivities(pageable,currentUser).getTotalPages();
        // If page number outside of page range then reloads page with appropriate number
        if (pageNo < 1 || pageNo > totalPages && totalPages > 0) {
            pageNo = pageNo < 1 ? 1: totalPages;
            return "redirect:/view-activities?page=" + pageNo;
        }

        logger.info("GET /view-teams");
        pageable = PageRequest.of(pageNo-1, maxPageSize);
        Page<Activity> page = activityService.getPaginatedActivities(pageable,currentUser);
        System.out.println(page.getContent());
        List<Activity> listActivities = page.getContent();
        model.addAttribute("firstName", user.get().getFirstName());
        model.addAttribute("lastName", user.get().getLastName());
        model.addAttribute("displayPicture", user.get().getPictureString());
        model.addAttribute("navTeams", teamService.getTeamList());
        model.addAttribute("httpServletRequest", request);
        model.addAttribute("page", pageNo);
        model.addAttribute("totalPages", page.getTotalPages() == 0 ? 1 : page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("activities", listActivities);
        logger.info("page number" + pageNo);
        logger.info("total pages" + page.getTotalPages());

        return "viewActivities";
    }

}

