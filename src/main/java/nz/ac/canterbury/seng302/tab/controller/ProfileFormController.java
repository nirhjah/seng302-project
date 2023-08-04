package nz.ac.canterbury.seng302.tab.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import nz.ac.canterbury.seng302.tab.service.*;
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
import nz.ac.canterbury.seng302.tab.entity.Activity;
import nz.ac.canterbury.seng302.tab.entity.Formation;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;

/**
 * Spring Boot Controller class for the ProfileForm
 */
@Controller
public class ProfileFormController {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private TeamImageService teamImageService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private UserService userService;

    @Autowired
    private FormationService formationService;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private FactService factService;

    public ProfileFormController(UserService userService, TeamService teamService, ActivityService activityService, FactService factService, FormationService formationService) {
        this.userService = userService;
        this.formationService = formationService;
        this.teamService = teamService;
        this.activityService = activityService;
        this.factService = factService;
    }

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
        model.addAttribute("displayToken", team.getToken());
        model.addAttribute("clubId",teamService.getTeamClubId(team));

        if( team.getTeamClub()!=null){
            model.addAttribute("clubName",team.getTeamClub().getName());
        }

        // Is the currently logged in user this team's manager?
        Optional<User> oUser = userService.getCurrentUser();
        if (oUser.isEmpty()) {
            return "redirect:login";
        }
        User user = oUser.get();

        int totalWins = activityService.getNumberOfWins(team);
        int totalLosses = activityService.getNumberOfLoses(team);
        int totalDraws = activityService.getNumberOfDraws(team);
        int totalGamesAndFriendlies = activityService.numberOfTotalGamesAndFriendlies(team);

        List<Activity> activities = activityService.getLast5GamesOrFriendliesForTeamWithOutcome(team);
        List<Map<User, Long>> scorerAndPoints = factService.getTop5Scorers(team);

        model.addAttribute("top5Scorers", scorerAndPoints);
        model.addAttribute("last5GOrF", activities);
        model.addAttribute("totalWins", totalWins);
        model.addAttribute("totalLosses", totalLosses);
        model.addAttribute("totalDraws", totalDraws);
        model.addAttribute("totalGOrF", totalGamesAndFriendlies);

        // Rambling that's required for navBar.html
        List<Formation> formationsList = formationService.getTeamsFormations(teamID);
        model.addAttribute("httpServletRequest", request);
        model.addAttribute("isUserManager", team.isManager(user));
        model.addAttribute("isUserManagerOrCoach", team.isManager(user) || team.isCoach(user));
        model.addAttribute("formations", formationsList);

        return "profileForm";
    }

    /**
     * Gets the image file as a multipartfile and checks if it's a .jpg, .svg, or
     * .png and within size limit. If no, an
     * error message is displayed. Else, the file will be saved in the database as a
     * Byte array.
     *
     * @param file               uploaded MultipartFile file
     * @return Takes user back to profile page
     */
    @PostMapping("/profile")
    public String uploadPicture(
            @RequestParam("file") MultipartFile file,
            @RequestParam("teamID") long teamID) {
        logger.info("POST /profile");
        teamImageService.updateProfilePicture(teamID, file);
        return "redirect:/profile?teamID=" + teamID;
    }

    /**
     * Saves formation into the system or updates formation.
     *
     * @param newFormation formation string
     * @param teamID id of the team to add the formation to
     * @param formationID if a formation is being updated, then this will represent the id of said formation
     * @param customPlayerPositions if the formation is a 'custom' formation then this will be a string of px elements
     *                              describing the left and bottom displacement for each player in a form such as
     *                              "20px30px;40px20px"
     * @param custom boolean to represent whether a formation has been manually changed by dragging and dropping the
     *               players rather than simply being from a generated formation string
     * @return reloads the page
     */
    @PostMapping("/profile/create-formation")
    public String createAndUpdateFormation(
            @RequestParam("formation") String newFormation,
            @RequestParam("teamID") long teamID,
            @RequestParam(name="formationID", defaultValue = "-1") long formationID,
            @RequestParam("customPlayerPositions") String customPlayerPositions,
            @RequestParam("custom") Boolean custom) {
        logger.info("POST /profile");
        Team team = teamService.getTeam(teamID);
        Optional<Formation> formationOptional = formationService.getFormation(formationID);
        Formation formation;
        if (formationOptional.isPresent()) {
            formation = formationOptional.get();
            formation.setFormation(newFormation);
        } else {
            formation = new Formation(newFormation, team);
        }
        formation.setCustomPlayerPositions(customPlayerPositions);
        formation.setCustom(custom);
        formationService.addOrUpdateFormation(formation);
        return "redirect:/profile?teamID=" + teamID;
    }

}
