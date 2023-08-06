package nz.ac.canterbury.seng302.tab.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nz.ac.canterbury.seng302.tab.entity.*;
import nz.ac.canterbury.seng302.tab.entity.competition.Competition;
import nz.ac.canterbury.seng302.tab.entity.competition.TeamCompetition;
import nz.ac.canterbury.seng302.tab.entity.competition.UserCompetition;
import nz.ac.canterbury.seng302.tab.form.CreateAndEditCompetitionForm;
import nz.ac.canterbury.seng302.tab.service.CompetitionService;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Spring Boot Controller class for the Create Competition Form
 */
@Controller
public class CreateCompetitionController {

    Logger logger = LoggerFactory.getLogger(CreateCompetitionController.class);

    private CompetitionService competitionService;

    private final UserService userService;

    private final TeamService teamService;

    @Autowired
    public CreateCompetitionController(CompetitionService competitionService,UserService userService, TeamService teamService) {
        this.competitionService = competitionService;
        this.userService = userService;
        this.teamService = teamService;
    }

    /**
     * Gets Competition form
     * @param competitionID  if editing, get competition through competitionID
     * @param model   model
     * @param request http request
     * @return create club form
     */
    @GetMapping("/createCompetition")
    public String clubForm(@RequestParam(name = "edit", required = false) Long competitionID,
                           Model model,
                           HttpServletRequest request) {

        logger.info("GET /createCompetition");
        prefillModel(model, request);
        if (competitionID != null) {
            Optional<Competition> optionalCompetition = competitionService.findCompetitionById(competitionID);
            if (optionalCompetition.isPresent()) {
                prefillModelWithCompetition(model, optionalCompetition.get());
            }
        }
        return "createCompetitionForm";
    }

    /**
     * Handles the creation or editing of a competition based on the provided form data.
     * This method is invoked when a POST request is made to "/createCompetition" endpoint.
     *
     * @param competitionID                The ID of the competition being edited.
     * @param form The form object containing additional competition-related data for validation.
     * @param bindingResult         The result of data binding and validation for form object.
     * @param httpServletResponse   The response object used for setting HTTP status codes.
     * @param model                 The model object to hold attributes for the view.
     * @param httpServletRequest    The request object to get additional information about the HTTP request.
     * @return The view name to be displayed after the competition creation/edit process.
     * @throws IOException If an I/O error occurs during file handling.
     */
    @PostMapping("/create")
    public String createCompetition(
            @RequestParam(name = "competitionID", defaultValue = "-1") long competitionID,
            @Validated CreateAndEditCompetitionForm form,
            BindingResult bindingResult,
            HttpServletResponse httpServletResponse,
            Model model,
            HttpServletRequest httpServletRequest) throws IOException {
        prefillModel(model, httpServletRequest);

        Optional<User> optUser = userService.getCurrentUser();
        if (optUser.isEmpty()) {
            return "redirect:/home";
        }

        if (bindingResult.hasErrors()) {
            System.out.println(bindingResult.getAllErrors());
            httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "createCompetitionForm";
        }

        Optional<Competition> optionalCompetition = competitionService.findCompetitionById(competitionID);
        if (optionalCompetition.isPresent()) {

            Competition editCompetition = optionalCompetition.get();
            editCompetition.setName(form.getName());
            editCompetition.setSport(form.getSport());
            editCompetition.setGrade(form.getGrade());
            editCompetition.setLocation(form.getLocation());

            if (editCompetition instanceof TeamCompetition) {
                ((TeamCompetition) editCompetition).setTeams(form.getTeams());
            } else {
                ((UserCompetition) editCompetition).setPlayers(form.getPlayers());
            }

            competitionService.updateOrAddCompetition(editCompetition);
            return "redirect:/view-competition?competitionID=" + editCompetition.getCompetitionId();

        } else {
            Competition competition;

            if (!form.getTeams().isEmpty()) {
                competition = new TeamCompetition(form.getName(), form.getGrade(), form.getSport(), form.getLocation(), form.getTeams());
            } else {
                competition = new UserCompetition(form.getName(), form.getGrade(), form.getSport(), form.getLocation(), form.getPlayers());
            }

            competitionService.updateOrAddCompetition(competition);
            return "redirect:/view-competition?competitionID=" + competition.getCompetitionId();

        }
    }

    /**
     * Prefills model with club fields
     * @param model model to be filled
     * @param competition  club to get info from
     */
    public void prefillModelWithCompetition(Model model, Competition competition) {
        model.addAttribute("competitionID", competition.getCompetitionId());
        model.addAttribute("name", competition.getName());
        model.addAttribute("sport", competition.getSport());
        model.addAttribute("age", competition.getGrade().getAge());
        model.addAttribute("gender", competition.getGrade().getSex());
        model.addAttribute("level", competition.getGrade().getCompetitiveness());
        model.addAttribute("addressLine1", competition.getLocation().getAddressLine1());
        model.addAttribute("addressLine2", competition.getLocation().getAddressLine2());
        model.addAttribute("suburb", competition.getLocation().getSuburb());
        model.addAttribute("postcode", competition.getLocation().getPostcode());
        model.addAttribute("city", competition.getLocation().getCity());
        model.addAttribute("country", competition.getLocation().getCountry());
        if (competition instanceof TeamCompetition) {
            model.addAttribute("teams", ((TeamCompetition) competition).getTeams());
        } else {
            model.addAttribute("users", ((UserCompetition) competition).getPlayers());
        }

    }

    /**
     * Prefill model with info required from navbar, as well as the list of teams a user is manager of
     * @param model model to be filled
     */
    public void prefillModel(Model model, HttpServletRequest httpServletRequest) {
        Optional<User> user = userService.getCurrentUser();
        List<Team> teamsUserManagerOf = new ArrayList<>();
        for (Team team : teamService.getTeamList()) {
            if (team.isManager(user.get())) {
                teamsUserManagerOf.add(team);
            }
        }
        model.addAttribute("httpServletRequest", httpServletRequest);
        model.addAttribute("listOfTeams", teamsUserManagerOf);
    }

    /**
     * A JSON API endpoint, which gets all teams of given sport. Used by the createActivity page to update
     * @return A json object of type <code>{formationId: "formationString", ...}</code>
     */
    @GetMapping(path = "/createActivity/get_team_formation", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<Long, String>> getTeamFormation(@RequestParam("teamId") long teamId) {
        logger.info("GET /createActivity/get_team_formation");
        // CHECK: Are we logged in?
        Optional<User> oCurrentUser = userService.getCurrentUser();
        if (oCurrentUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User currentUser = oCurrentUser.get();
        // CHECK: Does our requested team exist?
        Team team = teamService.getTeam(teamId);
        if (team == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        // CHECK: Do you have permission to edit this team's formations?
        // (This doubles as an "Are you in this team" check)
        if (!team.isCoach(currentUser) && !team.isManager(currentUser)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Return a JSON object of (id -> string)
        Map<Long, String> formations = formationService.getTeamsFormations(teamId).stream()
                .collect(Collectors.toMap(Formation::getFormationId, Formation::getFormation));

        return ResponseEntity.ok().body(formations);
    }

}
