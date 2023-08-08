package nz.ac.canterbury.seng302.tab.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nz.ac.canterbury.seng302.tab.entity.*;
import nz.ac.canterbury.seng302.tab.entity.competition.Competition;
import nz.ac.canterbury.seng302.tab.entity.competition.TeamCompetition;
import nz.ac.canterbury.seng302.tab.entity.competition.UserCompetition;
import nz.ac.canterbury.seng302.tab.form.CreateAndEditCompetitionForm;
import nz.ac.canterbury.seng302.tab.response.competition.CompetitionTeamInfo;
import nz.ac.canterbury.seng302.tab.response.competition.CompetitionUserInfo;
import nz.ac.canterbury.seng302.tab.service.CompetitionService;
import nz.ac.canterbury.seng302.tab.service.SportService;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import nz.ac.canterbury.seng302.tab.validator.CompetitionFormValidators;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
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

    private final CompetitionService competitionService;

    private final UserService userService;

    private final TeamService teamService;

    private final SportService sportsService;

    @Autowired
    public CreateCompetitionController(CompetitionService competitionService,UserService userService, TeamService teamService, SportService sportsService) {
        this.competitionService = competitionService;
        this.userService = userService;
        this.teamService = teamService;
        this.sportsService = sportsService;
    }

    /**
     * Gets Competition form
     * @param competitionID  if editing, get competition through competitionID
     * @param model   model
     * @param request http request
     * @return create club form
     */
    @GetMapping("/createCompetition")
    public String clubForm(@RequestParam(name = "edit", required = false) Long competitionID, CreateAndEditCompetitionForm form,
                           Model model,
                           HttpServletRequest request) {

        logger.info("GET /createCompetition");
        prefillModel(model, request);
        if (competitionID != null) {
            Optional<Competition> optionalCompetition = competitionService.findCompetitionById(competitionID);
            optionalCompetition.ifPresent(competition -> prefillModelWithCompetition(model, competition));
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
    @PostMapping("/createCompetition")
    public String createCompetition(
            @RequestParam(name = "competitionID", defaultValue = "-1") long competitionID,
            @RequestParam("usersOrTeams") String usersOrTeams,
            @RequestParam("userTeamID") List<Long> IDs,
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

        postCreateActivityErrorChecking(bindingResult, form, IDs);


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

            if (usersOrTeams.equals("teams")) {
                Set<Team> teams = IDs.stream()
                        .map(teamService::getTeam)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());
                ((TeamCompetition) editCompetition).setTeams(teams);
            } else {
                Set<User> users = IDs.stream()
                        .map(userService::getUser)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());
                ((UserCompetition) editCompetition).setPlayers(users);
            }

            competitionService.updateOrAddCompetition(editCompetition);
            return "redirect:/view-competition?competitionID=" + editCompetition.getCompetitionId();

        } else {
            Competition competition;

            if (!form.getTeams().isEmpty()) {
                Set<Team> teams = IDs.stream()
                        .map(teamService::getTeam)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());
                competition = new TeamCompetition(form.getName(), form.getGrade(), form.getSport(), form.getLocation(), teams);
            } else {
                Set<User> users = IDs.stream()
                        .map(userService::getUser)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());
                competition = new UserCompetition(form.getName(), form.getGrade(), form.getSport(), form.getLocation(), users);
            }

            competitionService.updateOrAddCompetition(competition);
            return "redirect:/view-competition?competitionID=" + competition.getCompetitionId();

        }
    }

    /**
     * Performs various error checks for this form, on top of the Jakarta annotations in the form.
     * @param bindingResult Any found errors are added to this
     * @param form The form containing the data we're validating
     */
    private void postCreateActivityErrorChecking(
            BindingResult bindingResult,
            CreateAndEditCompetitionForm form,
            List<Long> IDs) {

        // The competition requires a team or a user to be selected
        if (IDs.size()==0) {
            bindingResult.addError(new FieldError("CreateAndEditCompetitionForm", "competitors",
                    CompetitionFormValidators.NO_COMPETITORS_MSG));
        }

       // All the grade attributes must be selected
        if (form.getAge() == null || form.getSex() == null || form.getCompetitiveness() == null) {
            bindingResult.addError(new FieldError("CreateAndEditCompetitionForm", "grade",
                    CompetitionFormValidators.NO_GRADE_MSG));
        }
    }

    /**
     * Prefills model with competition fields
     *
     * @param model model to be filled
     * @param competition  competition to get info from
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
        model.addAttribute("presetAges", Grade.Age.values());
        model.addAttribute("presetSexes", Grade.Sex.values());
        model.addAttribute("presetGenders", Grade.Competitiveness.values());


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
     * Creates JSON object of type List<Team>
     * @param sport sport for teams
     * @return JSON object of type List<Team>
     */
    @GetMapping(path = "/createCompetition/get_teams", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CompetitionTeamInfo>> getTeamsJSON(@RequestParam String sport,
                                                            @RequestParam(required = false, defaultValue = "") String search) {
        return ResponseEntity.ok().body(
                teamService.findTeamsBySportAndSearch(sport, search).stream().map(team -> new CompetitionTeamInfo(
                        team.getTeamId(),
                        team.getName(),
                        team.getPictureString()
                )).toList()
        );
    }

    /**
     * Creates JSON object of type List<User>. Users can have multiple sports so approach is different
     * @param sport sport for users
     * @return JSON object of type List<User>
     */
    @GetMapping(path = "/createCompetition/get_users", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CompetitionUserInfo>> getUsersJSON(@RequestParam String sport,
                                                                  @RequestParam(required = false, defaultValue = "") String search) {
        return ResponseEntity.ok().body(
                userService.findUsersBySportAndName(sport, search).stream().map(user -> new CompetitionUserInfo(
                        user.getUserId(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getPictureString()
                )).toList()
        );
    }


}
