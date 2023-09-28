package nz.ac.canterbury.seng302.tab.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nz.ac.canterbury.seng302.tab.api.response.ClubTeamInfo;
import nz.ac.canterbury.seng302.tab.entity.*;
import nz.ac.canterbury.seng302.tab.form.CreateAndEditClubForm;
import nz.ac.canterbury.seng302.tab.helper.exceptions.UnmatchedSportException;
import nz.ac.canterbury.seng302.tab.service.image.ClubImageService;
import nz.ac.canterbury.seng302.tab.service.ClubService;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import static nz.ac.canterbury.seng302.tab.validator.LocationValidators.INVALID_CHARACTERS_MSG;


/**
 * Spring Boot Controller class for the Create Club Form
 */
@Controller
public class CreateClubController {

    Logger logger = LoggerFactory.getLogger(CreateClubController.class);

    private final ClubService clubService;

    private final UserService userService;

    private final TeamService teamService;

    private final ClubImageService clubImageService;

    private static final String CREATE_CLUB_FORM_STRING = "CreateAndEditClubForm";
    private static final String SELECTED_TEAM_STRING = "selectedTeams";

    private static final String HTTP_SERVLET_REQUEST_STRING = "httpServletRequest";

    private static final String ADDRESS_LINE_1_STRING = "addressLine1";

    @Autowired
    public CreateClubController(ClubService clubService,UserService userService, TeamService teamService, ClubImageService clubImageService) {
        this.clubService = clubService;
        this.userService = userService;
        this.teamService = teamService;
        this.clubImageService = clubImageService;
    }

    /**
     * Gets club form
     * @param clubId  if editing, get club through clubId
     * @param model   model
     * @param request http request
     * @return create club form
     */
    @GetMapping("/createClub")
    public String clubForm(@RequestParam(name = "edit", required = false) Long clubId,
                           Model model,CreateAndEditClubForm createAndEditClubForm,
                           HttpServletRequest request) throws MalformedURLException {

        logger.info("GET /createClub");
        prefillModel(model, request);
        URL url = new URL(request.getRequestURL().toString());
        String path = (url.getPath() + "/..");
        model.addAttribute("path", path);

        if (clubId != null) {
            Optional<Club> optClub = clubService.findClubById(clubId);
            if (optClub.isPresent()) {
                prefillModelWithClub(model, optClub.get());

                List<String> selectedTeamIds = teamService.findTeamsByClub(optClub.get())
                        .stream()
                        .map(team -> String.valueOf(team.getTeamId())).toList();
                model.addAttribute("selectedTeamIds", selectedTeamIds);
                createAndEditClubForm.setSelectedTeams(selectedTeamIds);

                createAndEditClubForm.prepopulate(optClub.get());

            }
        }
        return "createClubForm";
    }

    /**
     * Performs error checking on the addressline1 and postcode fields of a creating or editing club.
     * If the required fields are empty, errors are added to the BindingResult.
     *
     * @param bindingResult       The BindingResult object that holds the validation errors.
     * @param createAndEditClubForm The CreateAndEditClubForm object containing the form data to be checked.
     */
    private void postCreateClubErrorChecking(BindingResult bindingResult, CreateAndEditClubForm createAndEditClubForm){
        String addressLine1= createAndEditClubForm.getAddressLine1().trim();
        if (addressLine1.isEmpty()) {
            bindingResult.addError(new FieldError(CREATE_CLUB_FORM_STRING, ADDRESS_LINE_1_STRING, "Field cannot be empty"));
        } else {
            if (!addressLine1.matches("^[a-zA-Z0-9 ',-]*$")) {
                bindingResult.addError(new FieldError(CREATE_CLUB_FORM_STRING, ADDRESS_LINE_1_STRING, INVALID_CHARACTERS_MSG));

            }
        }

        String postcode = createAndEditClubForm.getPostcode().trim();
        if (postcode.isEmpty()) {
            bindingResult.addError(new FieldError(CREATE_CLUB_FORM_STRING, "postcode", "Field cannot be empty"));
        }
    }


