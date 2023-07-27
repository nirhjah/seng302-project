package nz.ac.canterbury.seng302.tab.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Sport;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.form.CreateAndEditTeamForm;
import nz.ac.canterbury.seng302.tab.service.SportService;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import nz.ac.canterbury.seng302.tab.validator.TeamFormValidators;

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

    private static final String TEMPLATE_NAME = "createTeamForm";


    /**
     * Gets createTeamForm to be displayed and contains name, sport,
     * location and teamID model attributes to be added to html.
     */
    private void prefillModel(Model model, HttpServletRequest httpServletRequest) {
        model.addAttribute("countryCitySuburbNameRegex", TeamFormValidators.VALID_COUNTRY_SUBURB_CITY_REGEX);
        model.addAttribute("countryCitySuburbNameRegexMsg", TeamFormValidators.INVALID_COUNTRY_SUBURB_CITY_MSG);
        model.addAttribute("teamNameUnicodeRegex", teamService.teamNameUnicodeRegex);
        model.addAttribute("teamNameMsg", TeamFormValidators.INVALID_TEAM_NAME_MSG);
        model.addAttribute("sportUnicodeRegex", teamService.sportUnicodeRegex);
        model.addAttribute("sportUnicodeMsg", TeamFormValidators.INVALID_TEAM_SPORT_MSG);
        model.addAttribute("httpServletRequest", httpServletRequest);
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
        return String.format("redirect:./profile?teamID=%s", teamID);
    }

    /**
     * Endpoint for creating a new team. This form will not be pre-populated
     */
    @GetMapping("/createTeam")
    public String createTeamForm(
            Model model,
            HttpServletRequest request,
            CreateAndEditTeamForm createAndEditTeamForm) throws MalformedURLException {

        logger.info("GET /createTeam - new team");

        prefillModel(model, request);

        URL url = new URL(request.getRequestURL().toString());
        String path = (url.getPath() + "/..");
        model.addAttribute("path", path);


        List<String> knownSports = sportService.getAllSportNames();
        model.addAttribute("knownSports", knownSports);
 
        return TEMPLATE_NAME;
    }


    /**
     * Endpoint for *updating* a team.
     */
    @GetMapping(value = "/createTeam", params = {"edit"})
    public String editTeamForm(
            @RequestParam(name = "edit", required = true) long teamID,
            Model model,
            HttpServletRequest request,
            CreateAndEditTeamForm createAndEditTeamForm) throws MalformedURLException {

        logger.info("GET /createTeam - updated team with ID={}", teamID);
        prefillModel(model, request);
        
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

        List<String> knownSports = sportService.getAllSportNames();
        model.addAttribute("knownSports", knownSports);
        model.addAttribute("httpServletRequest", request);

        URL url = new URL(request.getRequestURL().toString());
        String path = (url.getPath() + "/..");
        model.addAttribute("path", path);
 
        return TEMPLATE_NAME;
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
        logger.info("POST /createTeam - update team");


        // I'm starting to regret this pattern
        Optional<User> user = userService.getCurrentUser();
        if (user.isEmpty()) {
            return "redirect:login";
        }

        boolean editingTeam = (teamID != -1);

        // Are there form errors?
        if (bindingResult.hasErrors()) {
            logger.error("{}", bindingResult);
            prefillModel(model, httpServletRequest);
            httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            model.addAttribute("teamID", teamID);
            logger.info("bad request");
            return TEMPLATE_NAME;
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

        return String.format("redirect:./profile?teamID=%s", team.getTeamId());
    }
}
