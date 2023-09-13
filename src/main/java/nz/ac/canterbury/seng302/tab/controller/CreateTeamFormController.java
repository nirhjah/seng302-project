package nz.ac.canterbury.seng302.tab.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nz.ac.canterbury.seng302.tab.entity.*;
import nz.ac.canterbury.seng302.tab.entity.lineUp.LineUp;
import nz.ac.canterbury.seng302.tab.entity.lineUp.LineUpPosition;
import nz.ac.canterbury.seng302.tab.enums.ActivityType;
import nz.ac.canterbury.seng302.tab.form.CreateAndEditTeamForm;
import nz.ac.canterbury.seng302.tab.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Boot Controller class for the Create Team Form
 */
@Controller
public class CreateTeamFormController {
    
    Logger logger = LoggerFactory.getLogger(CreateTeamFormController.class);

    private static final String CREATE_TEAM_TEMPLATE = "createTeamForm";
    private static final String REDIRECT_HOME = "redirect:/home";
    private static final String IS_EDITING_KEY = "isEditing";

    private static final String HTTP_SERVLET_REQUEST_STRING = "httpServletRequest";

    private TeamService teamService;
    private SportService sportService;
    private UserService userService;
    private FormationService formationService;
    private ActivityService activityService;
    private LineUpPositionService lineUpPositionService;
    private LineUpService lineUpService;

    public CreateTeamFormController(TeamService teamService, SportService sportService, UserService userService, FormationService formationService, ActivityService activityService, LineUpPositionService lineUpPositionService, LineUpService lineUpService) {
        this.teamService = teamService;
        this.sportService = sportService;
        this.userService = userService;
        this.formationService = formationService;
        this.activityService = activityService;
        this.lineUpPositionService = lineUpPositionService;
        this.lineUpService = lineUpService;
    }


    /**
     * Creates a location entity from details provided in the form
     * @param createAndEditTeamForm The response from our users
     * @return A location object, with whitespace before/after each field trimmed.
     */
    private Location createLocationFromTrimmedForm(CreateAndEditTeamForm createAndEditTeamForm) {
        String trimmedCountry = teamService.clipExtraWhitespace(createAndEditTeamForm.getCountry());
        String trimmedAddressLine1 = teamService.clipExtraWhitespace(createAndEditTeamForm.getAddressLine1());
        String trimmedAddressLine2 = teamService.clipExtraWhitespace(createAndEditTeamForm.getAddressLine2());
        String trimmedCity = teamService.clipExtraWhitespace(createAndEditTeamForm.getCity());
        String trimmedPostcode = teamService.clipExtraWhitespace(createAndEditTeamForm.getPostcode());
        String trimmedSuburb = teamService.clipExtraWhitespace(createAndEditTeamForm.getSuburb());

        return new Location(trimmedAddressLine1, trimmedAddressLine2, trimmedSuburb, trimmedCity,
                trimmedPostcode, trimmedCountry);
    }

    /**
     * Triggers the generation of a new token for a team
     * @param teamID the id of the team.
     * @return redirect back to team profile page
     */
    @PostMapping("/generateTeamToken")
    public String generateTeamToken(@RequestParam(name = "teamID") Long teamID) {
        var team = teamService.getTeam(teamID);
        if (team != null) {
            var user = userService.getCurrentUser();
            if (user.isPresent() && team.isManager(user.get())) {
                team.generateToken(teamService);
                teamService.updateTeam(team);
                logger.info("POST /generateTeamToken, new token: {}", team.getToken());
            }
        }
        return String.format("redirect:./team-info?teamID=%s", teamID);
    }

    /**
     * Endpoint for creating a new team. Gives a blank form.
     */
    @GetMapping("/createTeam")
    public String createTeamForm(
            Model model,
            HttpServletRequest request,
            CreateAndEditTeamForm createAndEditTeamForm) throws MalformedURLException {

        logger.info("GET /createTeam - new team");

        model.addAttribute(HTTP_SERVLET_REQUEST_STRING, request);

        model.addAttribute(IS_EDITING_KEY, false);

        URL url = new URL(request.getRequestURL().toString());
        String path = (url.getPath() + "/..");
        model.addAttribute("path", path);


        List<String> knownSports = sportService.getAllSportNames();

        model.addAttribute("knownSports", knownSports);
 
        return CREATE_TEAM_TEMPLATE;
    }


