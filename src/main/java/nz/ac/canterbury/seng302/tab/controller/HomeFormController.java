package nz.ac.canterbury.seng302.tab.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import nz.ac.canterbury.seng302.tab.entity.Sport;
import nz.ac.canterbury.seng302.tab.service.SportService;
import nz.ac.canterbury.seng302.tab.service.TeamService;

/**
 * Spring Boot Controller class for the Home Form class.
 */
@Controller
public class HomeFormController {
    Logger logger = LoggerFactory.getLogger(HomeFormController.class);

    @Autowired
    private TeamService teamService;

    @Autowired
    private SportService sportService;

    /**
     * Redirects GET default url '/' to '/home'
     *
     * @return redirect to /home
     */
    @GetMapping("/")
    public String home() {
        logger.info("GET /homeForm");
        return "redirect:./home";
    }

    /**
     * Gets the thymeleaf page representing the /home page (a basic welcome screen with nav bar)
     *
     * @param model (map-like) representation of data to be used in thymeleaf display
     * @return thymeleaf homeForm
     */
    @GetMapping("/home")
    public String getTemplate(Model model) {
        logger.info("GET /homeForm");
        model.addAttribute("navTeams", teamService.getTeamList());
        return "homeForm";
    }

    /*
    TODO: REMOVE FROM BRANCH ONCE ADDING SPORTS IS IMPLEMENTED
    */
    @GetMapping("/addSport")
    public String addSport() {
        Sport sport = new Sport("Hockey");

        sportService.addSport(sport);
        System.out.println(sportService.getAllSports());
        return "redirect:./home";
    }
}

