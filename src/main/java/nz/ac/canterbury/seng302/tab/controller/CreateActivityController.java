package nz.ac.canterbury.seng302.tab.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nz.ac.canterbury.seng302.tab.entity.Activity;
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

    public void prefillModel(Model model) {
        Optional<User> user = userService.getCurrentUser();
        model.addAttribute("firstName", user.get().getFirstName());
        model.addAttribute("lastName", user.get().getLastName());
        model.addAttribute("displayPicture", user.get().getPictureString());
        model.addAttribute("navTeams", teamService.getTeamList());
        model.addAttribute("teamList", teamService.getTeamList());
        model.addAttribute("activityTypes", Activity.ActivityType.values());
    }

    @GetMapping("/createActivity")
    public String activityForm(CreateActivityForm createActivityForm,
                                        Model model,
                                        HttpServletRequest httpServletRequest) {
        model.addAttribute("httpServletRequest", httpServletRequest);
        prefillModel(model);
        logger.info("GET /createActivity");
        return "createActivity";
    }

    @PostMapping("/createActivity")
    public String createActivity(@Validated CreateActivityForm createActivityForm,
                                 BindingResult bindingResult,
                                 HttpServletResponse httpServletResponse,
                                 Model model,
                                 HttpServletRequest httpServletRequest) {
        model.addAttribute("httpServletRequest", httpServletRequest);
        prefillModel(model);

        if (!activityService.validateStartAndEnd(createActivityForm.getStartDateTime(), createActivityForm.getEndDateTime())) {
            if (!bindingResult.hasFieldErrors("startDateTime")) {
                bindingResult.addError(new FieldError("CreateActivityForm", "startDateTime",
                        ActivityFormValidators.END_BEFORE_START_MSG));
            }
        }

        Team team = teamService.getTeam(createActivityForm.getTeam());
        if (team != null) {
            if (!activityService.validateActivityDateTime(team.getCreationDate(),
                    createActivityForm.getStartDateTime(), createActivityForm.getEndDateTime())) {
                if (!bindingResult.hasFieldErrors("endDateTime")) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
                    bindingResult.addError(new FieldError("CreateActivityForm", "endDateTime",
                            ActivityFormValidators.ACTIVITY_BEFORE_TEAM_CREATION + team.getCreationDate().format(formatter)));
                }

            }
        } else if (!activityService.validateTeamSelection(createActivityForm.getActivityType(), team)) {
            bindingResult.addError(new FieldError("CreateActivityForm", "team",
                    ActivityFormValidators.TEAM_REQUIRED_MSG));
        }

        if (bindingResult.hasErrors()) {
            httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "createActivity";
        }

        Activity activity = new Activity(createActivityForm.getActivityType(), team,
                createActivityForm.getDescription(), createActivityForm.getStartDateTime(),
                createActivityForm.getEndDateTime(), userService.getCurrentUser().get());
        activity = activityService.updateOrAddActivity(activity);
        return String.format("redirect:./activity?actId=%s", activity.getId());
    }
}
