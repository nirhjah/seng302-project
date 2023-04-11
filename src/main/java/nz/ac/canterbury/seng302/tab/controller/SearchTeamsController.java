package nz.ac.canterbury.seng302.tab.controller;

import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.service.SportService;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

import nz.ac.canterbury.seng302.tab.repository.TeamRepository;

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
    private SportService sportService;

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
                              Model model) {
        boolean notSearch = false;
        logger.info("cityCheckBox = {}", filteredCities);
        logger.info("teamName = {}", teamName);
        if (teamName != null ) {
            if (teamName.length() < 3) {
                model.addAttribute("error", true);
                model.addAttribute("teams", new ArrayList<Team>());
            }
            else {
                PageRequest pageRequest = PageRequest.of(page, PAGE_SIZE);
                Page<Team> teamPage = teamService.findPaginatedTeamsByCityAndSports(pageRequest, filteredCities, filteredSports, teamName);
                List<Team> teams = teamPage.getContent();
                // Get all the sports of the given queried users
                var sports = teamRepository.findTeamByName(teamName, PageRequest.of(0, Integer.MAX_VALUE)).stream()
                        .map(team -> team.getSport().toLowerCase())
                        .distinct()
                        .sorted()
                        .toList();
                // Gets cities to populate in dropdown
                List<Location> locations = teamRepository.findLocationsByName(teamName);
                List<String> cities = locations.stream()
                        .map(Location::getCity)
                        .distinct()
                        .sorted()
                        .toList();
                int numPages = teamPage.getTotalPages();
                model.addAttribute("sports", sports);
                model.addAttribute("teams", teams);
                model.addAttribute("currentPage", page);
                model.addAttribute("totalPages", numPages);
                model.addAttribute("teamName", teamName);
                model.addAttribute("cities", cities);
            }
        } else {
            model.addAttribute("teams", new ArrayList<Team>());
            notSearch = true;
        }
        model.addAttribute("navTeams", teamService.getTeamList());
        model.addAttribute("notSearch", notSearch);
        return "searchTeamsForm";
    }
}
