package nz.ac.canterbury.seng302.tab.controller;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
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

import java.util.ArrayList;
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

    @Autowired
    private TeamService teamService;

    @Autowired
    private UserService userService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private SportService sportService;

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
        var sports = sportService.getAllSports();
        var cities = locationService.getLocationList()
                .stream()
                .map((location) -> location.getCity())
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
            @RequestParam(value = "page", defaultValue = "-1") int pageNo,
            @RequestParam(value = "currentSearch", defaultValue = "") String currentSearch,
            @RequestParam(name = "sports", required=false) List<String> sports,
            @RequestParam(name = "cities", required = false) List<String> cities,
            Model model, HttpServletRequest request) {
        model.addAttribute("httpServletRequest",request);
        // If no teams exist in the database
        if (teamService.getTeamList().size() == 0) {
            return "redirect:/home";
        }

        // If page number outside of page range then reloads page with appropriate number
        if (pageNo < 1 || pageNo > teamService.findPaginated(pageNo, MAX_PAGE_SIZE).getTotalPages() && teamService.findPaginated(pageNo, MAX_PAGE_SIZE).getTotalPages() > 0) {
            pageNo = pageNo < 1 ? 1: teamService.findPaginated(pageNo, MAX_PAGE_SIZE).getTotalPages();
            return "redirect:/view-teams?page=" + pageNo;
        }

        logger.info("GET /view-teams");

        Optional<User> opt = userService.getCurrentUser();
        if (opt.isEmpty()) {
            logger.info("GET /view-teams: getCurrentUser() failed!");
            return "redirect:/home";
        }
        var user = opt.get();

        Page<Team> page = getTeamPage(pageNo, currentSearch, cities, sports);

        populateModelBasics(model, user, page);
        populateFilterDropdowns(model);

        model.addAttribute("page", pageNo);
        model.addAttribute("currentSearch", currentSearch);

        return "viewAllTeams";
    }
}
