package nz.ac.canterbury.seng302.tab.controller;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

/**
 * Spring Boot Controller for View Teams Form
 */
@Controller
public class ViewAllTeamsController {

    private static int maxPageSize = 10;
    Logger logger = LoggerFactory.getLogger(ViewAllTeamsController.class);

    @Autowired
    private TeamService teamService;

    @Autowired
    private UserService userService;

    /**
     * Gets viewAllTeams doc with required attributes. Reroutes if page out of available range or no teams in database
     * @param pageNo integer corresponding page to be displayed
     * @param model  (map-like) representation of name, language and isJava boolean for use in thymeleaf
     * @return thymeleaf viewAllTeams
     */
    @GetMapping("/view-teams")
    public String findPaginated(@RequestParam(value = "page", defaultValue = "-1") int pageNo,
                                Model model, HttpServletRequest request) {
        model.addAttribute("httpServletRequest",request);
        logger.info("GET /view-teams");
        // If no teams exist in the database
        if (teamService.getTeamList().size() == 0) {
            return "redirect:/home";
        }

        // If page number outside of page range then reloads page with appropriate number
        if (pageNo < 1 || pageNo > teamService.findPaginated(pageNo, maxPageSize).getTotalPages() && teamService.findPaginated(pageNo, maxPageSize).getTotalPages() > 0) {
            pageNo = pageNo < 1 ? 1: teamService.findPaginated(pageNo, maxPageSize).getTotalPages();
            return "redirect:/view-teams?page=" + pageNo;
        }

        Page<Team> page = teamService.findPaginated(pageNo, maxPageSize);
        List<Team> listTeams = page.getContent();

        Optional<User> user = userService.getCurrentUser();
        model.addAttribute("firstName", user.get().getFirstName());
        model.addAttribute("lastName", user.get().getLastName());
        model.addAttribute("displayPicture", user.get().getPictureString());
        model.addAttribute("navTeams", teamService.getTeamList());
        model.addAttribute("page", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("displayTeams", listTeams);
        return "viewAllTeams";
    }
}
