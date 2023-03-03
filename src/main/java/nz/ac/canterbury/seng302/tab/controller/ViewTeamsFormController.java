package nz.ac.canterbury.seng302.tab.controller;

import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.List;

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

    @GetMapping("/view_teams_form")
    public String profileForm(Model model,
                              @RequestParam(value = "teamID", required = false) Long teamID,
                              @RequestParam(value = "teamFilter", required = false, defaultValue = "") String teamSearchQuery,
                              @RequestParam(value = "page", defaultValue = "1") int pageNumber) {
        logger.info("GET /view_teams_form");
        logger.info(teamSearchQuery);
        List<Team> teamList = teamService.getTeamList();

        // Comparator for sorting teams by location first then name
        Comparator<Team> compareByText = Comparator
                                        .comparing(Team::getLocation)
                                        .thenComparing(Team::getName);

        // Filters and then sorts the list
        teamList = teamList.stream()
                .filter(p -> (p.getLocation().toLowerCase().contains(teamSearchQuery.toLowerCase()) ||
                        p.getName().toLowerCase().contains(teamSearchQuery.toLowerCase())))
                .sorted(compareByText)
                .collect(Collectors.toList());
        int totalPages = (int)Math.ceil((double)teamList.size() / maxPageSize);

        // Prevent users from trying to access pages outside of range
        if (teamList.size() > 0 && pageNumber > (int)Math.ceil(totalPages)) {
            pageNumber = (int)Math.ceil(totalPages);
        } else if (pageNumber < 1) {
            pageNumber = 1;
        }

        if (teamList.size() > 0) {
            // Remove teams that won't fit on the page
            teamList = teamList.stream()
                    .skip((long) (pageNumber - 1) * maxPageSize)
                    .limit(maxPageSize)
                    .collect(Collectors.toList());
        }



        model.addAttribute("navTeams", teamService.getTeamList());
        model.addAttribute("displayTeams", teamList);
        model.addAttribute("teamID", teamID);
        model.addAttribute("pageNumber", pageNumber);
        model.addAttribute("totalPages", totalPages);

        return "viewTeamsForm";
    }
}