    /**
     * Endpoint for *updating* a team. Gives a pre-populated form.
     */
    @GetMapping(value = "/createTeam", params = {"edit"})
    public String editTeamForm(
            @RequestParam(name = "edit", required = true) long teamID,
            Model model,
            HttpServletRequest request,
            CreateAndEditTeamForm createAndEditTeamForm) throws MalformedURLException {

        logger.info("GET /createTeam - updated team with ID={}", teamID);

        model.addAttribute(HTTP_SERVLET_REQUEST_STRING, request);

        model.addAttribute(IS_EDITING_KEY, true);
        
        // I'm starting to regret this pattern
        Optional<User> user = userService.getCurrentUser();
        if (user.isEmpty()) {
            return "redirect:login";
        }
        
        // Does the team exist?
        Team team = teamService.getTeam(teamID);
        if (team == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Team with given ID does not exist");
        }
        // Are you allow to edit this team?
        if (!team.isManager(user.get())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is unable to modify this team");
        }

        createAndEditTeamForm.prepopulate(team);
        model.addAttribute("teamID", teamID);

        List<String> knownSports = sportService.getAllSportNames();
        model.addAttribute("knownSports", knownSports);
        model.addAttribute(HTTP_SERVLET_REQUEST_STRING, request);

        URL url = new URL(request.getRequestURL().toString());
        String path = (url.getPath() + "/..");
        model.addAttribute("path", path);
 
        return CREATE_TEAM_TEMPLATE;
    }


    private void addDebugEntities(Team team) {
        // TODO remove this
        // Generate users:
        var users = new ArrayList<User>();
        for (int i=0; i<30; i++) {
            try {
                var str = UUID.randomUUID().toString();
                var u = User.defaultDummyUser();
                u.setEmail(str + "@gmail.com");
                u.setFirstName(str.substring(0,6));
                u.setLastName("b");
                u = userService.updateOrAddUser(u);
                userService.userJoinTeam(u, team);
                users.add(u);
            } catch (Exception e) {
                logger.error("exception caught: " + e.getMessage());
            }
        }

        // Generate activity:
        var t = "test comp";
        var now = LocalDateTime.now();
        var next = now.plusMinutes(10);
        var loc = team.getLocation();
        var user = userService.getCurrentUser().get();
        Activity activity = new Activity(ActivityType.Game, team, t, now, next, user, loc);
        activity = activityService.updateOrAddActivity(activity);

        // Generate formation:
        Formation f = new Formation("1-4-4-2", team);
        f = formationService.addOrUpdateFormation(f);
        activity.setFormation(f);

        // Create lineup:
        LineUp lineUp = new LineUp(f, team, activity);
        lineUp = lineUpService.updateOrAddLineUp(lineUp);
        for (int i=0; i<users.size(); i++) {
            LineUpPosition lup = new LineUpPosition(lineUp, users.get(i), i+1);
            lineUpPositionService.addLineUpPosition(lup);
        }

    }


    /**
     * Posts a form response with team name, sport and location
     *
     * @param model (map-like) representation of name, language and isJava boolean
     *              for use in thymeleaf,
     *              with values being set to relevant parameters provided
     * @return thymeleaf teamFormTemplate
     */
    @PostMapping("/createTeam")
    public String submitTeamForm(
            @RequestParam(name = "teamID", defaultValue = "-1") long teamID,
            @Validated CreateAndEditTeamForm createAndEditTeamForm,
            BindingResult bindingResult,
            HttpServletResponse httpServletResponse,
            Model model,
            HttpServletRequest httpServletRequest) throws IOException {
        
        boolean editingTeam = (teamID != -1);
        logger.info("POST /createTeam - {} team", (editingTeam ? "updating" : "creating"));


        // I'm starting to regret this pattern
        Optional<User> user = userService.getCurrentUser();
        if (user.isEmpty()) {
            return REDIRECT_HOME;
        }


        // Are there form errors?
        if (bindingResult.hasErrors()) {
            logger.error("{}", bindingResult);

            model.addAttribute(HTTP_SERVLET_REQUEST_STRING, httpServletRequest);

            httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            model.addAttribute("teamID", teamID);
            model.addAttribute(IS_EDITING_KEY, editingTeam);
            logger.info("bad request");
            return CREATE_TEAM_TEMPLATE;
        }

        Team team;
        if (editingTeam) {  // Updating an existing team
            // Does the team exist?
            team = teamService.getTeam(teamID);
            if (team == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Team with given ID does not exist");
            }
            // Are you allowed to edit this team?
            if (!team.isManager(user.get())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is unable to modify this team");
            }
        } else { // create a new team
            team = new Team(null, null, null, user.get());
        }
        // Either way, we need to add these details
        // trim all extra whitespace and trailing/leading whitespace
        String trimmedName = teamService.clipExtraWhitespace(createAndEditTeamForm.getName());
        String trimmedSport = teamService.clipExtraWhitespace(createAndEditTeamForm.getSport());
        Location location = createLocationFromTrimmedForm(createAndEditTeamForm);

        team.setName(trimmedName);
        team.setSport(trimmedSport);
        team.setLocation(location);
        team.generateToken(teamService);
        team = teamService.addTeam(team);

        List<String> knownSports = sportService.getAllSportNames();
        if (!knownSports.contains(trimmedSport)) {
            sportService.addSport(new Sport(trimmedSport));
        }

        //addDebugEntities(team);

        return String.format("redirect:./team-info?teamID=%s", team.getTeamId());
    }
}
