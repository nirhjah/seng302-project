package nz.ac.canterbury.seng302.tab.controller;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.Null;
import nz.ac.canterbury.seng302.tab.entity.Activity;
import nz.ac.canterbury.seng302.tab.entity.Formation;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.enums.ActivityType;
import nz.ac.canterbury.seng302.tab.form.CreateActivityForm;
import nz.ac.canterbury.seng302.tab.service.ActivityService;
import nz.ac.canterbury.seng302.tab.service.FormationService;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import nz.ac.canterbury.seng302.tab.validator.ActivityFormValidators;

@Controller
public class CreateActivityController {

    private TeamService teamService;
    private UserService userService;
    private ActivityService activityService;
    private FormationService formationService;

    private Logger logger = LoggerFactory.getLogger(CreateActivityController.class);

    private static final String TEMPLATE_NAME = "createActivity";

    public CreateActivityController(TeamService teamService, UserService userService,
            ActivityService activityService, FormationService formationService) {
        this.teamService = teamService;
        this.userService = userService;
        this.activityService = activityService;
        this.formationService = formationService;
    }

    /**
     * Prefills the model with required values
     * @param model the model to be filled
     * @param httpServletRequest the request
     * @throws MalformedURLException can be thrown by getting the path if invalid
     */
    public void prefillModel(Model model, HttpServletRequest httpServletRequest) throws MalformedURLException {
        Optional<User> user = userService.getCurrentUser();
        model.addAttribute("firstName", user.get().getFirstName());
        model.addAttribute("lastName", user.get().getLastName());
        model.addAttribute("displayPicture", user.get().getPictureString());
        model.addAttribute("navTeams", teamService.getTeamList());
        List<Team> allUserTeams = teamService.findTeamsWithUser(user.get());
        List<Team> teamList = new ArrayList<>();
        for (Team team : allUserTeams) {
            if (team.isManager(user.get()) || team.isCoach(user.get())) {
                teamList.add(team);
            }
        }
        model.addAttribute("teamList", teamList);
        model.addAttribute("activityTypes", ActivityType.values());
        URL url = new URL(httpServletRequest.getRequestURL().toString());
        String path = (url.getPath() + "/..");
        model.addAttribute("path", path);
    }

    private void fillModelWithActivity(Model model, Activity activity) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        LocalDateTime startDateTime =  activity.getActivityStart();
        String formattedStartDateTime = startDateTime.format(formatter);

