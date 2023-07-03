package nz.ac.canterbury.seng302.tab.controller;

import jakarta.servlet.http.HttpServletRequest;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.MalformedURLException;
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
    private UserService userService;

    @Autowired
    private TeamService teamService;

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


        Club club;

        if (clubId != null) {
            if ((club = clubService.findClubById(clubId)) != null) {
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

        Optional<User> user = userService.getCurrentUser();
        model.addAttribute("firstName", user.get().getFirstName());
        model.addAttribute("lastName", user.get().getLastName());
        model.addAttribute("displayPicture", user.get().getPictureString());
        model.addAttribute("navTeams", teamService.getTeamList());

        return "createClub";
    }

}
