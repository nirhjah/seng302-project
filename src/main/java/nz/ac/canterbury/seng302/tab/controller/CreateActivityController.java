package nz.ac.canterbury.seng302.tab.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nz.ac.canterbury.seng302.tab.entity.Activity;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.form.CreateActivityForm;
import nz.ac.canterbury.seng302.tab.service.ActivityService;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import nz.ac.canterbury.seng302.tab.validator.ActivityFormValidators;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Controller
public class CreateActivityController {

    @Autowired
    TeamService teamService;

    @Autowired
    UserService userService;

    @Autowired
    ActivityService activityService;

    Logger logger = LoggerFactory.getLogger(CreateActivityController.class);

    /**
     * Prefills the model with required values
     * @param model the model to be filled
     * @param httpServletRequest the request
     * @throws MalformedURLException
     */
    public void prefillModel(Model model, HttpServletRequest httpServletRequest) throws MalformedURLException {
        Optional<User> user = userService.getCurrentUser();
        model.addAttribute("firstName", user.get().getFirstName());
        model.addAttribute("lastName", user.get().getLastName());
        model.addAttribute("displayPicture", user.get().getPictureString());
        model.addAttribute("navTeams", teamService.getTeamList());
        model.addAttribute("teamList", teamService.getTeamList());
        model.addAttribute("activityTypes", Activity.ActivityType.values());
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
     * @param actId the id of activity if editting, otherwise null
     * @param activityType the type of actvity
     * @param teamId the ID of the team, (optional)
     * @param description description of the activity
     * @param startDateTime start date time of the activity
     * @param endDateTime end date time of the activity
     * @param addressLine1 address line 1 of the activity
     * @param addressLine2 address line 2 of the activity
     * @param city city the activity takes place in
     * @param country country that activity takes place in
     * @param postcode postcode of activity
     * @param suburb suburb of event
     * @param createActivityForm form used to create activity
     * @param bindingResult holds the results of validating the form
     * @param httpServletResponse the response to send
     * @param model model mapping of information
     * @param httpServletRequest the request
     * @return returns my activity page iff the details are valid, returns to activity page otherwise
     * @throws MalformedURLException thrown in some cases
     */
    @PostMapping("/createActivity")
    public String createActivity(
            @RequestParam(name = "actId", defaultValue = "-1") long actId,
            @RequestParam(name = "activityType", required = false) Activity.ActivityType activityType,
            @RequestParam(name = "team", defaultValue = "-1") long teamId,
            @RequestParam(name="description", required = false) String description,
            @RequestParam(name="startDateTime", required = false) LocalDateTime startDateTime,
            @RequestParam(name="endDateTime", required = false) LocalDateTime endDateTime,
            @RequestParam(name = "addressLine1") String addressLine1,
            @RequestParam(name = "addressLine2") String addressLine2,
            @RequestParam(name = "city") String city,
            @RequestParam(name = "country") String country,
            @RequestParam(name = "postcode") String postcode,
            @RequestParam(name = "suburb") String suburb,
            @Validated CreateActivityForm createActivityForm,
            BindingResult bindingResult,
            HttpServletResponse httpServletResponse,
            Model model,
            HttpServletRequest httpServletRequest) throws MalformedURLException {
        model.addAttribute("httpServletRequest", httpServletRequest);
        prefillModel(model, httpServletRequest);

        if (!activityService.validateStartAndEnd(startDateTime, endDateTime)) {
            if (!bindingResult.hasFieldErrors("startDateTime")) {
                bindingResult.addError(new FieldError("CreateActivityForm", "startDateTime",
                        ActivityFormValidators.END_BEFORE_START_MSG));
            }
        }
        addressLine1.trim();
        if (addressLine1.isEmpty()) {
            bindingResult.addError(new FieldError("CreateActivityForm", "addressLine1", "This is a required field"));
        }
        postcode.trim();
        if (postcode.isEmpty()) {
            bindingResult.addError(new FieldError("CreateActivityForm", "postcode", "This is a required field"));
        }

        User user = userService.getCurrentUser().get();
        Team team = teamService.getTeam(teamId);
        if (team!=null) {
            if (!activityService.validateActivityDateTime(team.getCreationDate(), startDateTime, endDateTime)) {
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
        } else if (!activityService.validateTeamSelection(activityType, team)) {
            bindingResult.addError(new FieldError("CreateActivityForm", "team",
                    ActivityFormValidators.TEAM_REQUIRED_MSG));
        }

        if (bindingResult.hasErrors()) {
            httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            if (actId != -1) {
                model.addAttribute("actId", actId);
                //return "redirect:./createActivity" + (actId != -1 ? "?edit=" + actId : "");
                Activity activity = activityService.findActivityById(actId);
                fillModelWithActivity(model, activity);
                return "createActivity";
            } else {
                return "createActivity";
            }
        }
        Location location = new Location(addressLine1, addressLine2, suburb, city, postcode, country);
        if (actId !=-1) {
            Activity editActivity = activityService.findActivityById(actId);
            editActivity.setActivityType(activityType);
            editActivity.setTeam(team);
            editActivity.setActivityEnd(endDateTime);
            editActivity.setActivityStart(startDateTime);
            editActivity.setActivityOwner(userService.getCurrentUser().get());
            editActivity.setLocation(location);
            editActivity.setDescription(description);
            editActivity = activityService.updateOrAddActivity(editActivity);
            return "redirect:./view-activities";
        } else {

            Activity activity = new Activity(activityType, team,
                    description, startDateTime, endDateTime, userService.getCurrentUser().get(), location);
            activity = activityService.updateOrAddActivity(activity);
            return "redirect:./view-activities";
        }
    }
}
