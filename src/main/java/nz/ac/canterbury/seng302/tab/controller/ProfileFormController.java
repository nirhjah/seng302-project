package nz.ac.canterbury.seng302.tab.controller;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for profile form
 */
@Controller
public class ProfileFormController {

    Logger logger = LoggerFactory.getLogger(ProfileFormController.class);
    private long teamId;
    @Autowired
    private TeamService teamService;
    /**
     * Gets form to be displayed, includes the ability to display results of previous form when linked to from POST form
     * @param teamID team for which the details are to be displayed
     * @param model (map-like) representation of name, language and isJava boolean for use in thymeleaf
     * @return thymeleaf profileForm
     */


    @GetMapping("/profile_form")
    public String profileForm(Model model,
                              @RequestParam(value = "teamID", required = false) Long teamID) {
        logger.info("GET /profile_form");


        // Retrieve the selected team from the list of available teams using the ID
        // If the name is null or empty, return null
        List<Team> teamList = teamService.getTeamList();
        Team selectedTeam = null;
        String teamName = null;
        String teamLocation= null;
        String teamSport= null;
        String teamPicture=null;
        if (teamID != null) {
            // Find the selected team by its id
            selectedTeam = teamList.stream()
                    .filter(team -> team.getTeamId().equals(teamID))
                    .findFirst()
                    .orElse(null);
        }
        if (selectedTeam != null) {
            teamName=selectedTeam.getName() ;
            teamLocation=selectedTeam.getSport();
            teamSport=selectedTeam.getLocation();
            teamPicture= selectedTeam.getPictureString();
        }
        this.teamId= teamID;
        model.addAttribute("displayTeams", teamList);
        model.addAttribute("teamID", teamID);
        model.addAttribute("displayName", teamName);
        model.addAttribute("displaySport", teamLocation);
        model.addAttribute("displayLocation", teamSport);
        model.addAttribute("displayPicture", teamPicture);

        return "profileForm";
    }

    @PostMapping("/profile_form")
    public RedirectView uploadPicture(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes, Model model)
    {

        model.addAttribute("teamID", this.teamId);
        if (file.isEmpty()){
            redirectAttributes.addFlashAttribute("emptyFileError", true);
            return new RedirectView("/profile_form?teamID=" + this.teamId, true);
        }

        if (!isSupportedContentType(file.getContentType())){
            redirectAttributes.addFlashAttribute("typeError", true);
            return new RedirectView("/profile_form?teamID=" + this.teamId, true);
        }
        if (file.getSize()>10000000){
            redirectAttributes.addFlashAttribute("sizeError", true);
            return new RedirectView("/profile_form?teamID=" + this.teamId, true);
        }
        teamService.updatePicture(file,this.teamId );
        return new RedirectView("/profile_form?teamID=" + this.teamId, true);
    }
    private boolean isSupportedContentType(String contentType){
        return contentType.equals("image/png")|| contentType.equals("image/jpg")||contentType.equals("image/svg+xml")|| contentType.equals("image/jpeg");
    }

}


