package nz.ac.canterbury.seng302.tab.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nz.ac.canterbury.seng302.tab.entity.Activity;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.form.CreateActivityForm;
import nz.ac.canterbury.seng302.tab.service.ActivityService;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
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
    }

    @GetMapping("/createActivity")
    public String activityForm(CreateActivityForm createActivityForm,
                                        Model model,
                                        HttpServletRequest httpServletRequest) {
        model.addAttribute("httpServletRequest", httpServletRequest);
        prefillModel(model);
        logger.info("GET /createActivity");
        model.addAttribute("teamList", teamService.getTeamList());
        model.addAttribute("activityTypes", Activity.ActivityType.values());
        return "createActivity";
    }

    @PostMapping("/createActivity")
    public String createActivity(@Validated CreateActivityForm createActivityForm,
                                 @RequestParam(name="startDateTime") LocalDateTime startDateTime,
                                 @RequestParam(name="endDateTime") LocalDateTime endDateTime,
                                 BindingResult bindingResult,
                                 HttpServletResponse httpServletResponse,
                                 Model model,
                                 HttpServletRequest httpServletRequest) {
        model.addAttribute("httpServletRequest", httpServletRequest);
        prefillModel(model);
        if (!activityService.validateStartAndEnd(startDateTime, endDateTime)) {
            bindingResult.addError(new FieldError("CreateActivityForm", "startTime", "Start must be before end"));
        }
        if (createActivityForm.getTeam() != null &&
                !activityService.validateActivityDateTime(createActivityForm.getTeam().getCreationDate(),
                        createActivityForm.getStartDateTime(), createActivityForm.getEndDateTime())) {
            bindingResult.addError(new FieldError("CreateActivityForm", "endTime",
                    "Activity must occur after team creation: " +
                            createActivityForm.getTeam().getCreationDate().toString()));
        }
        if (bindingResult.hasErrors()) {
            httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "createActivity";
        }

        Activity activity = new Activity(createActivityForm.getActivityType(), createActivityForm.getTeam(),
                createActivityForm.getDescription(), createActivityForm.getStartDateTime(),
                createActivityForm.getEndDateTime(), userService.getCurrentUser().get());
        activity = activityService.updateOrAddActivity(activity);
        return String.format("redirect:./activity?actId=%s", activity.getId());
    }
}
