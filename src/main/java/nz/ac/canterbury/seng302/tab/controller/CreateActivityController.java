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
import java.util.Locale;
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

    @GetMapping("/createActivity")
    public String activityForm( @RequestParam(name="edit", required=false) Long actId,CreateActivityForm createActivityForm,
                                        Model model,
                                        HttpServletRequest httpServletRequest) throws MalformedURLException {
        model.addAttribute("httpServletRequest", httpServletRequest);
        prefillModel(model, httpServletRequest);
        logger.info("GET /createActivity");

        LocalDateTime startDateTime = LocalDateTime.now().plusMinutes(10);;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        String formattedStartTime = startDateTime.format(formatter);
        model.addAttribute("startDateTime", formattedStartTime);

        Activity activity;
        if (actId !=null){
            if ((activity = activityService.findActivityById(actId))!=null){

                startDateTime =  activity.getActivityStart();
                String formattedStartDateTime = startDateTime.format(formatter);

                LocalDateTime endDateTime =  activity.getActivityEnd();
                String formattedEndDateTime = endDateTime.format(formatter);

                model.addAttribute("activityType", activity.getActivityType());
                model.addAttribute("teamName", activity.getTeam().getName());
                model.addAttribute("actId", activity.getId());
                model.addAttribute("startDateTime",formattedStartDateTime);
                model.addAttribute("endDateTime", formattedEndDateTime);
                model.addAttribute("description", activity.getDescription());
            }

        }
        return "createActivity";
    }

    @PostMapping("/createActivity")
    public String createActivity(
            @RequestParam(name = "activityType", required = false) Activity.ActivityType activityType,
            @RequestParam(name = "team", defaultValue = "-1") long teamId,
            @RequestParam(name="description", required = false) String description,
            @RequestParam(name="startDateTime", required = false) LocalDateTime startDateTime,
            @RequestParam(name="endDateTime", required = false) LocalDateTime endDateTime,
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

        Team team = teamService.getTeam(teamId);
        if (team != null) {
            if (!activityService.validateActivityDateTime(team.getCreationDate(), startDateTime, endDateTime)) {
                if (!bindingResult.hasFieldErrors("endDateTime")) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
                    bindingResult.addError(new FieldError("CreateActivityForm", "endDateTime",
                            ActivityFormValidators.ACTIVITY_BEFORE_TEAM_CREATION + team.getCreationDate().format(formatter)));
                }

            }
        } else if (!activityService.validateTeamSelection(activityType, team)) {
            bindingResult.addError(new FieldError("CreateActivityForm", "team",
                    ActivityFormValidators.TEAM_REQUIRED_MSG));
        }

        if (bindingResult.hasErrors()) {
            httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "createActivity";
        }

        Location location = new Location(null, null, null, "chch", null, "nz");

        Activity activity = new Activity(activityType, team,
                description, startDateTime, endDateTime, userService.getCurrentUser().get(), location);
        activity = activityService.updateOrAddActivity(activity);
        return String.format("redirect:./activity?actId=%s", activity.getId());
    }
}
