package nz.ac.canterbury.seng302.tab.controller;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

/**
 * Spring Boot Controller class for the ProfileForm
 */
@Controller
public class ProfileFormController {

    Logger logger = LoggerFactory.getLogger(ProfileFormController.class);

    @Autowired
    private TeamService teamService;

    @Autowired
    private UserService userService;

    /**
     * Gets form to be displayed, includes the ability to display results of
     * previous form when linked to from POST form
     *
     * @param teamID team for which the details are to be displayed
     * @param model  (map-like) representation of name, language and isJava boolean
     *               for use in thymeleaf
     * @return thymeleaf profileForm
     */
    @GetMapping("/profile")
    public String profileForm(
            Model model,
            @RequestParam(value = "teamID") Long teamID,
            HttpServletRequest request) {
        logger.info("GET /profileForm");

        // Gets the team from the database, or giving a 404 if not found.
        Team selectedTeam = teamService.getTeam(teamID);

        if (selectedTeam == null) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(404));
        }
        model.addAttribute("teamID", teamID);
        model.addAttribute("displayName", selectedTeam.getName());
        model.addAttribute("displaySport", selectedTeam.getSport());
        model.addAttribute("displayLocation", selectedTeam.getLocation());
        model.addAttribute("displayTeamPicture", selectedTeam.getPictureString());
        model.addAttribute("displayToken", selectedTeam.getToken());

        // Is the currently logged in user this team's manager?
        Optional<User> oUser = userService.getCurrentUser();
        if (oUser.isEmpty()) {
            return "redirect:login";
        }
        User user = oUser.get();
        boolean isUserManager = teamService.isUserManagerOfTeam(user.getUserId(), teamID);
        model.addAttribute("isUserManager", isUserManager);
        logger.info("boolean manager is: {}", isUserManager);

        // Rambling that's required for navBar.html
        List<Team> teamList = teamService.getTeamList();
        model.addAttribute("firstName", user.getFirstName());
        model.addAttribute("lastName", user.getLastName());
        model.addAttribute("displayPicture", user.getPictureString());
        model.addAttribute("navTeams", teamList);
        model.addAttribute("httpServletRequest", request);


        return "profileForm";
    }

    /**
     * Gets the image file as a multipartfile and checks if it's a .jpg, .svg, or
     * .png and within size limit. If no, an
     * error message is displayed. Else, the file will be saved in the database as a
     * Byte array.
     *
     * @param file               uploaded MultipartFile file
     * @param redirectAttributes
     * @param model              (map-like) representation of team id
     * @return
     */
    @PostMapping("/profile")
    public String uploadPicture(
            @RequestParam("file") MultipartFile file,
            @RequestParam("teamID") long teamID) {
        teamService.updatePicture(file, teamID);
        return "redirect:/profile?teamID=" + teamID;
    }

}
