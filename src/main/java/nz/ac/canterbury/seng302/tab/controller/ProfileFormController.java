package nz.ac.canterbury.seng302.tab.controller;

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
    public String uploadPicture(@RequestParam("file") MultipartFile file, Model model)
    {
        if (file.isEmpty()){
            model.addAttribute("emptyFileError", true);
            return "profileForm";
        }

        if (!isSupportedContentType(file.getContentType())){
            model.addAttribute("typeError", true);
            return "profileForm";
        }
        if (file.getSize()>1000000){
            model.addAttribute("sizeError",true);
            return "profileForm";
        }
        teamService.updatePicture(file,this.teamId );
        return "profileForm";
    }
    private boolean isSupportedContentType(String contentType){
        return contentType.equals("image/png")|| contentType.equals("image/jpg")||contentType.equals("image/svg");
    }
}

//    /**
//     * Posts a form response with name and favourite language
//     * @param name if user
//     * @param favouriteLanguage users favourite programming language
//     * @param model (map-like) representation of name, language and isJava boolean for use in thymeleaf,
//     *              with values being set to relevant parameters provided
//     * @return thymeleaf profileForm
//     */
//    @PostMapping("/profile_form")
//    public String submitProfileForm( @RequestParam(name="name") String name,
//                              @RequestParam(name = "favouriteLanguage") String favouriteLanguage,
//                              Model model) {
//        logger.info("POST /profile_form");
//        formService.addFormResult(new FormResult(name, favouriteLanguage));
//        model.addAttribute("displayName", name);
//        model.addAttribute("displayFavouriteLanguage", favouriteLanguage);
//        model.addAttribute("isJava", favouriteLanguage.equalsIgnoreCase("java"));
//        return "profileForm";
//    }

