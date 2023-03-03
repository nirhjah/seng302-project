package nz.ac.canterbury.seng302.tab.controller;

import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for profile form
 */
@Controller
public class SearchTeamsController {

    Logger logger = LoggerFactory.getLogger(nz.ac.canterbury.seng302.tab.controller.SearchTeamsController.class);

    private String allUnicodeRegex = "^[\\p{L}\\s\\d\\.\\}\\{]+$";

    @Autowired
    private TeamService teamService;

    /**
     * Gets form to be displayed, includes the ability to display results of previous form when linked to from POST form
     *
     * @param teamID team for which the details are to be displayed
     * @param model  (map-like) representation of name, language and isJava boolean for use in thymeleaf
     * @return thymeleaf profileForm
     */
    @GetMapping("/search_teams_form")
    public String profileForm(Model model,
                              @RequestParam(value = "teamID", required = false) Long teamID,
                              @RequestParam(value = "teamFilter", required = false) String teamSearchQuery) {
        logger.info("GET /search_teams_form");
        model.addAttribute("allUnicodeRegex", allUnicodeRegex);

        return "searchTeamsForm";
    }

    @PostMapping("/search_teams_form")
    public String submitSearchTeams(Model model,
                                    @RequestParam(value = "teamFilter", required = false) String teamSearchQuery) {
        logger.info("POST /search_teams_form");

        // server side validation
        if (!teamSearchQuery.matches(allUnicodeRegex)) {
            model.addAttribute("validationError", true);
            return "searchTeamsForm";
        }

        model.addAttribute("teamFilter", teamSearchQuery);
        List<Team> teamList = teamService.getTeamList();
        if (teamSearchQuery != null) {
            List<Team> filteredList = teamList.stream()
                    .filter(p -> (p.getLocation().toLowerCase().contains(teamSearchQuery.toLowerCase()) ||
                            p.getName().toLowerCase().contains(teamSearchQuery.toLowerCase())))
                    .collect(Collectors.toList());
        }

//        model.addAttribute("displayTeams", teamList);
//        model.addAttribute("teamID", teamID);
        return "searchTeamsForm";
    }
}
