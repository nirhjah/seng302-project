package nz.ac.canterbury.seng302.tab.controller;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.tab.entity.Team;
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

    /**
     * Gets searchTeamsForm to be displayed
     *
     * @param teamName name of the team that has been searched for by the database
     * @param page return the page number
     * @param model    (map-like) representation of teamID and teamFilter
     * @return thymeleaf searchTeamsForm
     */
    @GetMapping("/searchTeams")
    public String searchTeams(@RequestParam(value = "teamName", required = false) String teamName,
                              @RequestParam(value = "page", defaultValue = "0") int page,
                              Model model, HttpServletRequest request) {
        model.addAttribute("notSearch", false);
        model.addAttribute("httpServletRequest",request);
        if (teamName != null) {
            if (teamName.length() < 3) {
                model.addAttribute("error", true);
                model.addAttribute("teams", new ArrayList<Team>());
            } else {
                int pageSize = 10; // number of results per page
                PageRequest pageRequest = PageRequest.of(page, pageSize);
                Page<Team> teamPage = teamRepository.findTeamByName(teamName, pageRequest);
                List<Team> teams = teamPage.getContent();
                int numPages = teamPage.getTotalPages();
                model.addAttribute("teams", teams);
                model.addAttribute("currentPage", page);
                model.addAttribute("totalPages", numPages);
                model.addAttribute("teamName", teamName);
            }
        } else {
            model.addAttribute("teams", new ArrayList<Team>());
            model.addAttribute("notSearch", true);
        }
        model.addAttribute("navTeams", teamService.getTeamList());
        return "searchTeamsForm";
    }
}
