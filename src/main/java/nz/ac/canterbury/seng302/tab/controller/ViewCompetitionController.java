package nz.ac.canterbury.seng302.tab.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nz.ac.canterbury.seng302.tab.entity.Activity;
import nz.ac.canterbury.seng302.tab.entity.Fact.Fact;
import nz.ac.canterbury.seng302.tab.entity.Fact.Goal;
import nz.ac.canterbury.seng302.tab.entity.Fact.OppositionGoal;
import nz.ac.canterbury.seng302.tab.entity.Fact.Substitution;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.entity.competition.Competition;
import nz.ac.canterbury.seng302.tab.entity.competition.TeamCompetition;
import nz.ac.canterbury.seng302.tab.entity.competition.UserCompetition;
import nz.ac.canterbury.seng302.tab.enums.ActivityType;
import nz.ac.canterbury.seng302.tab.enums.FactType;
import nz.ac.canterbury.seng302.tab.form.CreateEventForm;
import nz.ac.canterbury.seng302.tab.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

        Optional<Competition> competitionOptional = competitionService.findCompetitionById(competitionId);
        if (competitionOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(404));
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
