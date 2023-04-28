package nz.ac.canterbury.seng302.tab.controller;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.tab.form.CreateActivityForm;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CreateActivityController {

    @Autowired
    TeamService teamService;

    Logger logger = LoggerFactory.getLogger(CreateActivityController.class);

    @GetMapping("/createActivity")
    public String activityForm(CreateActivityForm createActivityForm,
                                        Model model,
                                        HttpServletRequest httpServletRequest) {
        logger.info("GET /createActivity");
        model.addAttribute("teamList", teamService.getAllTeamNames());
        return "createActivity";
    }
}