        LocalDateTime endDateTime =  activity.getActivityEnd();
        String formattedEndDateTime = endDateTime.format(formatter);
        model.addAttribute("activityType", activity.getActivityType());
        if (activity.getTeam() != null) {
            model.addAttribute("teamName", activity.getTeam().getName());
        }
        model.addAttribute("actId", activity.getId());
        model.addAttribute("startDateTime",formattedStartDateTime);
        model.addAttribute("endDateTime", formattedEndDateTime);
        model.addAttribute("description", activity.getDescription());
        model.addAttribute("addressLine1", activity.getLocation().getAddressLine1());
        model.addAttribute("addressLine2", activity.getLocation().getAddressLine2());
        model.addAttribute("city", activity.getLocation().getCity());
        model.addAttribute("suburb", activity.getLocation().getSuburb());
        model.addAttribute("country", activity.getLocation().getCountry());
        model.addAttribute("postcode", activity.getLocation().getPostcode());
    }

    /**
     * Performs various error checks for this form, on top of the Jakarta annotations in the form.
     * @param bindingResult Any found errors are added to this
     * @param createActivityForm The form containing the data we're validating
     * @param team The team picked by this form
     * @param currentUser The user making the request
     */
    private void postCreateActivityErrorChecking(
            BindingResult bindingResult,
            CreateActivityForm createActivityForm,
            @Null Team team,
            User currentUser) {
        // The startDate is before endDate
        if (!activityService.validateStartAndEnd(createActivityForm.getStartDateTime(), createActivityForm.getEndDateTime())) {
            bindingResult.addError(new FieldError("CreateActivityForm", "startDateTime", ActivityFormValidators.END_BEFORE_START_MSG));
        }
        
        if (team != null) {
            // The dates are after the team's creation date
            if (!activityService.validateActivityDateTime(team.getCreationDate(), createActivityForm.getStartDateTime(), createActivityForm.getEndDateTime())) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
                bindingResult.addError(new FieldError("CreateActivityForm", "endDateTime",
                        ActivityFormValidators.ACTIVITY_BEFORE_TEAM_CREATION + team.getCreationDate().format(formatter)));
            }
            // This user needs the authority to create/update activities
            boolean hasCreateAuth = team.isCoach(currentUser) || team.isManager(currentUser);
            if (!hasCreateAuth) {
                bindingResult.addError(new FieldError("CreateActivityForm", "team", ActivityFormValidators.NOT_A_COACH_OR_MANAGER));
            }
        }
        // The activity type might require a team
        if (!activityService.validateTeamSelection(createActivityForm.getActivityType(), team)) {
            bindingResult.addError(new FieldError("CreateActivityForm", "team",
                    ActivityFormValidators.TEAM_REQUIRED_MSG));
        }

        // Formations can only exist under certain circumstances.
        // However, if the conditions aren't met, we simply don't add the formation.
        // This is because the front-end greys out the Formation dropdown if the conditions aren't met,
        // so the value may be set but it's invalid.
        long formationId = createActivityForm.getFormation();
        if (formationId != -1 && (Activity.canContainFormation(createActivityForm.getActivityType(), team))) {
            // Check that your team has this formation
            if (formationService.findFormationById(formationId).map(form -> form.getTeam().equals(team)).isEmpty()) {
                bindingResult.addError(new FieldError("CreateActivityForm", "formation",
                    ActivityFormValidators.FORMATION_DOES_NOT_EXIST_MSG));
            }
        }
    }

    /**
     * This controller handles both the edit and creation of an activity
     * @param actId the ID if activity if editing, otherwise null
     * @param createActivityForm the form to be used
     * @param model a mapping of data to pass to the html
     * @param httpServletRequest the request
     * @return the create activity template
     * @throws MalformedURLException thrown in some cases
     */
    @GetMapping("/createActivity")
    public String activityForm(
            @RequestParam(name="edit", required=false) Long actId,
            CreateActivityForm createActivityForm,
            Model model,
            HttpServletRequest httpServletRequest) throws MalformedURLException {
        logger.info("GET /createActivity");
        model.addAttribute("httpServletRequest", httpServletRequest);
        prefillModel(model, httpServletRequest);

        // If you're creating an activity, just return a blank field
        if (actId == null) {
            return TEMPLATE_NAME;
        }
        logger.info("You should be stupid");
            
        User currentUser = userService.getCurrentUser().get();
        Activity activity = activityService.findActivityById(actId);

        // You can't edit an activity that doesn't exist
        if (activity == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Can't edit an activity that doesn't exist");
        }

        Team team = activity.getTeam();
        // If it's a teamless activity, only the original creator can edit it
        if (team == null && !activity.getActivityOwner().equals(currentUser)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Incorrect permissions to edit activity");
        }

        // If it's an activity with a team, you must be the manager or coach
        if (team != null && !team.isCoach(currentUser) && !team.isManager(currentUser)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Incorrect permissions to edit activity");
        }

        fillModelWithActivity(model, activity);
        return TEMPLATE_NAME;
    }

    /**
     * Handles creating or adding activities
     * @param actId the id of activity if editing, otherwise null
     * @param createActivityForm form used to create activity
     * @param bindingResult holds the results of validating the form
     * @param model model mapping of information
     * @param httpServletRequest the request
     * @param httpServletResponse the response to send
     * @return returns my activity page iff the details are valid, returns to activity page otherwise
     * @throws MalformedURLException thrown in some cases
     */
    @PostMapping("/createActivity")
    public String createActivity(
            @RequestParam(name = "edit", defaultValue = "-1") Long actId,
            @Validated CreateActivityForm createActivityForm,
            BindingResult bindingResult,
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            Model model) throws MalformedURLException {

        logger.info("POST /createActivity");
        model.addAttribute("httpServletRequest", httpServletRequest);
        prefillModel(model, httpServletRequest);

        User currentUser = userService.getCurrentUser().get();
        Team team = teamService.getTeam(createActivityForm.getTeam());
        Activity activity = activityService.findActivityById(actId);

        postCreateActivityErrorChecking(bindingResult, createActivityForm, team, currentUser);

        if (bindingResult.hasErrors()) {
            httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            if (activity != null) {
                model.addAttribute("actId", actId);
                fillModelWithActivity(model, activity);
            }
            return "createActivity";
        }

        Location location = new Location(
            createActivityForm.getAddressLine1(),
            createActivityForm.getAddressLine2(),
            createActivityForm.getSuburb(),
            createActivityForm.getCity(),
            createActivityForm.getPostcode(),
            createActivityForm.getCountry()
        );

        if (actId == -1) {  // Creating a new activity
            activity = new Activity();
        } else {            // Updating an existing activity
            activity = activityService.findActivityById(actId);
        }
        activity.setActivityType(createActivityForm.getActivityType());
        activity.setTeam(team);
        activity.setActivityEnd(createActivityForm.getEndDateTime());
        activity.setActivityStart(createActivityForm.getStartDateTime());
        activity.setActivityOwner(currentUser);
        activity.setLocation(location);
        activity.setDescription(createActivityForm.getDescription());
        // Add formation if possible
        if (createActivityForm.getFormation() == -1 || !Activity.canContainFormation(createActivityForm.getActivityType(), team)) {
            activity.setFormation(null);
        } else {
            Optional<Formation> formation = formationService.findFormationById(createActivityForm.getFormation());
            // The error checking function checks if this exists, so this should always pass
            activity.setFormation(formation.get());
        }
        activity = activityService.updateOrAddActivity(activity);
        return String.format("redirect:./view-activity?activityID=%s", activity.getId());
    }

    /**
     * A JSON API endpoint, which gives the formations of an associated team.. Used by the createActivity page to update
     * @return A json object of type <code>{formationId: "formationString", ...}</code>
     */
    @GetMapping(path = "/createActivity/get_team_formation", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<Long, String>> getTeamFormation(@RequestParam("teamId") long teamId) {
        logger.info("GET /createActivity/get_team_formation");
        // CHECK: Are we logged in?
        Optional<User> oCurrentUser = userService.getCurrentUser();
        if (oCurrentUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User currentUser = oCurrentUser.get();
        // CHECK: Does our requested team exist?
        Team team = teamService.getTeam(teamId);
        if (team == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        // CHECK: Do you have permission to edit this team's formations?
        // (This doubles as an "Are you in this team" check)
        if (!team.isCoach(currentUser) && !team.isManager(currentUser)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Return a JSON object of (id -> string)
        Map<Long, String> formations = formationService.getTeamsFormations(teamId).stream()
                    .collect(Collectors.toMap(Formation::getFormationId, Formation::getFormation));

        return ResponseEntity.ok().body(formations);
    }

}
