package nz.ac.canterbury.seng302.tab.controller;

import java.time.LocalDateTime;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletResponse;
import nz.ac.canterbury.seng302.tab.entity.Activity;
import nz.ac.canterbury.seng302.tab.entity.Fact.Fact;
import nz.ac.canterbury.seng302.tab.entity.Fact.Goal;
import nz.ac.canterbury.seng302.tab.entity.Fact.Substitution;
import nz.ac.canterbury.seng302.tab.entity.lineUp.LineUp;
import nz.ac.canterbury.seng302.tab.entity.lineUp.LineUpPosition;
import nz.ac.canterbury.seng302.tab.enums.ActivityOutcome;
import nz.ac.canterbury.seng302.tab.enums.ActivityType;
import nz.ac.canterbury.seng302.tab.enums.FactType;
import nz.ac.canterbury.seng302.tab.form.AddFactForm;
import nz.ac.canterbury.seng302.tab.form.CreateEventForm;
import nz.ac.canterbury.seng302.tab.service.ActivityService;
import nz.ac.canterbury.seng302.tab.service.FactService;
import nz.ac.canterbury.seng302.tab.service.LineUpPositionService;
import nz.ac.canterbury.seng302.tab.service.LineUpService;

import nz.ac.canterbury.seng302.tab.validator.FactValidators;
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

