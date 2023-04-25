package nz.ac.canterbury.seng302.tab.controller;

import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.service.TeamService;
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
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;

/**
 * Spring Boot Controller class for the ProfileForm
 */
@Controller
public class ProfileFormController {

    Logger logger = LoggerFactory.getLogger(ProfileFormController.class);
    public static long teamId;
    @Autowired
    private TeamService teamService;

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
    public String profileForm(Model model, @RequestParam(value = "teamID", required = false) Long teamID) {
        logger.info("GET /profileForm");

        // Retrieve the selected team from the list of available teams using the ID
        // If the name is null or empty, return null
        List<Team> teamList = teamService.getTeamList();
        ProfileFormController.teamId = teamID;

        Team selectedTeam;
        if (teamID != null) {
            // Find the selected team by its id
            selectedTeam = teamList.stream()
                    .filter(team -> team.getTeamId().equals(teamID))
                    .findFirst()
                    .orElse(null);
        } else {
            return "redirect:./home";
        }

        if (selectedTeam != null) {
            model.addAttribute("displayName", selectedTeam.getName());
            model.addAttribute("displaySport", selectedTeam.getSport());
            model.addAttribute("displayLocation", selectedTeam.getLocation());
            model.addAttribute("displayPicture", selectedTeam.getPictureString());
            model.addAttribute("displayToken", selectedTeam.getToken());
        } else {
            return "redirect:./home";
        }

        model.addAttribute("navTeams", teamList);
        model.addAttribute("teamID", teamID);

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
    public String uploadPicture(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes,
            Model model) {
        model.addAttribute("teamID", ProfileFormController.teamId);
        teamService.updatePicture(file, ProfileFormController.teamId);
        return "redirect:/profile?teamID=" + ProfileFormController.teamId;
    }

}
