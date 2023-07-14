package nz.ac.canterbury.seng302.tab.controller;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

    public void fillModelWithActivity(Model model, Activity activity) {
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
     * This controller handles both the edit and creation of an activity
     * @param actId the ID if activity if editing, otherwise null
     * @param createActivityForm the form to be used
     * @param model a mapping of data to pass to the html
     * @param httpServletRequest the request
     * @return the create activity template
     * @throws MalformedURLException thrown in some cases
     */
    @GetMapping("/createActivity")
    public String activityForm( @RequestParam(name="edit", required=false) Long actId,CreateActivityForm createActivityForm,
                                        Model model,
                                        HttpServletRequest httpServletRequest) throws MalformedURLException {
        model.addAttribute("httpServletRequest", httpServletRequest);
        prefillModel(model, httpServletRequest);
        logger.info("GET /createActivity");


        Activity activity;
        if (actId !=null){
            if ((activity = activityService.findActivityById(actId))!=null){
                fillModelWithActivity(model, activity);
            }
        }
        return "createActivity";
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
            @RequestParam(name = "actId", defaultValue = "-1") Long actId,
            @Validated CreateActivityForm createActivityForm,
            BindingResult bindingResult,
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            Model model) throws MalformedURLException {

        logger.info("POST /createActivity");
        model.addAttribute("httpServletRequest", httpServletRequest);
        prefillModel(model, httpServletRequest);

        if (!activityService.validateStartAndEnd(createActivityForm.getStartDateTime(), createActivityForm.getEndDateTime())) {
            if (!bindingResult.hasFieldErrors("startDateTime")) {
                bindingResult.addError(new FieldError("CreateActivityForm", "startDateTime",
                        ActivityFormValidators.END_BEFORE_START_MSG));
            }
        }
        if (createActivityForm.getAddressLine1().isBlank()) {
            bindingResult.addError(new FieldError("CreateActivityForm", "addressLine1", "This is a required field"));
        }
        if (createActivityForm.getPostcode().isBlank()) {
            bindingResult.addError(new FieldError("CreateActivityForm", "postcode", "This is a required field"));
        }

        User user = userService.getCurrentUser().get();
        Team team = teamService.getTeam(createActivityForm.getTeam());
        if (team!=null) {
            if (!activityService.validateActivityDateTime(team.getCreationDate(), createActivityForm.getStartDateTime(), createActivityForm.getEndDateTime())) {
                if (!bindingResult.hasFieldErrors("endDateTime")) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
                    bindingResult.addError(new FieldError("CreateActivityForm", "endDateTime",
                            ActivityFormValidators.ACTIVITY_BEFORE_TEAM_CREATION + team.getCreationDate().format(formatter)));
                }
            }
            boolean hasCreateAuth = team.isCoach(user) || team.isManager(user);
            if (!hasCreateAuth) {
                bindingResult.addError(new FieldError("CreateActivityForm", "team", ActivityFormValidators.NOT_A_COACH_OR_MANAGER));
            }
        } else if (!activityService.validateTeamSelection(createActivityForm.getActivityType(), team)) {
            bindingResult.addError(new FieldError("CreateActivityForm", "team",
                    ActivityFormValidators.TEAM_REQUIRED_MSG));
        }

        if (bindingResult.hasErrors()) {
            httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            if (actId != -1) {
                model.addAttribute("actId", actId);
                Activity activity = activityService.findActivityById(actId);
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

        Activity activity;
        
        if (actId == -1) {  // Creating a new activity
            activity = new Activity();
        } else {            // Updating an existing activity
            activity = activityService.findActivityById(actId);
        }
        activity.setActivityType(createActivityForm.getActivityType());
        activity.setTeam(team);
        activity.setActivityEnd(createActivityForm.getEndDateTime());
        activity.setActivityStart(createActivityForm.getStartDateTime());
        activity.setActivityOwner(userService.getCurrentUser().get());
        activity.setLocation(location);
        activity.setDescription(createActivityForm.getDescription());
        activity = activityService.updateOrAddActivity(activity);
        return String.format("redirect:./view-activity?activityID=%s", activity.getId());
    }

    @GetMapping(path = "/createActivity/get_team_formation", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> getTeamFormation(@RequestParam("teamId") long teamId) {
        logger.info("GET /createActivity/get_team_formation");
        // CHECK: Are we logged in?
        Optional<User> oCurrentUser = userService.getCurrentUser();
        if (oCurrentUser.isEmpty()) {
            return ResponseEntity.status(401).build();
        }
        User currentUser = oCurrentUser.get();
        // CHECK: Does our requested team exist?
        Team team = teamService.getTeam(teamId);
        if (team == null) {
            return ResponseEntity.status(403).build();
        }
        // CHECK: Is the current user in the stated team?
        if (!team.getTeamMembers().contains(currentUser)) {
            return ResponseEntity.status(403).build();
        }

        // Return a JSON array of formation strings
        List<String> formations = formationService.getTeamsFormations(teamId).stream().map(Formation::getFormation).toList();

        return ResponseEntity.ok().body(formations);
    }

}
