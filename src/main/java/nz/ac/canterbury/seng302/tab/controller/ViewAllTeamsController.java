package nz.ac.canterbury.seng302.tab.controller;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Sport;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.helper.GenerateRandomTeams;
import nz.ac.canterbury.seng302.tab.service.LocationService;
import nz.ac.canterbury.seng302.tab.service.SportService;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Spring Boot Controller for View Teams Form
 */
@Controller
public class ViewAllTeamsController {
    private static final int MAX_PAGE_SIZE = 10;

    public static final Sort SORT_BY_TEAM_NAME = Sort.by(
            Sort.Order.asc("name").ignoreCase(),
            Sort.Order.asc("location.city").ignoreCase()
    );

    Logger logger = LoggerFactory.getLogger(ViewAllTeamsController.class);

    private TeamService teamService;
    private UserService userService;
    private LocationService locationService;
    private SportService sportService;

    @Autowired
    GenerateRandomTeams generateRandomTeams;


    /**
     * TODO: Remove all this stuff before merging into dev!
     *
     * IF YOU ARE REVIEWING THIS CODE RIGHT NOW, PLEASE
     * DENY THIS MR.
     * THIS CODE (populate_teams) SHOULD NOT BE PUSHED TO DEV,
     * AND IS ONLY FOR DEBUG PURPOSES!!!!
     */
    int NUM_TEAMS_TO_GENERATE = 20;

    // Should only be used for testing purposes!!!
    @GetMapping("/populate_teams")
    public String populateTestTeams() throws IOException {
        generateRandomTeams.createAndSaveRandomTeams(NUM_TEAMS_TO_GENERATE);
        return "redirect:home";
    }

    @Autowired
    public ViewAllTeamsController(TeamService teamService, UserService userService, LocationService locationService, SportService sportService) {
        this.teamService = teamService;
        this.userService = userService;
        this.locationService = locationService;
        this.sportService = sportService;
    }

    private Pageable getPageable(int page) {
        return PageRequest.of(page, MAX_PAGE_SIZE, SORT_BY_TEAM_NAME);
    }

    private Page<Team> getTeamPage(int page, String currentSearch, List<String> cities, List<String> sports) {
        var pageable = getPageable(page);
        return teamService.findPaginatedTeamsByCityAndSports(pageable, cities, sports, currentSearch);
    }

    private void populateModelBasics(Model model, User user, Page<Team> page) {
        List<Team> listOfTeams = page.getContent();
        model.addAttribute("listOfTeams", listOfTeams);
        model.addAttribute("firstName", user.getFirstName());
        model.addAttribute("lastName", user.getLastName());
        model.addAttribute("displayPicture", user.getPictureString());
        model.addAttribute("navTeams", teamService.getTeamList());
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
    }

    private void populateFilterDropdowns(Model model) {
        var sports = sportService.getAllSports()
                .stream()
                .map(Sport::getName)
                .distinct()
                .toList();
        var cities = locationService.getLocationList()
                .stream()
                .map(Location::getCity)
                .distinct()
                .toList();

        model.addAttribute("sports", sports);
        model.addAttribute("cities", cities);
    }

    /**
     * Gets viewAllTeams doc with required attributes. Reroutes if page out of available range or no teams in database
     * @param pageNo integer corresponding page to be displayed
     * @param model  (map-like) representation of name, language and isJava boolean for use in thymeleaf
     * @return thymeleaf viewAllTeams
     */
    @GetMapping("/view-teams")
    public String findPaginated(
            @RequestParam(name = "page", defaultValue = "1") int pageNo,
            @RequestParam(name = "currentSearch", required = false) String currentSearch,
            @RequestParam(name = "sports", required = false) List<String> sports,
            @RequestParam(name = "cities", required = false) List<String> cities,
            Model model, HttpServletRequest request) {
        logger.info("GET /view-teams");
        model.addAttribute("httpServletRequest", request);
        
        // If no teams exist in the database
        if (teamService.getNumberOfTeams() == 0) {
            return "redirect:/home";
        }

        Page<Team> page = getTeamPage(pageNo, currentSearch, cities, sports);
        var maxPage = page.getTotalPages();
        pageNo = Math.max(Math.min(pageNo, maxPage), 1);

        // Internally, pagination starts at 0 (page 0 is the first)
        // However, we want it to start at 1 for the user.
        var internalPageNo = pageNo - 1;

        Optional<User> opt = userService.getCurrentUser();
        if (opt.isEmpty()) {
            logger.info("GET /view-teams: getCurrentUser() failed!");
            return "redirect:login";
        }
        var user = opt.get();

        populateModelBasics(model, user, page);
        populateFilterDropdowns(model);

        model.addAttribute("page", pageNo);
        model.addAttribute("currentSearch", currentSearch);

        return "viewAllTeams";
    }
}
