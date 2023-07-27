package nz.ac.canterbury.seng302.tab.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import nz.ac.canterbury.seng302.tab.entity.Activity;
import nz.ac.canterbury.seng302.tab.entity.Fact.Fact;
import nz.ac.canterbury.seng302.tab.entity.Fact.Goal;
import nz.ac.canterbury.seng302.tab.entity.Fact.OppositionGoal;
import nz.ac.canterbury.seng302.tab.entity.Fact.Substitution;
import nz.ac.canterbury.seng302.tab.enums.ActivityType;
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


    private void populateOther(Model model, Activity activity) {
        ActivityType type = activity.getActivityType();

        model.addAttribute("canShowScores", type == ActivityType.Friendly || type == ActivityType.Game);
        model.addAttribute("canShowPlayers", type == ActivityType.Friendly || type == ActivityType.Game);

        /*
        Different activity types have different sets of allowed FactTypes they can hold.
         */
        List<FactType> possible = switch (type) {
            case Competition, Other -> List.of(FactType.FACT);
            case Friendly, Game -> List.of(FactType.GOAL, FactType.OPPOSITION_GOAL, FactType.SUBSTITUTION, FactType.FACT);
            case Training -> List.of();
        };

        model.addAttribute("possibleFactTypes", possible);
        model.addAttribute("noFacts", possible.size() == 0);
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
        if (!activityFacts.isEmpty()){
            List<Substitution> activitySubstitutions = new ArrayList<>();
            List<Goal> activityGoals = new ArrayList<>();

            for (Object fact : activityFacts) {
                if(fact instanceof Substitution) {
                    activitySubstitutions.add((Substitution) fact);

                } else if (fact instanceof Goal) {
                    activityGoals.add((Goal) fact);
                }
            }
            model.addAttribute("activitySubstitutions", activitySubstitutions);
            model.addAttribute("activityGoals", activityGoals);
        }

        logger.info("activityFacts: {}", activityFacts);
        model.addAttribute("activity", activity);

        model.addAttribute("activityFacts", activityFacts);


        // Rambling that's required for navBar.html
        model.addAttribute("httpServletRequest", request);
        model.addAttribute("possibleFactTypes", FactType.values());
        model.addAttribute("defaultFactType", FactType.FACT);

        populateOther(model, activity);

        return "viewActivity";
    }

    @PostMapping("/view-activity")
    public String createEvent(
            @RequestParam(name = "actId", defaultValue = "-1") long actId, 
            @RequestParam(name = "factType", defaultValue = "FACT")  FactType factType,
            @RequestParam(name = "description") String description,
            @RequestParam(name = "time") String time,
            @RequestParam(name = "scorer", defaultValue = "-1") int scorerId,
            @RequestParam(name = "playerOff", defaultValue = "-1") int subOffId,
            @RequestParam(name = "playerOn", defaultValue = "-1") int subOnId,
            HttpServletRequest request) {

        // create the new fact of facttype 
        logger.info("POST /view-activity");
        logger.info("got the desc " + description);
        logger.info(factType.name());
        logger.info(String.format("got the act id: %s", actId));
        logger.info(String.format("got the player on id: %s", subOnId));
        logger.info(String.format("got the player on id: %s", subOffId));
        logger.info(String.format("got the scorer id: %s", scorerId));

            
        Activity activity = activityService.findActivityById(actId);


        Fact fact;

        switch (factType) {
            case FACT:
                fact = new Fact(description, time, activity);
                break;
                
            case GOAL:
                Optional<User> potentialScorer = userService.findUserById(scorerId);
                if (potentialScorer.isEmpty()) {
                    logger.error("Scorer Id not found");
                    return String.format("redirect:./view-activity?activityID=%s", actId);
                }
                User scorer = potentialScorer.get();
                fact = new Goal(description, time, activity, scorer);

                // update the score 
                // activity.setOtherTeamScore("13");
                updateTeamsScore(activity);
                break;
                
            case SUBSTITUTION:
                Optional<User> potentialSubOff = userService.findUserById(subOffId);
                if (potentialSubOff.isEmpty()){
                    logger.error("subbed off player Id not found");
                    return String.format("redirect:./view-activity?activityID=%s", actId);
                }
                User playerOff = potentialSubOff.get(); 
                
                Optional<User> potentialSubOn = userService.findUserById(subOnId);
                if (potentialSubOff.isEmpty()){
                    logger.error("subbed on player Id not found");
                    return String.format("redirect:./view-activity?activityID=%s", actId);
                }
                User playerOn = potentialSubOn.get(); 
                
                fact = new Substitution(description, time, activity, playerOff, playerOn);
                break;

            case OPPOSITION_GOAL:
                updateAwayTeamsScore(activity);

                fact = new OppositionGoal(description, time, activity);
                break;
                
            default:
                logger.error("fact type unknown value");
                return String.format("redirect:./view-activity?activityID=%s", actId);
        }
        
        List<Fact> factList = new ArrayList<>();
        factList.add(fact);
        activity.addFactList(factList);
        activity = activityService.updateOrAddActivity(activity);
        

        return String.format("redirect:./view-activity?activityID=%s", actId);
    }

    /**
     * TODO: maybe move into activity service?
     * increments the home teams score by one 
     **/
    private void updateTeamsScore(Activity activity) {
        String score = activity.getActivityTeamScore();
        if (score == null) {
            score = "0";
        }
        int parsedScore = Integer.parseInt(score);
        parsedScore++;
        
        activity.setActivityTeamScore(String.valueOf(parsedScore));
    }

    /**
     * TODO: maybe move into activity service?
     * increments the away teams score by one 
     **/
    private void updateAwayTeamsScore(Activity activity) {
        String score = activity.getOtherTeamScore();
        if (score == null) {
            score = "0";
        }
        int parsedScore = Integer.parseInt(score);
        parsedScore++;
        
        activity.setOtherTeamScore(String.valueOf(parsedScore));
    }

}
