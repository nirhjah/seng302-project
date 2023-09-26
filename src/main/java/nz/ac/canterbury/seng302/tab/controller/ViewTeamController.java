package nz.ac.canterbury.seng302.tab.controller;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.tab.entity.*;
import nz.ac.canterbury.seng302.tab.service.*;
import nz.ac.canterbury.seng302.tab.service.image.TeamImageService;
import nz.ac.canterbury.seng302.tab.service.image.WhiteboardScreenshotService;
import nz.ac.canterbury.seng302.tab.validator.TeamFormValidators;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Spring Boot Controller class for the ViewTeamForm
 */
@Controller
public class ViewTeamController {

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

    @Autowired
    private WhiteboardScreenshotService whiteboardScreenshotService;

    public ViewTeamController(UserService userService, TeamService teamService, ActivityService activityService, FactService factService, FormationService formationService) {
        this.userService = userService;
        this.formationService = formationService;
        this.teamService = teamService;
        this.activityService = activityService;
        this.factService = factService;

    }

    /**
     * For testing purposes ONLY.
     * Delete this method after we are done testing, thanks
     */
    private WhiteboardScreenshot createMockScreenshot(Team team) {
        Resource resource = new ClassPathResource("/static/image/default-profile.png");
        MultipartFile file = null;
        try {
            InputStream is = resource.getInputStream();
            byte[] bytes = is.readAllBytes();
            // MockMultipartFile isn't available in dev! only in test
            file = new MultipartFile() {
                @Override
                public String getName() {
                    return "my_screenshot";
                }
                @Override
                public String getOriginalFilename() {
                    return "screenshot.png";
                }
                @Override
                public String getContentType() {
                    return "image/png";
                }
                @Override
                public boolean isEmpty() {
                    return false;
                }
                @Override
                public long getSize() {
                    return bytes.length;
                }
                @Override
                public byte[] getBytes() throws IOException {
                    return bytes;
                }
                @Override
                public InputStream getInputStream() throws IOException {
                    return null;
                }
                @Override
                public void transferTo(File dest) throws IOException, IllegalStateException {

                }
            };
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return whiteboardScreenshotService.createScreenshotForTeam(file, team, true);
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
    @GetMapping("/team-info")
    public String profileForm(
            Model model,
            @RequestParam(value = "teamID") Long teamID,
            HttpServletRequest request) {
        logger.info("GET /viewTeamForm");

        // Gets the team from the database, or giving a 404 if not found.
        Team team = teamService.getTeam(teamID);

        if (team == null) {
            logger.error("Couldn't find team with id: {}", teamID);
            throw new ResponseStatusException(HttpStatusCode.valueOf(404));
        }

        // Basic info about this team
        model.addAttribute("teamID", teamID);
        model.addAttribute("displayName", team.getName());
        model.addAttribute("displaySport", team.getSport());
        model.addAttribute("displayLocation", team.getLocation());
        model.addAttribute("displayToken", team.getToken());
        model.addAttribute("clubId",teamService.getTeamClubId(team));
        model.addAttribute("overallPlayersPlaytime", activityService.top5UsersWithPlayTimeAndAverageInTeam(team));
        if( team.getTeamClub()!=null){
            model.addAttribute("clubName",team.getTeamClub().getName());
        }

        // Set<WhiteboardScreenshot> screenshots = team.getScreenshots();
        // model.addAttribute("screenshots", screenshots);
        // This should eventually use team.getRecordings() as opposed to team.getScreenshots()
        // model.addAttribute("recordings", screenshots);
        List<WhiteboardScreenshot> screenshots = new ArrayList<>();
        WhiteboardScreenshot ss = createMockScreenshot(team);
        for (int i=0; i<10; i++) {
            screenshots.add(ss);
        }
        model.addAttribute("screenshots", screenshots);
        model.addAttribute("recordings", screenshots);

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
        Map<User, Long> scorerAndPoints = factService.getTop5Scorers(team);

        model.addAttribute("top5Scorers", scorerAndPoints);
        model.addAttribute("last5GOrF", activities);
        model.addAttribute("totalWins", totalWins);
        model.addAttribute("totalLosses", totalLosses);
        model.addAttribute("totalDraws", totalDraws);
        model.addAttribute("totalGOrF", totalGamesAndFriendlies);

        List<Formation> formationsList = formationService.getTeamsFormations(teamID);
        model.addAttribute("httpServletRequest", request);
        model.addAttribute("isUserManager", team.isManager(user));
        model.addAttribute("isUserManagerOrCoach", team.isManager(user) || team.isCoach(user));
        model.addAttribute("formations", formationsList);

        // Regex info
        model.addAttribute("formationRegex", TeamFormValidators.VALID_FORMATION_REGEX);

        return "viewTeamForm";
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
    @PostMapping("/team-info")
    public String uploadPicture(
            @RequestParam("file") MultipartFile file,
            @RequestParam("teamID") long teamID) {
        logger.info("POST /team-info");
        teamImageService.updateProfilePicture(teamID, file);

//        // THIS CODE IS FOR TESTING ONLY!!!
//        var team = teamService.getTeam(teamID);
//        if (team != null) {
//            whiteboardScreenshotService.createScreenshotForTeam(file, team, false);
//        }

        return "redirect:/team-info?teamID=" + teamID;
    }

    /**
     * <p>
     * Saves formation into the system or updates formation.
     * </p>
     * Restrictions:
     * <ul>
     *   <li> Invalid formation format: <code>400 BAD REQUEST</code> </li>
     *   <li> Team doesn't exist: <code>404 NOT FOUND</code> </li>
     *   <li> User isn't a coach/manager: <code>403 FORBIDDEN</code> </li>
     * </ul>
     *
     * @param newFormation formation string
     * @param teamID id of the team to add the formation to
     * @param formationID if a formation is being updated, then this will represent the id of said formation
     * @param customPlayerPositions if the formation is a 'custom' formation then this will be a string of px elements
     *                              describing the left and bottom displacement for each player in a form such as
     *                              "20px30px;40px20px"
     * @param custom boolean to represent whether a formation has been manually changed by dragging and dropping the
     *               players rather than simply being from a generated formation string
     * @return reloads the page on success, brings you to an error page on failure.
     */
    @PostMapping("/team-info/create-formation")
    public String createAndUpdateFormation(
            @RequestParam("formation") String newFormation,
            @RequestParam("teamID") long teamID,
            @RequestParam(name="formationID", defaultValue = "-1") long formationID,
            @RequestParam("customPlayerPositions") String customPlayerPositions,
            @RequestParam("custom") Boolean custom,
            RedirectAttributes redirectAttributes) {
        logger.info("POST /team-info/create-formation");

        User currentUser = userService.getCurrentUser().orElseThrow();

        /*
         * Note: This endpoint throws exceptions that will bring the user to an error page.
         * Because there should be no way for this endpoint to fail through the website's normal flow,
         * this is alright to do.
         */
        // Is the formation valid?
        if (!newFormation.matches(TeamFormValidators.VALID_FORMATION_REGEX)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, TeamFormValidators.INVALID_FORMATION_MSG);
        }
        Team team = teamService.getTeam(teamID);
        // Does the team exist?
        if (team == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No team with this ID exists");
        }
        // Are you allowed to modify this team?
        if (!team.isCoach(currentUser) && !team.isManager(currentUser)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Insufficient permissions to modify this team's formations");
        }
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
        redirectAttributes.addFlashAttribute("stayOnTab_name", "formations-tab");
        redirectAttributes.addFlashAttribute("stayOnTab_index", 1);

        return "redirect:/team-info?teamID=" + teamID;
    }

}
