package nz.ac.canterbury.seng302.tab.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import jakarta.servlet.http.HttpServletResponse;
import nz.ac.canterbury.seng302.tab.entity.Activity;
import nz.ac.canterbury.seng302.tab.entity.Fact.Fact;
import nz.ac.canterbury.seng302.tab.entity.Fact.Goal;
import nz.ac.canterbury.seng302.tab.entity.Fact.OppositionGoal;
import nz.ac.canterbury.seng302.tab.entity.Fact.Substitution;
import nz.ac.canterbury.seng302.tab.enums.ActivityOutcome;
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

    String createEventFormString = "createEventForm";

    String overallScoreTeamString = "overallScoreTeam";

    @Autowired
    public ViewActivityController(UserService userService, ActivityService activityService, TeamService teamService,FactService factService) {
        this.userService = userService;
        this.activityService = activityService;
        this.teamService = teamService;
        this.factService=factService;
    }

    /**
     * Gets all fact types and other information for statistics depending on the activity type
     * @param model  model to add data to
     * @param activity  current activity
     */
    private void populateOther(Model model, Activity activity) {
        ActivityType type = activity.getActivityType();

        model.addAttribute("canShowScores", type == ActivityType.Friendly || type == ActivityType.Game);
        model.addAttribute("canShowPlayers", type == ActivityType.Friendly || type == ActivityType.Game);

        /*
        Different activity types have different sets of allowed FactTypes they can hold.
         */
        List<FactType> possibleFactTypesForActivity = switch (type) {
            case Competition, Other -> List.of(FactType.FACT);
            case Friendly, Game -> List.of(FactType.GOAL, FactType.OPPOSITION_GOAL, FactType.SUBSTITUTION, FactType.FACT);
            case Training -> List.of();
        };

        model.addAttribute("possibleFactTypes", possibleFactTypesForActivity);
        model.addAttribute("activityOutcomes", List.of(ActivityOutcome.Win, ActivityOutcome.Loss, ActivityOutcome.Draw));

        model.addAttribute("noFacts", possibleFactTypesForActivity.size() == 0);
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

        model.addAttribute(createEventFormString, new CreateEventForm());

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
     * @param activityOutcome outcome of activity (win loss or draw) for team
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
            @RequestParam(name = "description", defaultValue = "") String description,
            @RequestParam(name = "overallScoreTeam", defaultValue = "") String overallScoreTeam,
            @RequestParam(name = "overallScoreOpponent", defaultValue = "") String overallScoreOpponent,
            @RequestParam(name = "activityOutcomes", defaultValue = "-1") ActivityOutcome activityOutcome,
            @RequestParam(name = "time") String time,
            @RequestParam(name = "goalValue", defaultValue = "1") int goalValue,
            @RequestParam(name = "scorer", defaultValue = "-1") int scorerId,
            @RequestParam(name = "playerOff", defaultValue = "-1") int subOffId,
            @RequestParam(name = "playerOn", defaultValue = "-1") int subOnId,
            @Validated CreateEventForm createEventForm,
            BindingResult bindingResult,
            HttpServletRequest request,
            Model model,
            HttpServletResponse httpServletResponse,
            RedirectAttributes redirectAttributes) {

        logger.info(factType.name());
        logger.info(String.format("got the act id: %s", actId));
        logger.info(String.format("got the player on id: %s", subOnId));
        logger.info(String.format("got the player on id: %s", subOffId));
        logger.info(String.format("got the scorer id: %s", scorerId));

        model.addAttribute(overallScoreTeamString, overallScoreTeam);
        model.addAttribute("httpServletRequest", request);

        Activity activity = activityService.findActivityById(actId);
        Fact fact;
        String viewActivityRedirectUrl = String.format("redirect:./view-activity?activityID=%s", actId);


        if (activityService.validateActivityScore(overallScoreTeam, overallScoreOpponent) == 1) {
            logger.info("scores not same type");
            bindingResult.addError(new FieldError(createEventFormString, overallScoreTeamString, "Both teams require scores of the same type"));
        }

        if (activityService.validateActivityScore(overallScoreTeam, overallScoreOpponent) == 2) {
            logger.info("one score is empty");
            bindingResult.addError(new FieldError(createEventFormString, overallScoreTeamString, "Other score field cannot be empty"));
        }

        if (factType == FactType.SUBSTITUTION && subOffId == subOnId) {
            logger.info("players cannot sub themselves");
            bindingResult.addError(new FieldError(createEventFormString, "subOn", "Players cannot sub themselves"));
        }

        if (factType == FactType.FACT && description.isEmpty()) {
                logger.info("description was not provided for fact");
                bindingResult.addError(new FieldError(createEventFormString, "description", "Fact type events require a description"));
        }



        if (bindingResult.hasErrors()) {
            httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            redirectAttributes.addFlashAttribute("scoreInvalid", "Leave Modal Open");
            redirectAttributes.addFlashAttribute(createEventFormBindingResult, bindingResult);
            return viewActivityRedirectUrl;
        }

        switch (factType) {
            case FACT:
                fact = new Fact(description, time, activity);
                break;

            case GOAL:
                Optional<User> potentialScorer = userService.findUserById(scorerId);
                if (potentialScorer.isEmpty()) {
                    logger.error("Scorer Id not found");
                    return viewActivityRedirectUrl;
                }

                User scorer = potentialScorer.get();
                fact = new Goal(description, time, activity, scorer, goalValue);

                activityService.updateTeamsScore(activity, goalValue);

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

                fact = new Substitution(description, time, activity, playerOff, playerOn);
                break;

            case OPPOSITION_GOAL:
                activityService.updateAwayTeamsScore(activity, goalValue);

                fact = new OppositionGoal(description, time, activity, goalValue);
                break;

            default:
                logger.error("fact type unknown value");
                return viewActivityRedirectUrl;
        }

        activity.setActivityOutcome(activityOutcome);


        List<Fact> factList = new ArrayList<>();
        factList.add(fact);
        activity.addFactList(factList);
        activity = activityService.updateOrAddActivity(activity);


        return viewActivityRedirectUrl;
    }

}
