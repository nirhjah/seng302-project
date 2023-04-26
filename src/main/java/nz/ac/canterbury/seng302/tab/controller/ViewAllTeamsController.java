package nz.ac.canterbury.seng302.tab.controller;

import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Sport;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.repository.LocationRepository;
import nz.ac.canterbury.seng302.tab.repository.SportRepository;
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
import java.util.Objects;

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

    @Autowired
    private SportRepository sportRepository;

    @Autowired
    private LocationRepository locationRepository;

    private Page<Team> getTeams(List<String> filteredCities, List<String> filteredSports, String searchQuery, int pageNumber) {
        PageRequest pageRequest = PageRequest.of(pageNumber, PAGE_SIZE);
        if (searchQuery == null) {
            return teamService.findPaginatedTeamsByCityAndSports(pageRequest, filteredCities, filteredSports);
        }
        return teamService.findPaginatedTeamsByCityAndSports(pageRequest, filteredCities, filteredSports, searchQuery);
    }

    /**
     * Gets sports that should populate the dropdown
     * @param searchQuery the current team search
     * @return list of sports, as strings
     */
    private List<String> getSportsForDropdown(String searchQuery) {
        boolean hasSearch = !Objects.isNull(searchQuery) && searchQuery.length() >= 3;
        if (hasSearch) {
            return teamRepository.findSportsByName(searchQuery).stream()
                    .distinct()
                    .sorted()
                    .toList();
        } else {
            return sportRepository
                    .findAll()
                    .stream()
                    .map(Sport::getName)
                    .distinct()
                    .toList();
        }
    }

    /**
     * Gets cities that should populate the dropdown
     * @param searchQuery the current team search
     * @return list of sports, as strings
     */
    private List<String> getCitiesForDropDown(String searchQuery) {
        boolean emptySearch = Objects.isNull(searchQuery) || searchQuery.length() < 3;
        if (emptySearch) {
            return locationRepository
                    .findAll()
                    .stream()
                    .map(Location::getCity)
                    .distinct()
                    .toList();
        } else {
            return teamRepository.findLocationsByName(searchQuery).stream()
                    .map(Location::getCity)
                    .distinct()
                    .sorted()
                    .toList();
        }
    }

    /**
     * Populates the sports and cities dropdowns with the appropriate sports and cities
     * for the searchQuery.
     * @param model
     * @param searchQuery the current team name search
     */
    private void populateDropdowns(Model model, String searchQuery) {
        var sports = getSportsForDropdown(searchQuery);
        var cities = getCitiesForDropDown(searchQuery);
        logger.info("city checkbox pop = {}", cities);
        logger.info("sport checkbox pop = {}", sports);
        logger.info("searchQuery = {}", searchQuery);

        model.addAttribute("sports", sports);
        model.addAttribute("cities", cities);
    }

    private void addParametersToModel(Model model, List<String> filteredCities, List<String> filteredSports, String searchQuery, int pageNumber) {
        Page<Team> teamPage;
        if (searchQuery != null && searchQuery.length() < 3) {
            searchQuery = null;
            model.addAttribute("searchTooShortError", true);
        }

        teamPage = getTeams(filteredCities, filteredSports, searchQuery, pageNumber);

        populateDropdowns(model, searchQuery);

        var teams = teamPage.getContent();
        model.addAttribute("searchQuery", searchQuery);
        model.addAttribute("page", pageNumber);
        model.addAttribute("teams", teams);
        logger.info("totalPages = {}", teamPage.getTotalPages());
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
            @RequestParam(value = "page", defaultValue = "1") int pageNo,
            @RequestParam(value = "searchQuery", required = false) String searchQuery,
            @RequestParam(value = "cityCheckbox", required = false) List<String> filteredCities,
            @RequestParam(value = "sports", required = false) List<String> filteredSports,
            Model model)
    {
        if (teamService.getTeamList().size() == 0) {
            // redirect to home if there aren't any teams.
            return "redirect:/home";
        }

        logger.info("GET /view-teams");

        addParametersToModel(model, filteredCities, filteredSports, searchQuery, pageNo);

        model.addAttribute("navTeams", teamService.getTeamList());
        return "viewAllTeams";
    }
}
