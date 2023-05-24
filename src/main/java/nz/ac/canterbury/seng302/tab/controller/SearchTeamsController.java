package nz.ac.canterbury.seng302.tab.controller;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.repository.TeamRepository;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;
/**
 * Spring Boot Controller class for Search Teams
 */
@Controller
public class SearchTeamsController {

    Logger logger = LoggerFactory.getLogger(nz.ac.canterbury.seng302.tab.controller.SearchTeamsController.class);

    @Autowired
    private TeamService teamService;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private UserService userService;
    
    private static final int PAGE_SIZE = 10;

    /**
     * Gets searchTeamsForm to be displayed
     *
     * @param teamName name of the team that has been searched for by the database
     * @param page return the page number
     * @param filteredCities list of cities to filter teams by, selected by the user
     * @param model    (map-like) representation of teamID and teamFilter
     * @return thymeleaf searchTeamsForm
     */
    @GetMapping("/searchTeams")
    public String searchTeams(@RequestParam(value = "teamName", required = false) String teamName,
                              @RequestParam(value = "page", defaultValue = "0") int page,
                              @RequestParam(value = "cityCheckbox", required = false) List<String> filteredCities,
                              @RequestParam(value = "sports", required = false) List<String> filteredSports,
                              Model model,
                              HttpServletRequest request) {
        boolean notSearch = false;
        model.addAttribute("httpServletRequest",request);

        logger.info("cityCheckBox = {}", filteredCities);
        logger.info("teamName = {}", teamName);
        // If teamName isn't defined, we show them nothing.
        // This is assumed to be the "hasn't started searching" state, so 
        // we only show them the search bar.
        if (teamName == null) {
            model.addAttribute("teams", List.of());
            notSearch = true;
        // Searches are required to be at least 3 chars long
        } else if (teamName.length() < 3) {
            model.addAttribute("teams", List.of());
            model.addAttribute("error", true);
        } else {
            PageRequest pageRequest = PageRequest.of(page, PAGE_SIZE);
            Page<Team> teamPage = teamService.findPaginatedTeamsByCityAndSports(pageRequest, filteredCities, filteredSports, teamName);
            List<Team> teams = teamPage.getContent();
            int numPages = teamPage.getTotalPages();
            // Get all the sports of the given queried users to populdate the dropdown
            var sports = teamRepository.findSportsByName(teamName).stream()
                    .distinct()
                    .sorted()
                    .toList();
            // Same as above, but for cities
            List<String> cities = teamRepository.findLocationsByName(teamName).stream()
                    .map(Location::getCity)
                    .distinct()
                    .sorted()
                    .toList();
            model.addAttribute("sports", sports);
            model.addAttribute("teams", teams);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", numPages);
            model.addAttribute("teamName", teamName);
            model.addAttribute("cities", cities);
        }
        model.addAttribute("navTeams", teamService.getTeamList());
        model.addAttribute("notSearch", notSearch);
        return "searchTeamsForm";
    }
}