import static nz.ac.canterbury.seng302.tab.validator.ActivityFormValidators.*;

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
    private LineUpService lineUpService;

    @Autowired
    private LineUpPositionService lineUpPositionService;

    String createEventFormBindingResult = "createEventFormBindingResult";

    String addFactFormBindingResult = "addFactFormBindingResult";

    String createEventFormString = "createEventForm";

    String overallScoreTeamString = "overallScoreTeam";

    String httpServletRequestString = "httpServletRequest";

    String leaveModalOpenString = "Leave Modal Open";

    String stayOnTabNameString = "stayOnTab_name";

    String stayOnTabIndexString = "stayOnTab_index";

    String scoreTabName = "score-tab";

    String viewActivityRedirect = "redirect:./view-activity?activityID=%s";

    int scoreTabIndex = 3;

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
        model.addAttribute("noFact", FactType.NONE);
        model.addAttribute("activityOutcomes", List.of(ActivityOutcome.Win, ActivityOutcome.Loss, ActivityOutcome.Draw));
        model.addAttribute("noOutcome", ActivityOutcome.None);
        model.addAttribute("selectedOutcome", activity.getOutcome() != null ? activity.getOutcome() : ActivityOutcome.None);

        model.addAttribute("overallScoreTeamSaved", activity.getActivityTeamScore());
        model.addAttribute("overallScoreOpponentSaved", activity.getOtherTeamScore());
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

        model.addAttribute(createEventFormString, createEventForm);
        model.addAttribute("addFactForm", new AddFactForm());

        if (model.asMap().containsKey(createEventFormBindingResult))
        {
            model.addAttribute("org.springframework.validation.BindingResult.createEventForm",
                    model.asMap().get(createEventFormBindingResult));
        }

        if (model.asMap().containsKey(addFactFormBindingResult))
        {
            model.addAttribute("org.springframework.validation.BindingResult.addFactForm",
                    model.asMap().get(addFactFormBindingResult));
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
            model.addAttribute("activityGoals", activityService.sortGoalTimesAscending(activityGoals));
        }

        logger.info("activityFacts: {}", activityFacts);
        model.addAttribute("activity", activity);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE dd MMMM yyyy K:mm a");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("KK:mm a");

        DateTimeFormatter titleFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
        model.addAttribute("activityStartTitle", activity.getActivityStart().format(titleFormatter));

        if (activity.getActivityStart().toLocalDate().equals(activity.getActivityEnd().toLocalDate())) {
            model.addAttribute("activityDateTime", activity.getActivityStart().format(formatter)
                    .concat(" - ").concat(activity.getActivityEnd().format(timeFormatter)));
        } else {
            model.addAttribute("activityDateTime", activity.getActivityStart().format(formatter).concat(" - ").concat(activity.getActivityEnd().format(formatter)));
        }

        model.addAttribute("activityFacts", activityFacts);
        List<Fact> factList = factService.getAllFactsOfGivenTypeForActivity(FactType.FACT.ordinal(), activity);

        model.addAttribute("factList", factList);

        // attributes for the subs

        // all players who are currently playing - for the sub off
        List<User> playersInLineUp = getAllPlayersPlaying(activity.getId());
        model.addAttribute("playersInLineUp", playersInLineUp);
        // all players who arent playing - for the sub on
        List<User> playersNotInLineUp = getAllPlayersNotPlaying(activity.getId());
        model.addAttribute("playersNotInLineUp", playersNotInLineUp);

        // Rambling that's required for navBar.html
        model.addAttribute(httpServletRequestString, request);
        model.addAttribute("possibleFactTypes", FactType.values());
        model.addAttribute("defaultFactType", FactType.FACT);
        model.addAttribute("playersInTeam", activity.getInvolvedMembersNoManagerAndCoaches());
        model.addAttribute("outcomeString", outcomeString(activity));
        model.addAttribute("currentUser", userService.getCurrentUser());
        populateOther(model, activity);

        return "viewActivity";
    }

    /**
     * Determines string to display depending on who won/loss/if it was draw
     * @param activity activity to get outcome of
     * @return string with display of outcome
     */
    private String outcomeString(Activity activity) {
        String outcomeString = "";
        if (activity.getOutcome() == ActivityOutcome.Win) {
            outcomeString = "Winner: " + activity.getTeam().getName();
        }
        if (activity.getOutcome() == ActivityOutcome.Loss) {
            outcomeString = "Winner: Opponent Team";
        }
        if (activity.getOutcome() == ActivityOutcome.Draw) {
            outcomeString = "Draw";
        }
        return outcomeString;
    }

    /**
     * Handles adding an overall score to an activity
     * @param actId           activity to add overall score to
     * @param result        BindingResult used for errors
     * @param request              request
     * @param model                model to be filled
     * @param httpServletResponse   httpServerletResponse
     * @param redirectAttributes    stores error message to be displayed
     * @return  view activity page
     */
    @PostMapping("/add-fact")
    public String addFactForm(
            @RequestParam(name = "actId", defaultValue = "-1") long actId,
            @RequestParam(name = "timeOfFact", required = false) String timeOfFact,
            @RequestParam(name = "description") String description,
            @Validated AddFactForm addFactForm,
            BindingResult result,
            HttpServletRequest request,
            Model model,
            HttpServletResponse httpServletResponse,
            RedirectAttributes redirectAttributes) {
        model.addAttribute("httpServletRequest", request);
        Activity activity = activityService.findActivityById(actId);
        String viewActivityRedirectUrl = String.format("redirect:./view-activity?activityID=%s", actId);
        if (!timeOfFact.isEmpty()) {
            try {
                int time = Integer.parseInt(timeOfFact);
                int totalActivityMinutes = (int) Duration.between(activity.getActivityStart(), activity.getActivityEnd()).toMinutes();
                if (time > totalActivityMinutes) {
                    result.addError(new FieldError("addFactForm", "timeOfFact", FactValidators.timeErrorMessage));
                }
            } catch (NumberFormatException e) {
                result.addError(new FieldError("addFactForm", "timeOfFact", "Must be an int"));
            }
        } else {
            timeOfFact = null;
        }
        if (LocalDateTime.now().isBefore(activity.getActivityStart())) {
            result.addError(new FieldError("addFactForm", "timeOfFact", "You can only add a fact once the activity starts"));
        }
        redirectAttributes.addFlashAttribute("stayOnTab_name", "facts-tab");
        redirectAttributes.addFlashAttribute("stayOnTab_index", 1);

        if (result.hasErrors()) {
            logger.info(result.getAllErrors().toString());
            httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            redirectAttributes.addFlashAttribute("factInvalid", "Leave Modal Open");
            redirectAttributes.addFlashAttribute(addFactFormBindingResult, result);
            return viewActivityRedirectUrl;

        }

        Fact fact = new Fact(description, timeOfFact, activity);
        factService.addOrUpdate(fact);
        redirectAttributes.addFlashAttribute("stayOnTab_Name", "facts-tab");
        redirectAttributes.addFlashAttribute("stayOnTab_index", 1);
        return viewActivityRedirectUrl;

    }


    /**
     * Handles adding a goal fact with scorer, desc (optional), time and value to an activity
     * @param actId           activity to add goal fact to
     * @param createEventForm      CreateEventForm object used for validation
     * @param bindingResult        BindingResult used for errors
     * @param request              request
     * @param model                model to be filled
     * @param httpServletResponse   httpServerletResponse
     * @param redirectAttributes    stores error message to be displayed
     * @return  view activity page
     */
    @PostMapping("/add-goal")
    public String addGoalForm(
            @RequestParam(name = "actId", defaultValue = "-1") long actId,
            @RequestParam(name = "scorer", defaultValue = "-1") int scorerId,
            @RequestParam(name = "goalValue", defaultValue = "1") int goalValue,
            @RequestParam(name = "time") String time,
            @RequestParam(name = "description", defaultValue = "") String description,
            @Validated CreateEventForm createEventForm,
            BindingResult bindingResult,
            HttpServletRequest request,
            Model model,
            HttpServletResponse httpServletResponse,
            RedirectAttributes redirectAttributes) {


        model.addAttribute(httpServletRequestString, request);

        Activity activity = activityService.findActivityById(actId);
        String viewActivityRedirectUrl = String.format(viewActivityRedirect, actId);


        Optional<User> potentialScorer = userService.findUserById(scorerId);
        if (potentialScorer.isEmpty()) {
            bindingResult.addError(new FieldError(createEventFormString, "scorer", PLAYER_IS_REQUIRED_MSG));
        }

        if (time.isBlank()) {
            bindingResult.addError(new FieldError(createEventFormString, "time", FIELD_CANNOT_BE_BLANK_MSG));
        } else {
            if (!activityService.checkTimeOfFactWithinActivity(activity, Integer.parseInt(time))) {
                bindingResult.addError(new FieldError(createEventFormString, "time", GOAL_NOT_SCORED_WITHIN_DURATION));
            }
        }

        if (LocalDateTime.now().isBefore(activity.getActivityStart())) {
            bindingResult.addError(new FieldError(createEventFormString, "scorer", ADDING_GOAL_BEFORE_ACTIVITY_START_MSG));
        }

        if (bindingResult.hasErrors()) {
            httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            redirectAttributes.addFlashAttribute("goalInvalid", leaveModalOpenString);
            redirectAttributes.addFlashAttribute(createEventFormBindingResult, bindingResult);
            redirectAttributes.addFlashAttribute(stayOnTabNameString, scoreTabName);
            redirectAttributes.addFlashAttribute(stayOnTabIndexString, scoreTabIndex);
            return viewActivityRedirectUrl;
        }

        List<Fact> factList = new ArrayList<>();
        if (potentialScorer.isPresent()) {
            User scorer = potentialScorer.get();
            Fact goalFact = new Goal(description, time, activity, scorer, goalValue);
            factList.add(goalFact);
            activity.addFactList(factList);
            activityService.updateOrAddActivity(activity);
        }






        redirectAttributes.addFlashAttribute(stayOnTabNameString, scoreTabName);
        redirectAttributes.addFlashAttribute(stayOnTabIndexString, scoreTabIndex);

        return viewActivityRedirectUrl;

    }


    /**
     * Handles adding an overall score to an activity
     * @param actId           activity to add overall score to
     * @param overallScoreTeam overall score of team
     * @param overallScoreOpponent overall score of opponent
     * @param createEventForm      CreateEventForm object used for validation
     * @param bindingResult        BindingResult used for errors
     * @param request              request
     * @param model                model to be filled
     * @param httpServletResponse   httpServerletResponse
     * @param redirectAttributes    stores error message to be displayed
     * @return  view activity page
     */
    @PostMapping("/overall-score")
    public String overallScoreForm(
            @RequestParam(name = "actId", defaultValue = "-1") long actId,
            @RequestParam(name = "overallScoreTeam", defaultValue = "") String overallScoreTeam,
            @RequestParam(name = "overallScoreOpponent", defaultValue = "") String overallScoreOpponent,
            @Validated CreateEventForm createEventForm,
            BindingResult bindingResult,
            HttpServletRequest request,
            Model model,
            HttpServletResponse httpServletResponse,
            RedirectAttributes redirectAttributes) {

        model.addAttribute(overallScoreTeamString, overallScoreTeam);
        model.addAttribute(httpServletRequestString, request);

        Activity activity = activityService.findActivityById(actId);
        String viewActivityRedirectUrl = String.format(viewActivityRedirect, actId);

        if (activityService.validateActivityScore(overallScoreTeam, overallScoreOpponent) == 1) {
            bindingResult.addError(new FieldError(createEventFormString, overallScoreTeamString, SCORE_FORMATS_DONT_MATCH_MSG));
        }

        if (activityService.validateActivityScore(overallScoreTeam, overallScoreOpponent) == 2) {
            bindingResult.addError(new FieldError(createEventFormString, overallScoreTeamString, OTHER_SCORE_CANNOT_BE_EMPTY_MSG));
        }

        if (LocalDateTime.now().isBefore(activity.getActivityStart())) {
            bindingResult.addError(new FieldError(createEventFormString, overallScoreTeamString, ADDING_STAT_BEFORE_START_TIME_MSG));
        }

        if (bindingResult.hasErrors()) {
            httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            redirectAttributes.addFlashAttribute("scoreInvalid", leaveModalOpenString);
            redirectAttributes.addFlashAttribute(createEventFormBindingResult, bindingResult);

            redirectAttributes.addFlashAttribute(stayOnTabNameString, scoreTabName);
            redirectAttributes.addFlashAttribute(stayOnTabIndexString, scoreTabIndex);

            return viewActivityRedirectUrl;
        }


        if (overallScoreTeam != null && overallScoreOpponent != null) {
            activity.setOtherTeamScore(overallScoreOpponent);
            activity.setActivityTeamScore(overallScoreTeam);
        }

        activityService.updateOrAddActivity(activity);

        redirectAttributes.addFlashAttribute(stayOnTabNameString, scoreTabName);
        redirectAttributes.addFlashAttribute(stayOnTabIndexString, scoreTabIndex);

        return viewActivityRedirectUrl;

    }

    @PostMapping("/add-sub")
    public String createSub(
                            @RequestParam(name="actId", defaultValue="-1") long actId,
                            @RequestParam(name="playerOn", defaultValue="-1") int subOnId,
                            @RequestParam(name="playerOff", defaultValue="-1") int subOffId,
                            @RequestParam(name = "time") String time) {
        
        logger.info(String.format("got the player on id: %s", subOnId));
        logger.info(String.format("got the player off id: %s", subOffId));
        logger.info(String.format("got the time %s", time));
        logger.info(String.format("activity %s", actId));
        String viewActivityRedirectUrl = String.format(viewActivityRedirect, actId);
        
        List<User> playersInLineUp = getAllPlayersPlaying(actId);
        if (playersInLineUp.isEmpty()) {
            logger.error("There are no players in the lineup but a sub was made ");
        }



        return viewActivityRedirectUrl;
    }

    /**
     * returns a list of the users that are currently in the lineup 
     * @param actId the activity id 
     * @return a list of the users that are currently in the lineup for the activity, if there are no players the returns an empty list 
    */
    private List<User> getAllPlayersPlaying(long actId) {
        List<LineUp> activityLineups = lineUpService.findLineUpByActivity(actId).get();
        for (LineUp lineup : activityLineups) {
            logger.info(String.format("the id of the activity is %d", lineup.getTeam().getId()));
        }
        // TODO we are grabbing the first one now but i dont know if there can possibly be multiple objects in this list
        LineUp lineup = activityLineups.get(0);
        Optional<List<LineUpPosition>> optionaLineupPositions = lineUpPositionService.findLineUpPositionsByLineUp(lineup.getLineUpId());
        if (optionaLineupPositions.isEmpty()) {
            // there are no players to be subbed off so return an empty list and expect the caller to handle this 
            return List.of();
        }
        List<User> playersInLineUp = optionaLineupPositions.get().stream().map(x -> x.getPlayer()).collect(Collectors.toList());
        for (User player : playersInLineUp) {
            logger.info(String.format("the player name is %s", player.getFirstName()));
        }

        return playersInLineUp;
    }

    /**
     * TODO: this just has all the players who arent playing as available subs but right now there isnt any functionality for otherwise in the backend
     * @param actId the activity id 
     * @return a list of users who arent playing in the current activity (on the bench)
    */
    private List<User> getAllPlayersNotPlaying(long actId) {
        List<User> playersPlaying = getAllPlayersPlaying(actId);
        List<User> playersInTeam = new ArrayList<>(activityService.findActivityById(actId).getTeam().getTeamMembers());

        List<User> playersNotPlaying = playersInTeam.stream().filter(player -> !playersPlaying.contains(player)).collect(Collectors.toList());
        
        return playersNotPlaying;
    }

    // TODO probably remove below code since its unused
    /**
     * Handles creating an event and adding overall scores
     * @param actId       activity ID to add stats/event to
     * @param factType    selected fact type
     * @param description description of event
     * @param activityOutcome outcome of activity (win loss or draw) for team
     * @param time                 time of event
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
            @RequestParam(name = "activityOutcomes", defaultValue = "None") ActivityOutcome activityOutcome,
            @RequestParam(name = "time") String time,
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

        model.addAttribute(httpServletRequestString, request);

        Activity activity = activityService.findActivityById(actId);
        Fact fact;
        String viewActivityRedirectUrl = String.format(viewActivityRedirect, actId);


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
            redirectAttributes.addFlashAttribute("scoreInvalid", leaveModalOpenString);
            redirectAttributes.addFlashAttribute(createEventFormBindingResult, bindingResult);
            return viewActivityRedirectUrl;
        }

        List<Fact> factList = new ArrayList<>();


        switch (factType) {
            case FACT:
                fact = new Fact(description, time, activity);
                factList.add(fact);

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
                factList.add(fact);

                break;

            case NONE:
                break;

            default:
                logger.error("fact type unknown value");
                return viewActivityRedirectUrl;
        }

        if (activityOutcome != ActivityOutcome.None) {
            activity.setActivityOutcome(activityOutcome);
        }


        activity.addFactList(factList);
        activityService.updateOrAddActivity(activity);


        return viewActivityRedirectUrl;
    }



}
