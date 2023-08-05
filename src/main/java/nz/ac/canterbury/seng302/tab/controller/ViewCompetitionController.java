package nz.ac.canterbury.seng302.tab.controller;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.tab.entity.Club;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.entity.competition.Competition;
import nz.ac.canterbury.seng302.tab.entity.competition.TeamCompetition;
import nz.ac.canterbury.seng302.tab.entity.competition.UserCompetition;
import nz.ac.canterbury.seng302.tab.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.Set;

/**
 * Spring Boot Controller class for the View Activity Page
 */
@Controller
public class ViewCompetitionController {


    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private UserService userService;

    @Autowired
    public ViewCompetitionController(CompetitionService competitionService) {
        this.competitionService = competitionService;
    }

    /**
     *
     * @param model         the model to be filled
     * @param competitionID the competition iD of the competition to be displayed on the page
     * @param request       http request
     * @return              view activity page
     */
    @GetMapping("/view-competition")
    public String viewActivityPage(
            Model model,
            @RequestParam(value = "competitionID") Long competitionID,
            HttpServletRequest request) {
        logger.info("GET /view-competition");

        Optional<User> user = userService.getCurrentUser();
        if (user.isEmpty()) {
            logger.error("No current user?");
            return "redirect:/home";
        }

        Optional<Competition> competitionOptional = competitionService.findCompetitionById(competitionID);
        if (competitionOptional.isEmpty()) {
//            throw new ResponseStatusException(HttpStatusCode.valueOf(404));
            return "redirect:/home";
        }

        Competition competition = competitionOptional.get();

        if (competition instanceof TeamCompetition) {
            Set<Team> teams = ((TeamCompetition) competition).getTeams();
            model.addAttribute("teams", teams);
        } else {
            Set<User> players = ((UserCompetition) competition).getPlayers();
            model.addAttribute("players", players);
        }


        model.addAttribute("competition", competition);

        // Rambling that's required for navBar.html
        model.addAttribute("httpServletRequest", request);

        return "viewCompetition";
    }

}
