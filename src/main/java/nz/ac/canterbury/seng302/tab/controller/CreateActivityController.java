package nz.ac.canterbury.seng302.tab.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.form.CreateActivityForm;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;

@Controller
public class CreateActivityController {

    @Autowired
    TeamService teamService;

    @Autowired
    UserService userService;

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
        model.addAttribute("teamList", teamService.getAllTeamNames());
        return "createActivity";
    }

    @PostMapping("/createActivity")
    public String createActivity(@Validated CreateActivityForm createActivityForm,
                                 BindingResult bindingResult,
                                 HttpServletResponse httpServletResponse,
                                 Model model,
                                 HttpServletRequest httpServletRequest) {
        return "createActivity";
    }
}
