package nz.ac.canterbury.seng302.tab.controller;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Sport;
import nz.ac.canterbury.seng302.tab.entity.Team;

import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.service.LocationService;
import nz.ac.canterbury.seng302.tab.service.SportService;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

    //
    // @Value("${ops.api.key}")
    // private String apiKey;

    /**
     * Gets createTeamForm to be displayed and contains name, sport,
     * location and teamID model attributes to be added to html.
     *
     * @return thymeleaf createTeamForm
     */

    @GetMapping("/createTeam")
    public String teamForm(@RequestParam(name = "edit", required = false) Long teamID,
                           @RequestParam(name = "invalid_input", defaultValue = "0") boolean invalidInput,
                           Model model,
                           HttpServletRequest request) throws MalformedURLException {

        logger.info("GET /createTeam");
        model.addAttribute("httpServletRequest", request);

        URL url = new URL(request.getRequestURL().toString());
        String path = (url.getPath() + "/..");
        String protocolAndAuthority = String.format("%s://%s", url.getProtocol(), url.getAuthority());
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
            } else {
                model.addAttribute("invalid_team", "Invalid team ID, creating a new team instead.");
            }
        }

        if (invalidInput) {
            model.addAttribute("invalid_input", "Invalid input.");
        }

        // client side validation

        model.addAttribute("addressRegex", teamService.addressRegex);
        model.addAttribute("countryCitySuburbNameRegex", teamService.countryCitySuburbNameRegex);
        model.addAttribute("postcodeRegex", teamService.postcodeRegex);
        model.addAttribute("teamNameUnicodeRegex", teamService.teamNameUnicodeRegex);
        model.addAttribute("sportUnicodeRegex", teamService.sportUnicodeRegex);

        List<String> knownSports = sportService.getAllSportNames();
        model.addAttribute("knownSports", knownSports);
        Optional<User> user = userService.getCurrentUser();
        model.addAttribute("firstName", user.get().getFirstName());
        model.addAttribute("lastName", user.get().getLastName());
        model.addAttribute("displayPicture", user.get().getPictureString());
        model.addAttribute("navTeams", teamService.getTeamList());
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
            Model model,
            HttpServletRequest httpServletRequest) throws IOException {
        logger.info("POST /createTeam");

        // client side validation
        model.addAttribute("countryOrCityNameRegex", teamService.countryCitySuburbNameRegex);
        model.addAttribute("postcodeRegex", teamService.postcodeRegex);
        model.addAttribute("teamNameUnicodeRegex", teamService.teamNameUnicodeRegex);
        model.addAttribute("sportUnicodeRegex", teamService.sportUnicodeRegex);

        // trim all extra whitespace and trailing/leading whitespace
        String trimmedName = teamService.clipExtraWhitespace(name);
        String trimmedSport = teamService.clipExtraWhitespace(sport);
        String trimmedCountry = teamService.clipExtraWhitespace(country);
        String trimmedAddressLine1 = teamService.clipExtraWhitespace(addressLine1);
        String trimmedAddressLine2 = teamService.clipExtraWhitespace(addressLine2);
        String trimmedCity = teamService.clipExtraWhitespace(city);
        String trimmedPostcode = teamService.clipExtraWhitespace(postcode);
        String trimmedSuburb = teamService.clipExtraWhitespace(suburb);

        // TeamService validation
        if (!teamService.validateTeamRegistration(trimmedSport, trimmedName, trimmedCountry, trimmedCity,
                trimmedPostcode, trimmedSuburb, trimmedAddressLine1, trimmedAddressLine2)) {
            return "redirect:./createTeam?invalid_input=1" + (teamID != -1 ? "&edit=" + teamID : "");
        }

        Location location = new Location(trimmedAddressLine1, trimmedAddressLine2, trimmedSuburb, trimmedCity,
                trimmedPostcode, trimmedCountry);
        Team team;
        if ((team = teamService.getTeam(teamID)) != null) {
            team.setName(trimmedName);
            team.setSport(trimmedSport);
            team.setLocation(location);
            team = teamService.updateTeam(team);
            teamID = team.getTeamId();
        } else {
            team = new Team(trimmedName, trimmedSport, location);
            team = teamService.addTeam(team);
            teamID = team.getTeamId();
        }

        List<String> knownSports = sportService.getAllSportNames();
        if (!knownSports.contains(trimmedSport)) {
            sportService.addSport(new Sport(trimmedSport));
        }

        return String.format("redirect:./profile?teamID=%s", teamID);
    }
}
