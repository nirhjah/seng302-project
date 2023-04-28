package nz.ac.canterbury.seng302.tab.controller;


import nz.ac.canterbury.seng302.tab.service.TeamService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


/**
 * Spring Boot Controller for My Teams Form
 */
@Controller
public class MyTeamsController {

    Logger logger = LoggerFactory.getLogger(MyTeamsController.class);

    @Autowired
    TeamService teamService;


    @GetMapping("/my-teams")
    public String myTeams(Model model) {

        model.addAttribute("navTeams", teamService.getTeamList());

        logger.info("GET /my-teams");


        return "myTeams";
    }
}
