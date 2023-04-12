package nz.ac.canterbury.seng302.tab.controller;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Sport;
import nz.ac.canterbury.seng302.tab.entity.Team;

import nz.ac.canterbury.seng302.tab.service.LocationService;
import nz.ac.canterbury.seng302.tab.service.SportService;
import nz.ac.canterbury.seng302.tab.service.TeamService;
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

    //
    // @Value("${ops.api.key}")
    // private String apiKey;

    /**
     * Countries and cities can have letters from all alphabets, with hyphens, apostrophes and
     * spaces. Must start with an alphabetical character
     */
    private final String countryCitySuburbNameRegex = "^\\p{L}+[\\- '\\p{L}]*$";

    /** Addresses can have letters, numbers, spaces, commas, periods, hyphens, forward slashes, apostrophes and pound signs. Must include
     * at least one alphanumeric character**/
    private  final String addressRegex = "^(?=.*[\\p{L}\\p{N}])(?:[\\- ,./#'\\p{L}\\p{N}])*$";

    /** Allow letters, numbers, forward slashes and hyphens. Must start with an alphanumeric character. */
    private final String postcodeRegex = "^[\\p{L}\\p{N}]+[\\-/\\p{L}\\p{N}]*$";

    /** A team name can be alphanumeric, dots and curly braces. Must start with on alphabetical character **/
    private final String teamNameUnicodeRegex = "^[\\p{L}\\p{N}]+[}{.\\p{L}\\p{N}]+$";

    /** A sport can be letters, space, apostrophes or hyphens. Must start with on alphabetical character**/
    private final String sportUnicodeRegex = "^\\p{L}+[\\- '\\p{L}]*$";

    /**
     * Gets createTeamForm to be displayed and contains name, sport,
     * location and teamID model attributes to be added to html.
     *
     * @return thymeleaf createTeamForm
     */

    @GetMapping("/createTeam")
    public String teamForm(@RequestParam(name = "edit", required = false) Long teamID,
                           @RequestParam(name = "invalid_input", defaultValue = "0") boolean invalidInput,
                           Model model, HttpServletRequest httpServletRequest) throws MalformedURLException {

        logger.info("GET /createTeam");

        URL url = new URL(httpServletRequest.getRequestURL().toString());
        String path = (url.getPath() + "/..");
        String protocolAndAuthority = String.format("%s://%s", url.getProtocol(), url.getAuthority());

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

        model.addAttribute("addressRegex", addressRegex);
        model.addAttribute("countryCitySuburbNameRegex", countryCitySuburbNameRegex);
        model.addAttribute("postcodeRegex", postcodeRegex);
        model.addAttribute("teamNameUnicodeRegex", teamNameUnicodeRegex);
        model.addAttribute("sportUnicodeRegex", sportUnicodeRegex);

        List<String> knownSports = sportService.getAllSportNames();
        model.addAttribute("knownSports", knownSports);
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
            Model model) throws IOException {
        logger.info("POST /createTeam");

        // client side validation
        model.addAttribute("countryOrCityNameRegex", countryCitySuburbNameRegex);
        model.addAttribute("postcodeRegex", postcodeRegex);
        model.addAttribute("teamNameUnicodeRegex", teamNameUnicodeRegex);
        model.addAttribute("sportUnicodeRegex", sportUnicodeRegex);

        // server side validation
        boolean nameValid = (name.matches(teamNameUnicodeRegex));
        boolean sportValid = (sport.matches(sportUnicodeRegex));
        boolean countryValid = (country.matches(countryCitySuburbNameRegex));
        boolean addressLine1Valid = (addressLine1.matches(addressRegex)) || addressLine1 == "";
        boolean addressLine2Valid = (addressLine2.matches(addressRegex)) || addressLine2 == "";
        boolean cityValid = (city.matches(countryCitySuburbNameRegex));
        boolean postcodeValid = (postcode.matches(postcodeRegex)) || postcode == "";
        boolean suburbValid = (suburb.matches(countryCitySuburbNameRegex)) || suburb == "";
        if (!sportValid || !nameValid || !countryValid || !cityValid || !postcodeValid || !suburbValid || !addressLine1Valid || !addressLine2Valid) {
            return "redirect:./createTeam?invalid_input=1" + (teamID != -1 ? "&edit=" + teamID : "");
        }
        Location location = new Location(addressLine1, addressLine2, suburb, city, postcode, country);
        Team team;
        if ((team = teamService.getTeam(teamID)) != null) {
            team.setName(name);
            team.setSport(sport);
            team.setLocation(location);
            team = teamService.updateTeam(team);
            teamID = team.getTeamId();
        } else {
            team = new Team(name, sport, location);
            team = teamService.addTeam(team);
            teamID = team.getTeamId();
        }

        List<String> knownSports = sportService.getAllSportNames();
        if (!knownSports.contains(sport)) {
            sportService.addSport(new Sport(sport));
        }

        return String.format("redirect:./profile?teamID=%s", teamID);
    }
}
