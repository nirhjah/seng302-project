package nz.ac.canterbury.seng302.tab.controller;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import jakarta.servlet.http.HttpServletResponse;
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
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    String createEventFormBindingResult = "createEventFormBindingResult";

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
     * @param createEventForm CreateEventForm object used for validation
     * @return           view activity page
     */
    @GetMapping("/view-activity")
    public String viewActivityPage(
            Model model,
            @RequestParam(value = "activityID") Long activityID,
            HttpServletRequest request,
            CreateEventForm createEventForm) {

        model.addAttribute("createEventForm", new CreateEventForm());

        if (model.asMap().containsKey(createEventFormBindingResult))
        {
            model.addAttribute("org.springframework.validation.BindingResult.createEventForm",
                    model.asMap().get(createEventFormBindingResult));
        }

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

    /**
     * Handles creating an event and adding overall scores
     * @param actId       activity ID to add stats/event to
     * @param factType    selected fact type
     * @param description description of event
     * @param overallScoreTeam  overall score for team
     * @param overallScoreOpponent overall score for opponent
     * @param time                 time of event
     * @param scorerId             user ID of scorer
     * @param subOffId             user ID of sub off
     * @param subOnId              user ID of sub on
     * @param createEventForm      CreateEventForm object used for validation
     * @param bindingResult        BindingResult used for errors
     * @param request              request
     * @param model                model to be filled
     * @param httpServletResponse   httpServerletResponse
     * @param redirectAttributes    stores error message to be displayed
     * @return                       view activity page
     */
    @PostMapping("/view-activity")
    public String createEvent(
            @RequestParam(name = "actId", defaultValue = "-1") long actId,
            @RequestParam(name = "factType", defaultValue = "FACT")  FactType factType,
            @RequestParam(name = "description") String description,
            @RequestParam(name = "overallScoreTeam") String overallScoreTeam,
            @RequestParam(name = "overallScoreOpponent") String overallScoreOpponent,
            @RequestParam(name = "time") String time,
            @RequestParam(name = "scorer", defaultValue = "-1") int scorerId,
            @RequestParam(name = "playerOff", defaultValue = "-1") int subOffId,
            @RequestParam(name = "playerOn", defaultValue = "-1") int subOnId,
            @Validated CreateEventForm createEventForm,
            BindingResult bindingResult,
            HttpServletRequest request,
            Model model,
            HttpServletResponse httpServletResponse,
            RedirectAttributes redirectAttributes) {

        model.addAttribute("overallScoreTeam", overallScoreTeam);
        model.addAttribute("httpServletRequest", request);

        Activity activity = activityService.findActivityById(actId);

        System.out.println(time);


        Fact fact;
        String viewActivityRedirectUrl = String.format("redirect:./view-activity?activityID=%s", actId);


        if (activityService.validateActivityScore(overallScoreTeam, overallScoreOpponent) == 1) {
            logger.info("scores not same type");
            bindingResult.addError(new FieldError("createEventForm", "overallScoreTeam", "Both teams require scores of the same type"));
        }

        if (activityService.validateActivityScore(overallScoreTeam, overallScoreOpponent) == 2) {
            logger.info("one score is empty");
            bindingResult.addError(new FieldError("createEventForm", "overallScoreTeam", "Other score field cannot be empty"));
        }

        validateEmptyTimeField(bindingResult,time);
        validateSubbingSamePlayers(bindingResult,subOffId,subOnId);

        if (bindingResult.hasErrors()) {
            httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            redirectAttributes.addFlashAttribute("scoreInvalid", "Leave Modal Open");
            redirectAttributes.addFlashAttribute(createEventFormBindingResult, bindingResult);

            return viewActivityRedirectUrl;
        }

        LocalTime localTime = LocalTime.parse(time);

        switch (factType) {
            case FACT:
                logger.info("is fact");
                fact = new Fact(description, activity,localTime);
                break;

            case GOAL:
                Optional<User> potentialScorer = userService.findUserById(scorerId);
                if (potentialScorer.isEmpty()) {
                    logger.error("Scorer Id not found");
                    return viewActivityRedirectUrl;
                }
                User scorer = potentialScorer.get();
                fact = new Goal(description, activity, scorer,localTime);

                // update the score
                // activity.setOtherTeamScore("13");
                activityService.updateTeamsScore(activity);
                break;

            case SUBSTITUTION:
                Optional<User> potentialSubOff = userService.findUserById(subOffId);
                if (potentialSubOff.isEmpty()){
                    logger.error("subbed off player Id not found");
                    return viewActivityRedirectUrl;
                }
                User playerOff = potentialSubOff.get();

                Optional<User> potentialSubOn = userService.findUserById(subOnId);
                if (potentialSubOff.isEmpty()){
                    logger.error("subbed on player Id not found");
                    return viewActivityRedirectUrl;
                }
                User playerOn = potentialSubOn.get();
                fact = new Substitution(description, activity, playerOff, playerOn,localTime);
                break;

            case OPPOSITION_GOAL:
                activityService.updateAwayTeamsScore(activity);

                fact = new OppositionGoal(description, localTime, activity);
                break;

            default:
                logger.error("fact type unknown value");
                return viewActivityRedirectUrl;
        }
        
        List<Fact> factList = new ArrayList<>();
        factList.add(fact);
        activity.addFactList(factList);

        activityService.updateOrAddActivity(activity);

        return viewActivityRedirectUrl;
    }


    /**
     * Validates whether the given time field is empty or not and adds an error message to the BindingResult
     * if it is empty.
     *
     * @param bindingResult The BindingResult object that holds validation errors.
     * @param time The time string to be validated.
     */
    private void validateEmptyTimeField(BindingResult bindingResult, String time){
        if (time.isEmpty()) {
            bindingResult.addError(new FieldError("createEventForm", "time", "Time field cannot be empty"));
        }
    }

    /**
     * Validates whether the players involved in a substitution are the same and adds an error message
     * to the BindingResult if they are identical.
     *
     * @param bindingResult The BindingResult object that holds validation errors.
     * @param subOnOff The ID of the player who is being substituted off.
     * @param subOnId The ID of the player who is being substituted on.
     */
    private void validateSubbingSamePlayers(BindingResult bindingResult,long subOnOff, long subOnId){
        if (subOnOff==subOnId) {
            bindingResult.addError(new FieldError("createEventForm", "playerOn", "Cannot substitute the same player"));
        }
    }

}
