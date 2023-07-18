package nz.ac.canterbury.seng302.tab.controller;

import java.util.List;
import java.util.Optional;

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

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;

/**
 * Spring Boot Controller class for the ProfileForm
 */
@Controller
public class ProfileFormController {

    Logger logger = LoggerFactory.getLogger(getClass());

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
        Team team = teamService.getTeam(teamID);

        if (team == null) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(404));
        }
        model.addAttribute("teamID", teamID);
        model.addAttribute("displayName", team.getName());
        model.addAttribute("displaySport", team.getSport());
        model.addAttribute("displayLocation", team.getLocation());
        model.addAttribute("displayTeamPicture", teamService.getProfilePictureEncodedString(teamID));
        model.addAttribute("displayToken", team.getToken());

        // Is the currently logged in user this team's manager?
        Optional<User> oUser = userService.getCurrentUser();
        if (oUser.isEmpty()) {
            return "redirect:login";
        }
        User user = oUser.get();

        // Rambling that's required for navBar.html
        List<Team> teamList = teamService.getTeamList();
        model.addAttribute("firstName", user.getFirstName());
        model.addAttribute("lastName", user.getLastName());
        model.addAttribute("displayPicture", userService.getEncodedPictureString(user.get().getUserId()));
        model.addAttribute("navTeams", teamList);
        model.addAttribute("httpServletRequest", request);
        model.addAttribute("isUserManager", team.isManager(user));
        model.addAttribute("isUserManagerOrCoach", team.isManager(user) || team.isCoach(user));

        return "profileForm";
    }

    /**
     * Gets the image file as a multipartfile and checks if it's a .jpg, .svg, or
     * .png and within size limit. If no, an
     * error message is displayed. Else, the file will be saved in the database as a
     * Byte array.
     *
     * @param file               uploaded MultipartFile file
     * @return
     */
    @PostMapping("/profile")
    public String uploadPicture(
            @RequestParam("file") MultipartFile file,
            @RequestParam("teamID") long teamID) {
        logger.info("POST /profile");
        teamService.updatePicture(file, teamID);
        return "redirect:/profile?teamID=" + teamID;
    }

}
