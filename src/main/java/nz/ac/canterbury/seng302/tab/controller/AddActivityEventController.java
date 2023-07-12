package nz.ac.canterbury.seng302.tab.controller;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.tab.entity.Activity;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.enums.ActivityType;
import nz.ac.canterbury.seng302.tab.service.ActivityService;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;


import java.net.MalformedURLException;
import java.util.List;
import java.util.Optional;

@Controller
public class AddActivityEventController {

    @Autowired
    TeamService teamService;

    @Autowired
    UserService userService;

    @Autowired
    ActivityService activityService;

    Logger logger = LoggerFactory.getLogger(CreateActivityController.class);

    /**
     * Handles creating an event in the activity
     * @param activityId the id of activity the event will be added to 
     * @param activityType the type of actvity
     * @param teamId the ID of the team, (optional)
     */
    @GetMapping("/addActivityEvent")
    public String addActivityEvent(
            @RequestParam(name = "activityID", defaultValue = "-1") long activityId,
            @RequestParam(name = "activityType", required = false) ActivityType activityType,
            @RequestParam(name = "team", defaultValue = "-1") long teamId,
            Model model,
            HttpServletRequest request) {
        logger.info("GET /addActivityEvent");

        Activity activity = activityService.findActivityById(activityId);

        if (activity == null) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(404));
        }
        
        model.addAttribute("activity", activity);
        
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

        return "addActivityEvent";
    }
}
