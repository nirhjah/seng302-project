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
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


/**
 * Spring Boot Controller for My Teams Form
 */
@Controller
public class MyTeamsController {

    private static int maxPageSize = 10;

    Logger logger = LoggerFactory.getLogger(MyTeamsController.class);

    @Autowired
    TeamService teamService;


    @GetMapping("/my-teams")
    public String myTeams(@RequestParam(value = "page", defaultValue = "-1") int pageNo,
                          Model model) {

        // If page number outside of page then reloads page with appropriate number
        if (pageNo < 1 || pageNo > teamService.findPaginated(pageNo, maxPageSize).getTotalPages() && teamService.findPaginated(pageNo, maxPageSize).getTotalPages() > 0) {
            pageNo = pageNo < 1 ? 1: teamService.findPaginated(pageNo, maxPageSize).getTotalPages();
            return "redirect:/my-teams?page=" + pageNo;
        }

        logger.info("GET /my-teams");

        Page<Team> page = teamService.findPaginated(pageNo, maxPageSize);

        List<Team> listTeams = page.getContent();

        model.addAttribute("navTeams", teamService.getTeamList());
        model.addAttribute("page", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("displayTeams", listTeams);

        return "myTeams";
    }
}
