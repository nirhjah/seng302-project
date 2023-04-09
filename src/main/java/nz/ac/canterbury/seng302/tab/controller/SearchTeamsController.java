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
import java.util.Collections;
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
                int pageSize = 10; // number of results per page
                PageRequest pageRequest = PageRequest.of(page, pageSize);
                Page<Team> teamPage = teamService.findPaginatedTeamsByCity(pageRequest, filteredCities, teamName);
                List<Team> teams = teamPage.getContent();
                //Gets cities to populate in dropdown
                List<Location> locations = teamRepository.findLocationsByName(teamName);
                List<String> cities = new ArrayList<>();
                for (Location location: locations) {
                    if (!cities.contains(location.getCity().toLowerCase())) {
                        cities.add(location.getCity().toLowerCase());
                        Collections.sort(cities);
                    }
                }
                int numPages = teamPage.getTotalPages();
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
        model.addAttribute("navTeams", teamService.getTeamList());
        return "searchTeamsForm";
    }
}
