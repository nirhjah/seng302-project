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
import nz.ac.canterbury.seng302.tab.repository.TeamRepository;
/**
 * Controller for profile form
 */
@Controller
public class SearchTeamsController {

    Logger logger = LoggerFactory.getLogger(nz.ac.canterbury.seng302.tab.controller.SearchTeamsController.class);

    private String allUnicodeRegex = "^[\\p{L}\\s\\d\\.\\}\\{]+$";

    @Autowired
    private TeamService teamService;

    @Autowired
    private TeamRepository teamRepository;

    /**
     * Gets form to be displayed
     *
     * @param model  (map-like) representation of teamID and teamFilter
     * @param teamID team for which the details are to be displayed
     * @return thymeleaf searchTeamsForm
     */
    @GetMapping("/searchTeams")
    public String searchTeams(@RequestParam(value = "teamName", required = false) String teamName,
                              Model model) {
        if (teamName != null) {
            List<Team> teams = teamRepository.findTeamByName(teamName);
            model.addAttribute("teams", teams);
        } else {
            model.addAttribute("teams", new ArrayList<Team>());
        }
        return "searchTeamsForm";
    }
//    @GetMapping("/search_teams")
//    public String profileForm(Model model,
//                              @RequestParam(value = "teamID", required = false) Long teamID,
//                              @RequestParam(value = "teamFilter", required = false) String teamSearchQuery,
//                              TeamRepository team) {
//        logger.info("GET /search_teams_form");
//        model.addAttribute("allUnicodeRegex", allUnicodeRegex);
//
////        List<Team> teamList = teamService.getTeamList();
////        if (teamSearchQuery != null) {
////            List<Team> filteredList = teamList.stream()
////                    .filter(p -> (p.getLocation().toLowerCase().contains(teamSearchQuery.toLowerCase()) ||
////                            p.getName().toLowerCase().contains(teamSearchQuery.toLowerCase())))
////                    .collect(Collectors.toList());
////        }
//        List<Team> teams = team.findTeamByName("teamName");
//        logger.info(teams.toString());
//
//        return "searchTeamsForm";
////        for (Team match : team.findByName("t")) {
////            logger.info("success");
////        }
////        return "searchTeamsForm";
//    }

    /**
     * posts a form response
     *
     * @param model  (map-like) representation of teamID and teamFilter
     * @param teamID team for which the details are to be displayed
     * @return thymeleaf searchTeamsForm
     */
//    @PostMapping("/search_teams")
//    public String submitSearchTeams(Model model,
//                                    @RequestParam(value = "teamFilter", required = false) String teamSearchQuery) {
//        logger.info("POST /search_teams_form");
//
//        // server side validation
//        if (!teamSearchQuery.matches(allUnicodeRegex)) {
//            model.addAttribute("validationError", true);
//            return "searchTeamsForm";
//        }
//
//        model.addAttribute("teamFilter", teamSearchQuery);
//        List<Team> teamList = teamService.getTeamList();
//        logger.info(teamList.get(0).toString());
//        if (teamSearchQuery != null) {
//            List<Team> filteredList = teamList.stream()
//                    .filter(p -> (p.getLocation().toLowerCase().contains(teamSearchQuery.toLowerCase()) ||
//                            p.getName().toLowerCase().contains(teamSearchQuery.toLowerCase())))
//                    .collect(Collectors.toList());
//        }
////        if (teamSearchQuery != null) {
////            List<Team> filteredList = teamList.stream()
////                    .filter(p -> (p.getLocation().toLowerCase().contains(teamSearchQuery.toLowerCase()) ||
////                            p.getName().toLowerCase().contains(teamSearchQuery.toLowerCase())))
////                    .collect(Collectors.toList());
////        }
//
////        model.addAttribute("displayTeams", teamList);
////        model.addAttribute("teamID", teamID);
//        return "searchTeamsForm";
//    }
}
