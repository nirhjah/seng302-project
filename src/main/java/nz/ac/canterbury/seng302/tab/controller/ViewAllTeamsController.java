package nz.ac.canterbury.seng302.tab.controller;

import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.repository.TeamRepository;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Spring Boot Controller for View Teams Form
 */
@Controller
public class ViewAllTeamsController {
    private static final int PAGE_SIZE = 10;
    Logger logger = LoggerFactory.getLogger(ViewAllTeamsController.class);

    @Autowired
    private TeamService teamService;

    @Autowired
    private TeamRepository teamRepository;

    private Page<Team> getTeams(List<String> filteredCities, List<String> filteredSports, String searchQuery, int pageNumber) {
        PageRequest pageRequest = PageRequest.of(pageNumber, PAGE_SIZE);
        return teamService.findPaginatedTeamsByCityAndSports(pageRequest, filteredCities, filteredSports, searchQuery);
    }

    /**
     * Populates the sports and cities dropdowns with the appropriate sports and cities
     * for the searchQuery.
     * @param model
     * @param searchQuery the current team name search
     */
    private void populateDropdowns(Model model, String searchQuery) {
        var sports = teamRepository.findSportsByName(searchQuery).stream()
                .distinct()
                .sorted()
                .toList();

        List<String> cities = teamRepository.findLocationsByName(searchQuery).stream()
                .map(Location::getCity)
                .distinct()
                .sorted()
                .toList();

        logger.info("cityCheckBox = {}", cities);
        logger.info("searchQuery = {}", searchQuery);

        model.addAttribute("sports", sports);
        model.addAttribute("cities", cities);
    }

    private Page<Team> getAllTeams(int page) {
        return teamService.findPaginated(page, PAGE_SIZE);
    }

    private void addParametersToModel(Model model, List<String> filteredCities, List<String> filteredSports, String searchQuery, int pageNumber) {
        Page<Team> teamPage;
        if (searchQuery == null) {
            teamPage = getAllTeams(pageNumber);
        } else if (searchQuery.length() < 3) {
            model.addAttribute("searchTooShortError", true);
            teamPage = getAllTeams(pageNumber);
        } else {
            teamPage = getTeams(filteredCities, filteredSports, searchQuery, pageNumber);
        }

        populateDropdowns(model, searchQuery);

        var teams = teamPage.getContent();
        model.addAttribute("searchQuery", searchQuery);
        model.addAttribute("page", pageNumber);
        model.addAttribute("teams", teams);
        model.addAttribute("totalPages", teamPage.getTotalPages());
    }

    /**
     * Gets viewAllTeams doc with required attributes. Reroutes if page out of available range or no teams in database
     * @param pageNo integer corresponding page to be displayed
     * @param model  (map-like) representation of name, language and isJava boolean for use in thymeleaf
     * @param searchQuery The current search. This represents a team name.
     * @return thymeleaf viewAllTeams
     */
    @GetMapping("/view-teams")
    public String findPaginated(
            @RequestParam(value = "searchQuery", required = false) String searchQuery,
            @RequestParam(value = "page", defaultValue = "-1") int pageNo,
            @RequestParam(value = "cityCheckbox", required = false) List<String> filteredCities,
            @RequestParam(value = "sports", required = false) List<String> filteredSports,
            Model model)
    {
        logger.info("GET /view-teams");

        addParametersToModel(model, filteredCities, filteredSports, searchQuery, pageNo);

        model.addAttribute("navTeams", teamService.getTeamList());
        return "viewAllTeams";
    }
}
