package nz.ac.canterbury.seng302.tab.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nz.ac.canterbury.seng302.tab.entity.*;
import nz.ac.canterbury.seng302.tab.form.CreateAndEditClubForm;
import nz.ac.canterbury.seng302.tab.service.ClubService;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Spring Boot Controller class for the Create Club Form
 */
@Controller
public class CreateClubController {

    Logger logger = LoggerFactory.getLogger(CreateClubController.class);

    @Autowired
    private ClubService clubService;

    @Autowired
    private final UserService userService;


    private final TeamService teamService;

    public CreateClubController(ClubService clubService,UserService userService, TeamService teamService) {
        this.clubService = clubService;
        this.userService = userService;
        this.teamService = teamService;
    }

    /**
     * Gets club form
     * @param clubId  if editing, get club through clubId
     * @param model   model
     * @param request http request
     * @param createAndEditClubForm form data
     * @return create club form
     * @throws MalformedURLException
     */
    @GetMapping("/createClub")
    public String clubForm(@RequestParam(name = "edit", required = false) Long clubId,
                           Model model,
                           HttpServletRequest request, CreateAndEditClubForm createAndEditClubForm) throws MalformedURLException {

        logger.info("GET /createClub");
        model.addAttribute("httpServletRequest", request);
        Optional<User> user = userService.getCurrentUser();


        List<Team> teamsUserManagerOf = new ArrayList<>();
        for (Team team : teamService.getTeamList()) {
            if (team.isManager(user.get())) {
                teamsUserManagerOf.add(team);
            }
        }

        model.addAttribute("listOfTeams", teamsUserManagerOf);

        Club club;

        if (clubId != null) {
            if ((club = clubService.findClubById(clubId).get()) != null) {
                model.addAttribute("name", club.getName());
                model.addAttribute("addressLine1", club.getLocation().getAddressLine1());
                model.addAttribute("addressLine2", club.getLocation().getAddressLine2());
                model.addAttribute("city", club.getLocation().getCity());
                model.addAttribute("suburb", club.getLocation().getSuburb());
                model.addAttribute("country", club.getLocation().getCountry());
                model.addAttribute("postcode", club.getLocation().getPostcode());
                model.addAttribute("clubId", club.getClubId());
            } else {
                model.addAttribute("invalidClub", "Invalid club ID, creating club");
            }
        }

      //  Optional<User> user = userService.getCurrentUser();
        model.addAttribute("firstName", user.get().getFirstName());
        model.addAttribute("lastName", user.get().getLastName());
        model.addAttribute("displayPicture", user.get().getPictureString());
        model.addAttribute("navTeams", teamService.getTeamList());


        return "createClubForm";
    }


    @PostMapping("/createClub")
    public String createClub(
            @RequestParam(name = "clubId", defaultValue = "-1") long clubId,
            @RequestParam(name="name") String name,
            @RequestParam(name = "addressLine1") String addressLine1,
            @RequestParam(name = "addressLine2") String addressLine2,
            @RequestParam(name = "city") String city,
            @RequestParam(name = "country") String country,
            @RequestParam(name = "postcode") String postcode,
            @RequestParam(name = "suburb") String suburb,
            @RequestParam(name = "selectedTeams") List<Team> selectedTeams,
            @Validated CreateAndEditClubForm createAndEditClubForm,
            BindingResult bindingResult,
            HttpServletResponse httpServletResponse,
            Model model,
            HttpServletRequest httpServletRequest) throws IOException {
        model.addAttribute("httpServletRequest", httpServletRequest);
        //prefillModel(model, httpServletRequest);


        addressLine1.trim();
        if (addressLine1.isEmpty()) {
            bindingResult.addError(new FieldError("CreateAndEditClubForm", "addressLine1", "Field cannot be empty"));
        }
        postcode.trim();
        if (postcode.isEmpty()) {
            bindingResult.addError(new FieldError("CreateAndEditClubForm", "postcode", "Field cannot be empty"));
        }

        if (!clubService.validateTeamSportsinClub(selectedTeams)) {
            System.out.println("Teams should have same sport" + selectedTeams);
            bindingResult.addError(new FieldError("CreateAndEditClubForm", "selectedTeams", "Teams must have same sport"));

        }

        if (bindingResult.hasErrors()) {
            httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "createClubForm";
        }

        Location location = new Location(addressLine1, addressLine2, suburb, city, postcode, country);
        Club club = new Club(name, location);


        club.addTeam(selectedTeams);
        clubService.updateOrAddClub(club);


        return "redirect:/home"; //TODO Redirect to view club page when it's done




    }

}
