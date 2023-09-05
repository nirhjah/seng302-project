package nz.ac.canterbury.seng302.tab.controller;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.entity.competition.Competition;
import nz.ac.canterbury.seng302.tab.entity.competition.TeamCompetition;
import nz.ac.canterbury.seng302.tab.entity.competition.UserCompetition;
import nz.ac.canterbury.seng302.tab.service.CompetitionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.time.format.DateTimeFormatter;
import java.util.Set;

/**
 * Spring Boot Controller class for the View Activity Page
 */
@Controller
public class ViewCompetitionController {

    Logger logger = LoggerFactory.getLogger(getClass());

    private CompetitionService competitionService;

    @Autowired
    public ViewCompetitionController(CompetitionService competitionService) {
        this.competitionService = competitionService;
    }

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy");

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

        Competition competition = competitionService.findCompetitionById(competitionID)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Competition does not exist"));

        model.addAttribute("competition", competition);

        if (competition instanceof TeamCompetition tc) {
            Set<Team> teams = tc.getTeams();
            model.addAttribute("teams", teams);
        } else if (competition instanceof UserCompetition uc) {
            Set<User> players = uc.getPlayers();
            model.addAttribute("players", players);
        } else {
            throw new IllegalArgumentException("Competition of unknown type: " + competition);
        }

        String displayDate = competition.getCompetitionStartDate().format(DATE_FORMATTER);
        model.addAttribute("displayDate", displayDate);

        // Rambling that's required for navBar.html
        model.addAttribute("httpServletRequest", request);

        return "viewCompetition";
    }

}
