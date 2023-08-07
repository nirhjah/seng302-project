package nz.ac.canterbury.seng302.tab.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nz.ac.canterbury.seng302.tab.entity.*;

import nz.ac.canterbury.seng302.tab.entity.competition.Competition;
import nz.ac.canterbury.seng302.tab.entity.competition.TeamCompetition;
import nz.ac.canterbury.seng302.tab.entity.competition.UserCompetition;
import nz.ac.canterbury.seng302.tab.form.CreateAndEditTeamForm;
import nz.ac.canterbury.seng302.tab.service.CompetitionService;
import nz.ac.canterbury.seng302.tab.service.SportService;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Optional;

/**
 * Spring Boot Controller class for the Create Team Form
 */
@Controller
public class CreateTeamFormController {

    Logger logger = LoggerFactory.getLogger(CreateTeamFormController.class);

    @Autowired
    private TeamService teamService;

    @Autowired
    private SportService sportService;

    @Autowired
    private UserService userService;

    @Autowired
    private CompetitionService competitionService;

    private static final String CREATE_TEAM_TEMPLATE = "createTeamForm";

    private static final String REDIRECT_HOME = "redirect:./profile?teamID=%s";

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
                logger.info("POST /generateTeamToken, new token: ".concat(team.getToken()));
            }
        }
        return String.format("redirect:./profile?teamID=%s", teamID);
    }

    /**
     * Gets the create team form for the user or editting a user
     * @param teamID Id of team
     * @param model (map-like) representation of name, language and isJava boolean
     *      *              for use in thymeleaf,
     *      *              with values being set to relevant parameters provided
     * @param request the HTTP request
     * @param createAndEditTeamForm the form that's being displayed
     * @return create team form page
     * @throws MalformedURLException
     */
    @GetMapping("/createTeam")
    public String teamForm(@RequestParam(name = "edit", required = false) Long teamID,
            Model model,
            HttpServletRequest request, CreateAndEditTeamForm createAndEditTeamForm) throws MalformedURLException {


        logger.info("GET /createTeam");
        model.addAttribute("httpServletRequest", request);

        URL url = new URL(request.getRequestURL().toString());
        String path = (url.getPath() + "/..");
        model.addAttribute("path", path);

        Team team;
        if (teamID != null) {
            if ((team = teamService.getTeam(teamID)) != null) {
                model.addAttribute("name", team.getName());
                model.addAttribute("sport", team.getSport());
                model.addAttribute("addressLine1", team.getLocation().getAddressLine1());
                model.addAttribute("addressLine2", team.getLocation().getAddressLine2());
                model.addAttribute("city", team.getLocation().getCity());
                model.addAttribute("suburb", team.getLocation().getSuburb());
                model.addAttribute("country", team.getLocation().getCountry());
                model.addAttribute("postcode", team.getLocation().getPostcode());
                model.addAttribute("teamID", team.getTeamId());
                model.addAttribute("path", path);
            } else {
                model.addAttribute("invalid_team", "Invalid team ID, creating a new team instead.");
            }
        }

        List<String> knownSports = sportService.getAllSportNames();
        model.addAttribute("knownSports", knownSports);
        Optional<User> user = userService.getCurrentUser();
        if (user.isPresent()) {
            model.addAttribute("firstName", user.get().getFirstName());
            model.addAttribute("lastName", user.get().getLastName());
            model.addAttribute("displayPicture", user.get().getPictureString());
            model.addAttribute("navTeams", teamService.getTeamList());
            return CREATE_TEAM_TEMPLATE;
        } else {
            return REDIRECT_HOME;
        }

    }

    /**
     * Posts a form response with team name, sport and location
     *
     * @param name  name if user
     * @param sport users team sport
     * @param model (map-like) representation of name, language and isJava boolean
     *              for use in thymeleaf,
     *              with values being set to relevant parameters provided
     * @return thymeleaf teamFormTemplate
     */
    @PostMapping("/createTeam")
    public String submitTeamForm(
            @RequestParam(name = "teamID", defaultValue = "-1") long teamID,
            @RequestParam(name = "name") String name,
            @RequestParam(name = "sport") String sport,
            @RequestParam(name = "addressLine1") String addressLine1,
            @RequestParam(name = "addressLine2") String addressLine2,
            @RequestParam(name = "city") String city,
            @RequestParam(name = "country") String country,
            @RequestParam(name = "postcode") String postcode,
            @RequestParam(name = "suburb") String suburb,
            @Validated CreateAndEditTeamForm createAndEditTeamForm,
            BindingResult bindingResult,
            HttpServletResponse httpServletResponse,
            Model model,
            HttpServletRequest httpServletRequest) throws IOException {
        logger.info("POST /createTeam");


        // client side validation
        model.addAttribute("countryOrCityNameRegex", teamService.countryCitySuburbNameRegex);
        model.addAttribute("postcodeRegex", teamService.postcodeRegex);
        model.addAttribute("teamNameUnicodeRegex", teamService.teamNameUnicodeRegex);
        model.addAttribute("sportUnicodeRegex", teamService.sportUnicodeRegex);
        model.addAttribute("httpServletRequest", httpServletRequest);
        Optional<User> user = userService.getCurrentUser();
        if (user.isEmpty()) {
            return REDIRECT_HOME;
        }
        model.addAttribute("firstName", user.get().getFirstName());
        model.addAttribute("lastName", user.get().getLastName());
        model.addAttribute("displayPicture", user.get().getPictureString());
        model.addAttribute("navTeams", teamService.getTeamList());


        if (bindingResult.hasErrors()) {
            httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            if (teamID != -1) {
                model.addAttribute("teamID", teamID);
            }
            logger.info("bad request");
            return CREATE_TEAM_TEMPLATE;
        }


        // trim all extra whitespace and trailing/leading whitespace
        String trimmedName = teamService.clipExtraWhitespace(name);
        String trimmedSport = teamService.clipExtraWhitespace(sport);
        String trimmedCountry = teamService.clipExtraWhitespace(createAndEditTeamForm.getCountry());
        String trimmedAddressLine1 = teamService.clipExtraWhitespace(createAndEditTeamForm.getAddressLine1());
        String trimmedAddressLine2 = teamService.clipExtraWhitespace(createAndEditTeamForm.getAddressLine2());
        String trimmedCity = teamService.clipExtraWhitespace(createAndEditTeamForm.getCity());
        String trimmedPostcode = teamService.clipExtraWhitespace(createAndEditTeamForm.getPostcode());
        String trimmedSuburb = teamService.clipExtraWhitespace(createAndEditTeamForm.getSuburb());

        // TeamService validation
        if (bindingResult.hasErrors()) {
            URL url = new URL(httpServletRequest.getRequestURL().toString());
            String path = (url.getPath() + "/..");
            model.addAttribute("path", path);
            return CREATE_TEAM_TEMPLATE;
        }

        Location location = new Location(trimmedAddressLine1, trimmedAddressLine2, trimmedSuburb, trimmedCity,
                trimmedPostcode, trimmedCountry);

        Team team = teamService.getTeam(teamID);
        boolean teamExists = team != null;
        if (teamExists) {
            // edit existing team
            team.setName(trimmedName);
            team.setSport(trimmedSport);
            team.setLocation(location);
            team = teamService.updateTeam(team);
        } else {
            // create a new team
            logger.info("creating new user ");
            Optional<User> currentUser = userService.getCurrentUser();
            if (currentUser.isPresent()) {
                logger.info("creating new user with manager");
                team = new Team(trimmedName, trimmedSport, location, currentUser.get());
            } else {
                logger.info("creating new user without manager");
                team = new Team(trimmedName, trimmedSport, location);
            }
            team.generateToken(teamService);
            team = teamService.addTeam(team);
        }
        teamID = team.getTeamId();

        List<String> knownSports = sportService.getAllSportNames();
        if (!knownSports.contains(trimmedSport)) {
            sportService.addSport(new Sport(trimmedSport));
        }


        //test code remove before merge
     /*   Competition competition1 = new TeamCompetition("football competition", new Grade(Grade.Age.UNDER_19S, Grade.Sex.MIXED, Grade.Competitiveness.SOCIAL), "football");
        Competition competition2 = new TeamCompetition("hockey competition", new Grade(Grade.Age.ADULT, Grade.Sex.WOMENS, Grade.Competitiveness.SOCIAL), "hockey");
        Competition competition3 = new TeamCompetition("rugby competition", new Grade(Grade.Age.ADULT, Grade.Sex.MENS, Grade.Competitiveness.SOCIAL), "rugby");
        Competition competition4 = new TeamCompetition("cricket competition", new Grade(Grade.Age.UNDER_6S, Grade.Sex.MIXED, Grade.Competitiveness.SOCIAL), "cricket");
        Competition competition5 = new TeamCompetition("blah competition", new Grade(Grade.Age.UNDER_6S, Grade.Sex.MENS, Grade.Competitiveness.SOCIAL), "soccer");
        Competition competition6 = new TeamCompetition("bleh competition", new Grade(Grade.Age.UNDER_10S, Grade.Sex.MIXED, Grade.Competitiveness.SOCIAL), "swimming");
        Competition competition7 = new TeamCompetition("sdfs competition", new Grade(Grade.Age.UNDER_10S, Grade.Sex.MIXED, Grade.Competitiveness.SOCIAL), "sdfdf");
        competitionService.updateOrAddCompetition(competition1);
        competitionService.updateOrAddCompetition(competition2);
        competitionService.updateOrAddCompetition(competition3);
        competitionService.updateOrAddCompetition(competition4);
        competitionService.updateOrAddCompetition(competition5);
        competitionService.updateOrAddCompetition(competition6);
        competitionService.updateOrAddCompetition(competition7);*/

        return String.format("redirect:./profile?teamID=%s", teamID);
    }
}
