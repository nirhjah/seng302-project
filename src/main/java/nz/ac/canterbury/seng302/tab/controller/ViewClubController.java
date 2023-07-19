package nz.ac.canterbury.seng302.tab.controller;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.tab.entity.Club;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.service.ClubService;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Base64;
import java.util.Optional;
@Controller
public class ViewClubController {

    @Autowired
    private TeamService teamService;

    @Autowired
    private UserService userService;

    @Autowired
    private ClubService clubService;
    Logger logger = LoggerFactory.getLogger(getClass());


    public ViewClubController(UserService userService, TeamService teamService,ClubService clubService) {
        this.userService = userService;
        this.teamService = teamService;
        this.clubService=clubService;
    }



    /**
     * Retrieves the club profile.
     *
     * @param model    the model object for adding attributes
     * @param clubId   the ID of the club to view
     * @param request  the HttpServletRequest object
     * @return the view name for displaying the club profile form
     */
    @GetMapping("/view-club")
    public String profileForm(
            Model model,
            @RequestParam(value = "clubID") Long clubId,
            HttpServletRequest request) {

        model.addAttribute("httpServletRequest", request);
        logger.info("GET /view club");

        Optional<User> user = userService.getCurrentUser();
        if (user.isEmpty()) {
            logger.error("No current user?");
            return "redirect:/home";
        }
        currentUserNavDisplay(user.get(), model);
        Optional<Club> optClub= clubService.findClubById(clubId);
        if (optClub.isEmpty()){
            return "redirect:/home";
        }
        Club club = optClub.get();
        model.addAttribute("club",club);
        model.addAttribute("clubLogo", club);
        model.addAttribute("location", club.getLocation().toString());
        return "viewClub";
    }


    /**
     * Method which injects the current user and team list information into the view
     * so that it will be displayed on the nav bar
     * @param user the current user who is logged into the system
     * @param model the Model object used to add attributes for the view
     */
    public void currentUserNavDisplay(User user, Model model){
        model.addAttribute("firstName", user.getFirstName());
        model.addAttribute("lastName", user.getLastName());
        model.addAttribute("displayPicture", user.getPictureString());
        model.addAttribute("navTeams", teamService.getTeamList());
    }
}
