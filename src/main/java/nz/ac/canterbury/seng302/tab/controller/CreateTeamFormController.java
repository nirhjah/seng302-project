package nz.ac.canterbury.seng302.tab.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Optional;

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
import nz.ac.canterbury.seng302.tab.validator.LocationValidators;
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


    /**
     * Gets createTeamForm to be displayed and contains name, sport,
     * location and teamID model attributes to be added to html.
     *
     * @return thymeleaf createTeamForm
     */
    public void prefillModel(Model model) {
        model.addAttribute("postcodeRegex", LocationValidators.VALID_POSTCODE_REGEX);
        model.addAttribute("postcodeRegexMsg", LocationValidators.INVALID_POSTCODE_MSG);
        model.addAttribute("addressRegex", LocationValidators.VALID_ADDRESS_REGEX);
        model.addAttribute("addressRegexMsg", LocationValidators.INVALID_POSTCODE_MSG);
        model.addAttribute("countryCitySuburbNameRegex", LocationValidators.VALID_COUNTRY_SUBURB_CITY_REGEX);
        model.addAttribute("countryCitySuburbNameRegexMsg", LocationValidators.INVALID_COUNTRY_SUBURB_CITY_MSG);
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

    @GetMapping("/createTeam")
    public String teamForm(@RequestParam(name = "edit", required = false) Long teamID,
            @RequestParam(name = "invalid_input", defaultValue = "0") boolean invalidInput,
            Model model,
            HttpServletRequest request, CreateAndEditTeamForm createAndEditTeamForm) throws MalformedURLException {

        logger.info("GET /createTeam");
        prefillModel(model);
        
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

        if (invalidInput) {
            model.addAttribute("invalid_input", "Invalid input.");
        }

        // client side validation

        model.addAttribute("teamNameUnicodeRegex", TeamFormValidators.VALID_TEAM_NAME_REGEX);
        model.addAttribute("sportUnicodeRegex", TeamFormValidators.VALID_TEAM_SPORT_REGEX);

        List<String> knownSports = sportService.getAllSportNames();
        model.addAttribute("knownSports", knownSports);
        return "createTeamForm";
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


        prefillModel(model);
        // client side validation
        model.addAttribute("countryOrCityNameRegex", LocationValidators.VALID_COUNTRY_SUBURB_CITY_REGEX);
        model.addAttribute("teamNameUnicodeRegex", TeamFormValidators.VALID_TEAM_NAME_REGEX);
        model.addAttribute("sportUnicodeRegex", TeamFormValidators.VALID_TEAM_SPORT_REGEX);
        model.addAttribute("httpServletRequest", httpServletRequest);

        if (bindingResult.hasErrors()) {
            httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            logger.info("bad request");
            return "createTeamForm";
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
        if (!teamService.validateTeamRegistration(trimmedSport, trimmedName, trimmedCountry, trimmedCity,
                trimmedPostcode, trimmedSuburb, trimmedAddressLine1, trimmedAddressLine2)) {
            return "redirect:./createTeam?invalid_input=1" + (teamID != -1 ? "&edit=" + teamID : "");
        }

        Location location = new Location(trimmedAddressLine1, trimmedAddressLine2, trimmedSuburb, trimmedCity,
                trimmedPostcode, trimmedCountry);

        Team team = teamService.getTeam(teamID);
        if (team != null) {
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

        return String.format("redirect:./profile?teamID=%s", teamID);
    }
}
