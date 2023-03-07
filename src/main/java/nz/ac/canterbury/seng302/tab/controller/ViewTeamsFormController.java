package nz.ac.canterbury.seng302.tab.controller;

import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Controller for profile form
 */
@Controller
public class ViewTeamsFormController {

    private static int maxPageSize = 10;
    Logger logger = LoggerFactory.getLogger(ViewTeamsFormController.class);

    @Autowired
    private TeamService teamService;

    /**
     * Gets form to be displayed, includes the ability to display results of previous form when linked to from POST form
     *
     * @param teamID team for which the details are to be displayed
     * @param model  (map-like) representation of name, language and isJava boolean for use in thymeleaf
     * @return thymeleaf profileForm
     */

    @GetMapping("/viewTeamsForm")
    public String profileForm(Model model,
                              @RequestParam(value = "teamID", required = false) Long teamID,
                              @RequestParam(value = "searchQuery", required = false, defaultValue = "") String searchQuery,
                              @RequestParam(value = "page", defaultValue = "1") int pageNo) {
        logger.info("GET /viewTeamsForm");
        model.addAttribute("teamID", teamID);
        model.addAttribute("searchQuery", searchQuery);
        return findPaginated(pageNo, model);
    }


    @GetMapping()
    public String findPaginated(@PathVariable (value = "pageNo") int pageNo, Model model) {

        // If page number is not in range
        if (pageNo < 1 || pageNo > teamService.findPaginated(pageNo, maxPageSize).getTotalPages()) {
            pageNo = pageNo < 1 ? 1: teamService.findPaginated(pageNo, maxPageSize).getTotalPages();
        }

        Page<Team> page = teamService.findPaginated(pageNo, maxPageSize);

        List<Team> listTeams = page.getContent();

        model.addAttribute("navTeams", teamService.getTeamList());
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("displayTeams", listTeams);
        return "viewTeamsForm";
    }
}
