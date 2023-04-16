package nz.ac.canterbury.seng302.tab.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Sport;
import nz.ac.canterbury.seng302.tab.entity.Team;

import nz.ac.canterbury.seng302.tab.form.CreateAndEditTeamForm;
import nz.ac.canterbury.seng302.tab.service.SportService;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.validator.TeamFormValidators;
import nz.ac.canterbury.seng302.tab.validator.UserFormValidators;
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

    /**
     * Gets createTeamForm to be displayed and contains name, sport,
     * location and teamID model attributes to be added to html.
     *
     * @return thymeleaf createTeamForm
     */

    public void prefillModel(Model model) {
        model.addAttribute("postcodeRegex", TeamFormValidators.VALID_POSTCODE_REGEX);
        model.addAttribute("postcodeRegexMsg",TeamFormValidators.INVALID_POSTCODE_MSG);
        model.addAttribute("addressRegex",TeamFormValidators.VALID_ADDRESS_REGEX);
        model.addAttribute("addressRegexMsg",TeamFormValidators.INVALID_POSTCODE_MSG);
        model.addAttribute("countryCitySuburbNameRegex",TeamFormValidators.VALID_COUNTRY_SUBURB_CITY_REGEX);
        model.addAttribute("countryCitySuburbNameRegexMsg",TeamFormValidators.INVALID_COUNTRY_SUBURB_CITY_MSG);
    }

    @GetMapping("/createTeam")
    public String teamForm(@RequestParam(name = "edit", required = false) Long teamID,
                           @RequestParam(name = "invalid_input", defaultValue = "0") boolean invalidInput,
                           Model model, HttpServletRequest httpServletRequest, CreateAndEditTeamForm createAndEditTeamForm) throws MalformedURLException {

        logger.info("GET /createTeam");
        prefillModel(model);
        URL url = new URL(httpServletRequest.getRequestURL().toString());
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
                model.addAttribute("path", path);
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
            @Validated CreateAndEditTeamForm createAndEditTeamForm,
            BindingResult bindingResult,
            HttpServletResponse httpServletResponse,
            Model model,
            HttpServletRequest httpServletRequest) throws IOException {
        logger.info("POST /createTeam");


        prefillModel(model);
        // client side validation
        model.addAttribute("countryOrCityNameRegex", teamService.countryCitySuburbNameRegex);
        model.addAttribute("postcodeRegex", teamService.postcodeRegex);
        model.addAttribute("teamNameUnicodeRegex", teamService.teamNameUnicodeRegex);
        model.addAttribute("sportUnicodeRegex", teamService.sportUnicodeRegex);

        if (bindingResult.hasErrors()) {
            httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "createTeamForm";
            //return "redirect:./createTeam?invalid_input=1" + (teamID != -1 ? "&edit=" + teamID : "");
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
