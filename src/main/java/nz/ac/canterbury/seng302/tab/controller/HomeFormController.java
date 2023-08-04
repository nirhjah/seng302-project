package nz.ac.canterbury.seng302.tab.controller;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.enums.AuthorityType;
import nz.ac.canterbury.seng302.tab.service.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import nz.ac.canterbury.seng302.tab.entity.Sport;
import nz.ac.canterbury.seng302.tab.service.TeamService;

import java.util.Optional;

/**
 * Spring Boot Controller class for the Home Form class.
 */
@Controller
public class HomeFormController {
    Logger logger = LoggerFactory.getLogger(HomeFormController.class);

    @Autowired
    private TeamService teamService;

    @Autowired
    private UserService userService;

    @Autowired
    public HomeFormController(UserService userService, TeamService teamService) {
        this.userService = userService;
        this.teamService = teamService;
    }

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


        // test

        Pageable pageable = PageRequest.of(0, 10);
        Page<User> test = userService.getAllUsersNotFedMans(pageable);
        for (User user : test) {
            System.out.println("User ID: " + user.getUserId());
            System.out.println("First Name: " + user.getFirstName());
            System.out.println("Last Name: " + user.getLastName());
            System.out.println("Date of Birth: " + user.getDateOfBirth());
            // Add other relevant user details you want to print...
            System.out.println("------------------------------------");

        }

        return "homeForm";
    }
}

