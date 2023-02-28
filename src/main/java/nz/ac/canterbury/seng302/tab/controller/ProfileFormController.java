package nz.ac.canterbury.seng302.tab.controller;

import nz.ac.canterbury.seng302.tab.entity.FormResult;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.service.FormService;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for profile form
 */
@Controller
public class ProfileFormController {

    Logger logger = LoggerFactory.getLogger(ProfileFormController.class);

    @Autowired
    private FormService formService;

    @Autowired
    private TeamService teamService;

    /**
     * Gets form to be displayed, includes the ability to display results of previous form when linked to from POST form
     * @param displayName location assigned to the team
     * @param displaySport sport assigned to the team
     * @param displayLocation location assigned to the team
     * @param model (map-like) representation of name, language and isJava boolean for use in thymeleaf
     * @return thymeleaf profileForm
     */
    @GetMapping("/profile_form")
    public String profileForm(@RequestParam(name="displayName",  required = false) String displayName,
                              @RequestParam(name="displaySport",  required = false) String displaySport,
                              @RequestParam(name="displayLocation",  required = false) String displayLocation,
                       Model model) {
        logger.info("GET /profile_form");
        model.addAttribute("displayName", displayName);
        model.addAttribute("displaySport", displaySport);
        model.addAttribute("displayLocation", displayLocation);

        List<Team> displayTeams = new ArrayList<Team>();
//        displayTeams.addAll(teamService.getTeamList());
        displayTeams.add(new Team("Team1", "Tokyo, Japan", "Football"));
        model.addAttribute("displayTeams", displayTeams);
        return "profileForm";
    }


//    /**
//     * Posts a form response with name and favourite language
//     * @param name name if user
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

}