    /**
     * Handles the creation or editing of a club based on the provided form data.
     * This method is invoked when a POST request is made to "/createClub" endpoint.
     *
     * @param clubId                The ID of the club being edited.
     * @param name                  The name of the club.
     * @param clubLogo              The logo image file of the club as a multipart file.
     * @param sport                 The sport associated with the club.
     * @param selectedTeams         (Optional) A list of selected teams associated with the club.
     * @param createAndEditClubForm The form object containing additional club-related data for validation.
     * @param bindingResult         The result of data binding and validation for form object.
     * @param httpServletResponse   The response object used for setting HTTP status codes.
     * @param model                 The model object to hold attributes for the view.
     * @param httpServletRequest    The request object to get additional information about the HTTP request.
     * @return The view name to be displayed after the club creation/edit process.
     * @throws IOException If an I/O error occurs during file handling.
     */
    @PostMapping("/createClub")
    public String createClub(
            @RequestParam(name = "clubId", defaultValue = "-1") long clubId,
            @RequestParam(name="name") String name,
            @RequestParam(name="file") MultipartFile clubLogo,
            @RequestParam(name = "sport") String sport,
            @RequestParam(name = "selectedTeams", required = false) List<String> selectedTeams,
            @Validated CreateAndEditClubForm createAndEditClubForm,
            BindingResult bindingResult,
            HttpServletResponse httpServletResponse,
            Model model,
            HttpServletRequest httpServletRequest) throws IOException {
        prefillModel(model, httpServletRequest);

        Optional<User> optUser = userService.getCurrentUser();
        if (optUser.isEmpty()) {
            return "redirect:/home";
        }
        postCreateClubErrorChecking(bindingResult, createAndEditClubForm);


        Location location = new Location(createAndEditClubForm.getAddressLine1(), createAndEditClubForm.getAddressLine2(), createAndEditClubForm.getSuburb(), createAndEditClubForm.getCity(), createAndEditClubForm.getPostcode(), createAndEditClubForm.getCountry());

        Optional<Club> optClub = clubService.findClubById(clubId);
        if (optClub.isPresent()) {
            Club editClub = optClub.get();
            editClub.setSport(sport);
            setTeamsClub(selectedTeams, editClub, bindingResult);

            if (bindingResult.hasErrors()) {
                httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                prefillModelWithClub(model, editClub);
                return "createClubForm";
            }

            editClub.setName(name);
            editClub.setLocation(location);

            // If there is no clubLogo passed in, nothing will be changed, so it's fine.
            clubImageService.updateClubLogo(editClub, clubLogo);

            clubService.updateOrAddClub(editClub);
            return "redirect:/view-club?clubID=" + editClub.getClubId();

        } else {
            User manager = optUser.get(); // manager is the current user
            Club club = new Club(name, location, sport, manager);

            setTeamsClub(selectedTeams, club, bindingResult);

            if (bindingResult.hasErrors()) {
                httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                model.addAttribute(SELECTED_TEAM_STRING, selectedTeams);
                model.addAttribute(SELECTED_TEAM_STRING, createAndEditClubForm.getSelectedTeams());

                return "createClubForm";
            }

            clubService.updateOrAddClub(club);

            if (!Objects.equals(clubLogo.getOriginalFilename(), "")) {
                // If there's a logo, set it.
                clubImageService.updateClubLogo(club, clubLogo);
            }

            return "redirect:/view-club?clubID=" + club.getClubId();

        }
    }

    /**
     * Sets team club using team's setTeamClub method, clears team club if sport changed and throws errors if teams don't have the same sport or if team is already in a club
     * @param selectedTeams     list of teams the user has selected to add to the club
     * @param club              club to add teams to
     * @param bindingResult      used to store errors
     */
    public void setTeamsClub(List<String> selectedTeams, Club club, BindingResult bindingResult) {
        for (Team teamsAlreadyInClub : teamService.findTeamsByClub(club)) {
            teamsAlreadyInClub.clearTeamClub();
        }
        try {
            if (selectedTeams != null) {
                for (String team : selectedTeams) {
                    if ( teamService.getTeam(Long.parseLong(team)).getTeamClub() != null ) {
                        bindingResult.addError(new FieldError(CREATE_CLUB_FORM_STRING, SELECTED_TEAM_STRING, teamService.getTeam(Long.parseLong(team)).getName() + " already belongs to another club"));
                    }
                    else {
                        teamService.getTeam(Long.parseLong(team)).setTeamClub(club);
                    }
                }
            }
        }
        catch (UnmatchedSportException e) {
            bindingResult.addError(new FieldError(CREATE_CLUB_FORM_STRING, SELECTED_TEAM_STRING, "Teams must have the same sport"));
        }
    }

    /**
     * Prefills model with club fields
     * @param model model to be filled
     * @param club  club to get info from
     */
    public void prefillModelWithClub(Model model, Club club) {
        model.addAttribute("clubId", club.getClubId());
        model.addAttribute("name", club.getName());
        model.addAttribute("sport", club.getSport());
        model.addAttribute(ADDRESS_LINE_1_STRING, club.getLocation().getAddressLine1());
        model.addAttribute("addressLine2", club.getLocation().getAddressLine2());
        model.addAttribute("suburb", club.getLocation().getSuburb());
        model.addAttribute("postcode", club.getLocation().getPostcode());
        model.addAttribute("city", club.getLocation().getCity());
        model.addAttribute("country", club.getLocation().getCountry());
        model.addAttribute(SELECTED_TEAM_STRING, teamService.findTeamsByClub(club));
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
        model.addAttribute(HTTP_SERVLET_REQUEST_STRING, httpServletRequest);
        model.addAttribute("listOfTeams", teamsUserManagerOf);
    }

    /**
     * Gets all teams with matching sport, and clubId, for a club to join. This is to ensure clubs can only add teams to their club that have the same sport, or
     * are not already in a club. and the list also includes teams that are part of the club itself, which is why we ask for the club id.
     * @param sport sport to match teams with
     * @param clubId club id to match teams with if it's part of this club
     * @return list of teams
     */
    @GetMapping("/teams_by_sport")
    public ResponseEntity<List<ClubTeamInfo>> getTeamsBySport(@RequestParam String sport, @RequestParam String clubId) {

        Optional<Club> optClub = clubService.findClubById(Long.parseLong(clubId));

        return optClub.map(club -> ResponseEntity.ok().body(
                teamService.findTeamsBySportAndClubOrNotInClub(sport, club).stream().map(team -> new ClubTeamInfo(
                        team.getTeamId(),
                        team.getName()
                )).toList()
        )).orElseGet(() -> ResponseEntity.ok().body(List.of()));
    }


    /**
     * Gets list of teams that match the sport and are also not part of a club
     * @param sport sport to match teams with
     * @return list of teams
     */
    @GetMapping("/teams_by_sport_not_in_club")
    public ResponseEntity<List<ClubTeamInfo>> getTeamsBySportNotInClub(@RequestParam String sport) {
            return ResponseEntity.ok().body(
                    teamService.findTeamsBySportAndNotInClub(sport).stream().map(team -> new ClubTeamInfo(
                            team.getTeamId(),
                            team.getName()
                    )).toList()
            );
    }


}
