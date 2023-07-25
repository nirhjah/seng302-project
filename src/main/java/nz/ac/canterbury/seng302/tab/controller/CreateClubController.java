package nz.ac.canterbury.seng302.tab.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nz.ac.canterbury.seng302.tab.entity.*;
import nz.ac.canterbury.seng302.tab.form.CreateAndEditClubForm;
import nz.ac.canterbury.seng302.tab.helper.exceptions.UnmatchedSportException;
import nz.ac.canterbury.seng302.tab.service.ClubService;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

/**
 * Spring Boot Controller class for the Create Club Form
 */
@Controller
public class CreateClubController {

    Logger logger = LoggerFactory.getLogger(CreateClubController.class);

    private ClubService clubService;

    private final UserService userService;

    private final TeamService teamService;

    @Autowired
    public CreateClubController(ClubService clubService,UserService userService, TeamService teamService) {
        this.clubService = clubService;
        this.userService = userService;
        this.teamService = teamService;
    }

    public void updateClubLogo(Club club, MultipartFile file) throws IOException {
        Optional<User> optUser = userService.getCurrentUser();
        if (optUser.isEmpty()) {
            return;
        }

        User user = optUser.get();

        if (!club.isManagedBy(user)) {
            // club isn't managed by current user! return.
            return;
        }

        String data = Base64.getEncoder().encodeToString(file.getBytes());
        club.setClubLogo(data);
    }


    /**
     * Gets club form
     * @param clubId  if editing, get club through clubId
     * @param model   model
     * @param request http request
     * @param createAndEditClubForm form data
     * @return create club form
     */
    @GetMapping("/createClub")
    public String clubForm(@RequestParam(name = "edit", required = false) Long clubId,
                           Model model,
                           HttpServletRequest request, CreateAndEditClubForm createAndEditClubForm) {

        logger.info("GET /createClub");
        prefillModel(model, request);

        Club club;

        if (clubId != null) {
            Optional<Club> optClub = clubService.findClubById(clubId);
            if (optClub.isPresent()) {
                prefillModelWithClub(model, optClub.get());
            }
        }
        return "createClubForm";
    }


    @PostMapping("/createClub")
    public String createClub(
            @RequestParam(name = "clubId", defaultValue = "-1") long clubId,
            @RequestParam(name="name") String name,
            @RequestParam(name="file") MultipartFile clubLogo,
            @RequestParam(name = "addressLine1") String addressLine1,
            @RequestParam(name = "addressLine2") String addressLine2,
            @RequestParam(name = "city") String city,
            @RequestParam(name = "country") String country,
            @RequestParam(name = "postcode") String postcode,
            @RequestParam(name = "suburb") String suburb,
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

        addressLine1 = addressLine1.trim();
        if (addressLine1.isEmpty()) {
            bindingResult.addError(new FieldError("CreateAndEditClubForm", "addressLine1", "Field cannot be empty"));
        }
        postcode = postcode.trim();
        if (postcode.isEmpty()) {
            bindingResult.addError(new FieldError("CreateAndEditClubForm", "postcode", "Field cannot be empty"));
        }

        Location location = new Location(addressLine1, addressLine2, suburb, city, postcode, country);

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

            if (clubLogo.getOriginalFilename()==""){
                logger.info("default");
                Resource resource = new ClassPathResource("/static/image/default-club-logo.png");
                InputStream is = resource.getInputStream();
                editClub.setClubLogo(Base64.getEncoder().encodeToString(is.readAllBytes()));
            }
            else{
                editClub.setClubLogo(Base64.getEncoder().encodeToString(clubLogo.getBytes()));
            }


            clubService.updateOrAddClub(editClub);
            return "redirect:/view-club?clubID=" + editClub.getClubId();

        } else {
            User manager = optUser.get(); // manager is the current user
            Club club;
            if (clubLogo.getOriginalFilename()==""){
                logger.info("default");
                Resource resource = new ClassPathResource("/static/image/default-club-logo.png");
                InputStream is = resource.getInputStream();
                String defaultLogo = Base64.getEncoder().encodeToString(is.readAllBytes());
                club= new Club(name, location, sport, manager,defaultLogo);
            }
            else {
                club = new Club(name, location, sport, manager, Base64.getEncoder().encodeToString(clubLogo.getBytes()));
            }

            setTeamsClub(selectedTeams, club, bindingResult);

            if (bindingResult.hasErrors()) {
                System.out.println(bindingResult.getAllErrors());
                httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return "createClubForm";
            }

            clubService.updateOrAddClub(club);
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
                        bindingResult.addError(new FieldError("CreateAndEditClubForm", "selectedTeams", "Team already belongs to another club"));
                    }
                    else {
                        teamService.getTeam(Long.parseLong(team)).setTeamClub(club);
                    }
                }}
        }
        catch (UnmatchedSportException e) {
            bindingResult.addError(new FieldError("CreateAndEditClubForm", "selectedTeams", "Teams must have the same sport"));
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
        model.addAttribute("addressLine1", club.getLocation().getAddressLine1());
        model.addAttribute("addressLine2", club.getLocation().getAddressLine2());
        model.addAttribute("suburb", club.getLocation().getSuburb());
        model.addAttribute("postcode", club.getLocation().getPostcode());
        model.addAttribute("city", club.getLocation().getCity());
        model.addAttribute("country", club.getLocation().getCountry());
        model.addAttribute("selectedTeams", teamService.findTeamsByClub(club));
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
        model.addAttribute("firstName", user.get().getFirstName());
        model.addAttribute("lastName", user.get().getLastName());
        model.addAttribute("displayPicture", user.get().getPictureString());
        model.addAttribute("navTeams", teamService.getTeamList());
    }

}
