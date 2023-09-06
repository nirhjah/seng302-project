package nz.ac.canterbury.seng302.tab.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nz.ac.canterbury.seng302.tab.entity.Grade;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.entity.competition.Competition;
import nz.ac.canterbury.seng302.tab.entity.competition.TeamCompetition;
import nz.ac.canterbury.seng302.tab.entity.competition.UserCompetition;
import nz.ac.canterbury.seng302.tab.form.CreateAndEditCompetitionForm;
import nz.ac.canterbury.seng302.tab.response.competition.CompetitionTeamInfo;
import nz.ac.canterbury.seng302.tab.response.competition.CompetitionUserInfo;
import nz.ac.canterbury.seng302.tab.service.CompetitionService;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import nz.ac.canterbury.seng302.tab.validator.CompetitionFormValidators;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final String TEAM_ATTR = "teams";
    private static final String USER_ATTR = "users";

    public CreateCompetitionController(CompetitionService competitionService, UserService userService, TeamService teamService) {
        this.competitionService = competitionService;
        this.userService = userService;
        this.teamService = teamService;
    }

    /**
     * Gets Competition form
     * @param competitionID  if editing, get competition through competitionID
     * @param model   model
     * @param request http request
     * @return create competition form
     */
    @GetMapping("/create-competition")
    public String createCompetitionForm(@RequestParam(name = "edit", required = false) Long competitionID, CreateAndEditCompetitionForm form,
                           Model model,
                           HttpServletRequest request) {

        logger.info("GET /create-competition");
        prefillModel(model, request);
        if (competitionID != null) {
            Optional<Competition> optionalCompetition = competitionService.findCompetitionById(competitionID);
            optionalCompetition.ifPresent(competition -> prefillFormWithCompetition(model, form, competition));
        }
        return "createCompetitionForm";
    }

    /**
     * Takes a list of Ids.
     * If the competition is a TeamCompetition,
     * Adds teams to a competition.
     * If the competition is a UserCompetition,
     * adds users instead.
     * @param competition The competition to edit
     * @param IDs A list of ids. These are primary keys.
     */
    private void editCompetitionParticipants(Competition competition, List<Long> IDs) {
        if (competition instanceof TeamCompetition teamCompetition) {
            Set<Team> teams = IDs.stream()
                    .map(teamService::getTeam)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            teamCompetition.setTeams(teams);
        } else if (competition instanceof UserCompetition userCompetition) {
            Set<User> users = IDs.stream()
                    .map(userService::getUser)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            userCompetition.setPlayers(users);
        } else {
            // should never happen
            logger.error("Unknown competition type: {}", competition.toString());
        }
    }

    /**
     * Edits an existing competition, according to the form passed in.
     * @param competition The competition to edit
     * @param form The form passed in by the user
     */
    private void editExistingCompetition(Competition competition, CreateAndEditCompetitionForm form) {
        competition.setName(form.getName());
        competition.setSport(form.getSport());
        competition.setGrade(form.getGrade());
        competition.setLocation(form.getLocation());
        competition.setDate(form.getStartDateTime(), form.getEndDateTime());
    }

    private Set<Team> getTeamsFromIds(List<Long> IDs) {
        return IDs.stream()
                .map(teamService::getTeam)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private Set<User> getUsersFromIds(List<Long> IDs) {
        return IDs.stream()
                .map(userService::getUser)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private void addIdsToModel(Model model, List<Long> IDs, boolean isTeamCompetition) {
        if (isTeamCompetition) {
            Set<Team> teams = getTeamsFromIds(IDs);
            model.addAttribute(TEAM_ATTR, teams);
        } else {
            // else, its a user competition
            Set<User> users = getUsersFromIds(IDs);
            model.addAttribute(USER_ATTR, users);
        }
    }

    /**
     * Handles the creation or editing of a competition based on the provided form data.
     * This method is invoked when a POST request is made to "/create-competition" endpoint.
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
    @PostMapping("/create-competition")
    public String createCompetition(
            @RequestParam(name = "competitionID", defaultValue = "-1") long competitionID,
            @RequestParam("usersOrTeams") String usersOrTeams,
            @RequestParam(name = "userTeamID", required = false) List<Long> IDs,
            @Validated CreateAndEditCompetitionForm form,
            BindingResult bindingResult,
            HttpServletResponse httpServletResponse,
            Model model,
            HttpServletRequest httpServletRequest) {
        prefillModel(model, httpServletRequest);

        Optional<User> optUser = userService.getCurrentUser();
        if (optUser.isEmpty()) {
            return "redirect:/home";
        }

        postCreateActivityErrorChecking(bindingResult, form, IDs);
        boolean isTeamCompetition = usersOrTeams.equals("teams");

        if (bindingResult.hasErrors()) {
            httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            if (IDs != null) {
                // We still add IDs, even though there was an error,
                // because we don't want the client to lose their work.
                addIdsToModel(model, IDs, isTeamCompetition);
            }
            return "createCompetitionForm";
        }

        Optional<Competition> optionalCompetition = competitionService.findCompetitionById(competitionID);
        if (optionalCompetition.isPresent()) {
            // Then we are editing:
            Competition editCompetition = optionalCompetition.get();

            editExistingCompetition(editCompetition, form);
            editCompetitionParticipants(editCompetition, IDs);

            editCompetition = competitionService.updateOrAddCompetition(editCompetition);
            return "redirect:/view-competition?competitionID=" + editCompetition.getCompetitionId();

        } else {
            // Else, create a new competition:
            Competition competition;

            if (isTeamCompetition) {
                Set<Team> teams = getTeamsFromIds(IDs);
                competition = new TeamCompetition(form.getName(), form.getGrade(), form.getSport(), form.getLocation(), form.getStartDateTime(), form.getEndDateTime(), teams);
            } else {
                Set<User> users = getUsersFromIds(IDs);
                competition = new UserCompetition(form.getName(), form.getGrade(), form.getSport(), form.getLocation(), form.getStartDateTime(), form.getEndDateTime(), users);
            }

            competition = competitionService.updateOrAddCompetition(competition);
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
        if (IDs == null || IDs.isEmpty()) {
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
     * Prefills form with competition fields
     *
     * @param model to be populated
     * @param form form to be populated
     * @param competition  competition to get info from
     */
    public void prefillFormWithCompetition(Model model, CreateAndEditCompetitionForm form, Competition competition) {
        model.addAttribute("competitionID", competition.getCompetitionId());

        form.setName(competition.getName());
        form.setSport(competition.getSport());

        Grade grade = competition.getGrade();
        form.setAge(grade.getAge());
        form.setSex(grade.getSex());
        form.setCompetitiveness(grade.getCompetitiveness());

        form.setStartDateTime(competition.getCompetitionStartDate());
        form.setEndDateTime(competition.getCompetitionEndDate());

        Location location = competition.getLocation();
        if (location != null) {
            form.setAddressLine1(location.getAddressLine1());
            form.setAddressLine2(location.getAddressLine2());
            form.setSuburb(location.getSuburb());
            form.setPostcode(location.getPostcode());
            form.setCity(location.getCity());
            form.setCountry(location.getCountry());
        }
        if (competition instanceof TeamCompetition) {
            model.addAttribute(TEAM_ATTR, ((TeamCompetition) competition).getTeams());
        } else {
            model.addAttribute(USER_ATTR, ((UserCompetition) competition).getPlayers());
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
     * Creates JSON object of type List<Team>
     * @param sport sport for teams
     * @return JSON object of type List<Team>
     */
    @GetMapping(path = "/create-competition/get_teams", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CompetitionTeamInfo>> getTeamsJSON(@RequestParam String sport,
                                                            @RequestParam(required = false, defaultValue = "") String search) {
        return ResponseEntity.ok().body(
                teamService.findTeamsBySportAndSearch(sport, search).stream().map(team -> new CompetitionTeamInfo(
                        team.getTeamId(),
                        team.getName()
                )).toList()
        );
    }

    /**
     * Creates JSON object of type List<User>. Users can have multiple sports so approach is different
     * @param sport sport for users
     * @return JSON object of type List<User>
     */
    @GetMapping(path = "/create-competition/get_users", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CompetitionUserInfo>> getUsersJSON(@RequestParam String sport,
                                                                  @RequestParam(required = false, defaultValue = "") String search) {
        return ResponseEntity.ok().body(
                userService.findUsersBySportAndName(sport, search).stream().map(user -> new CompetitionUserInfo(
                        user.getUserId(),
                        user.getFirstName(),
                        user.getLastName()
                )).toList()
        );
    }
}
