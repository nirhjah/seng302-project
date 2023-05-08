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

import java.util.Optional;

@Controller
public class ViewActivities {
    private static int maxPageSize = 10;
    Logger logger = LoggerFactory.getLogger(ViewAllTeamsController.class);

    @Autowired
    private UserService userService;
    @Autowired
    private ActivityService activityService;

    /**
     * Gets viewAllActivities doc with required attributes. Reroutes if page out of available range
     * @param pageNo integer corresponding page to be displayed
     * @param model  (map-like) representation of name, language and isJava boolean for use in thymeleaf
     * @return thymeleaf viewAllTeams
     */
    public String viewPageOfActivities(@RequestParam(name = "name", required = false, defaultValue = "-1") int userId,
                                @RequestParam(value = "page", defaultValue = "-1") int pageNo,
                                Model model, HttpServletRequest request) {
        if (pageNo < 1) {
            pageNo = 1;
        }
        Optional<User> user = userService.getCurrentUser();
        User currentUser = user.get();
        Pageable pageable = PageRequest.of(pageNo - 1, maxPageSize);
        Page<Activity> listTeams = activityService.getPaginatedActivities(pageable, currentUser);
        if (pageNo > listTeams.getTotalPages() && listTeams.getTotalPages() > 0) {
            pageNo = listTeams.getTotalPages();
              pageable = PageRequest.of(pageNo - 1, maxPageSize);
            listTeams = activityService.getPaginatedActivities(pageable, currentUser);
        }
        // If page number outside of page range then reloads page with appropriate number
//        if (pageNo < 1 || pageNo > activityService.getPaginatedActivities(pageNo, maxPageSize).getTotalPages() && activityService.findPaginated(pageNo, maxPageSize).getTotalPages() > 0) {
//            pageNo = pageNo < 1 ? 1: activityService.findPaginated(pageNo, maxPageSize).getTotalPages();
//            return "redirect:/view-teams?page=" + pageNo;
//        }

        model.addAttribute("page", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("displayTeams", listTeams);
    }

}
