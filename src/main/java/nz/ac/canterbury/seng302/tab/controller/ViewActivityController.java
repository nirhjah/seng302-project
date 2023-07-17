package nz.ac.canterbury.seng302.tab.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import nz.ac.canterbury.seng302.tab.entity.Activity;
import nz.ac.canterbury.seng302.tab.entity.Fact.Fact;
import nz.ac.canterbury.seng302.tab.entity.Fact.Goal;
import nz.ac.canterbury.seng302.tab.entity.Fact.Substitution;
import nz.ac.canterbury.seng302.tab.enums.FactType;
import nz.ac.canterbury.seng302.tab.form.CreateEventForm;
import nz.ac.canterbury.seng302.tab.service.ActivityService;
import nz.ac.canterbury.seng302.tab.service.FactService;
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

    @Autowired
    public ViewActivityController(UserService userService, ActivityService activityService, TeamService teamService,FactService factService) {
        this.userService = userService;
        this.activityService = activityService;
        this.teamService = teamService;
        this.factService=factService;
    }



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
            HttpServletRequest request,
            CreateEventForm createEventForm) {

        Activity activity = activityService.findActivityById(activityID);
        if (activity == null) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(404));
        }


        List<Fact> activityFacts = factService.getAllFactsForActivity(activity);
        List<Substitution> activitySubstitutions = new ArrayList<>();
        List<Goal> activityGoals = new ArrayList<>();

        for (Object fact : activityFacts) {
            if(fact instanceof Substitution) {
                activitySubstitutions.add((Substitution) fact);

            } else if (fact instanceof Goal) {
                activityGoals.add((Goal) fact);
            }

        }
        

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
        model.addAttribute("possibleFactTypes", FactType.values());

        return "viewActivity";
    }

    @PostMapping("/view-activity")
    public String createEvent(
            @RequestParam(name = "actId", defaultValue = "-1") long actId, 
            @RequestParam(name = "factType") FactType factType,
            @RequestParam(name = "description") String description,
            @RequestParam(name = "time") String time,
            HttpServletRequest request) {

        // create the new fact of facttype 
        logger.info("POST /view-activity");
        logger.info("got the desc " + description);
        logger.info(factType.name());
        logger.info(String.format("got the act id: %s", actId));

        // add the fact to the game 
            
        Activity activity = activityService.findActivityById(actId);

        Fact fact = new Fact(description, time, activity);
        List<Fact> factList= new ArrayList<>();
        factList.add(fact);
        
        // activity.addFactToFactList(fact);
        activity.addFactList(factList);
        activity = activityService.updateOrAddActivity(activity);
        
        return String.format("redirect:./view-activity?activityID=%s", actId);
    }



}
