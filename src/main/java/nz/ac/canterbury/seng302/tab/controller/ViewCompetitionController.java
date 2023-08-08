package nz.ac.canterbury.seng302.tab.controller;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.tab.entity.Grade;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.entity.competition.Competition;
import nz.ac.canterbury.seng302.tab.entity.competition.UserCompetition;
import nz.ac.canterbury.seng302.tab.service.CompetitionService;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;

/**
 * Spring Boot Controller class for the View Activity Page
 */
@Controller
public class ViewCompetitionController {


    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private TeamService teamService;

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private UserService userService;


    String createEventFormBindingResult = "createEventFormBindingResult";

    @Autowired
    public ViewCompetitionController(UserService userService, CompetitionService competitionService, TeamService teamService) {
        this.userService = userService;
        this.competitionService = competitionService;
        this.teamService = teamService;
    }

    /**
     *
     * @param model         the model to be filled
     * @param competitionId the competition id of the competition to be displayed on the page
     * @param request       http request
     * @return              view activity page
     */
    @GetMapping("/view-competition")
    public String viewActivityPage(
            Model model,
            @RequestParam(value = "competitionId") Long competitionId,
            HttpServletRequest request) {

        Competition competition = competitionService.findCompetitionById(competitionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatusCode.valueOf(404)));

        model.addAttribute("competition", competition);

        // Rambling that's required for navBar.html
        model.addAttribute("httpServletRequest", request);

        return "viewCompetition";
    }


    /**
     * PLEASE DELETE ME ONCE CREATING COMPETITIONS IS ALLOWED
     */
    @GetMapping("/test-create-user-competition")
    public String testCreateUserCompetition() {
        Grade grade = new Grade(Grade.Age.OVER_50S, Grade.Sex.WOMENS, Grade.Competitiveness.SOCIAL);
        Location location = new Location(null, null, null, "Chch", "90210", "NZ");
        Set<User> players = Set.of(userService.getCurrentUser().orElseThrow());

        Competition userComp = new UserCompetition("CoolUser", grade, "CoolSport", location, players);

        userComp = competitionService.updateOrAddCompetition(userComp);

        return "redirect:view-competition?competitionId=" + userComp.getCompetitionId();
    }

}
