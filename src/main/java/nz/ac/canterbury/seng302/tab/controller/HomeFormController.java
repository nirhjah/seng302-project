package nz.ac.canterbury.seng302.tab.controller;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.service.LocationService;

import nz.ac.canterbury.seng302.tab.repository.TeamRepository;
import nz.ac.canterbury.seng302.tab.service.TeamService;
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
    private LocationService locationService;
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
    public String getTemplate(Model model, HttpServletRequest request) {
        logger.info("GET /homeForm");
        model.addAttribute("httpServletRequest", request);
        model.addAttribute("navTeams", teamService.getTeamList());
        return "homeForm";
    }
}

