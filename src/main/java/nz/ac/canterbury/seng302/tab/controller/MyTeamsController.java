package nz.ac.canterbury.seng302.tab.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.form.JoinTeamForm;
import nz.ac.canterbury.seng302.tab.repository.TeamRepository;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Spring Boot Controller for My Teams Page
 */
@Controller
public class MyTeamsController {

    private static int maxPageSize = 10;

    Logger logger = LoggerFactory.getLogger(ViewAllTeamsController.class);

    @Autowired
    private TeamService teamService;

    @Autowired
    private TeamRepository teamRepository;
    
    @Autowired
    private UserService userService;

    /**
     * Gets the page of teams the user has joined
     * @param pageNo  integer corresponding page to be displayed
     * @param model   representation of data for thymeleaf display
     * @param request required for the navbar implementation
     * @return my teams page
     */
    @GetMapping("/my-teams")
    public String myTeamsForm(@RequestParam(value = "page", defaultValue = "-1") int pageNo,
                                Model model, HttpServletRequest request) {
        model.addAttribute("httpServletRequest",request);

        model.addAttribute("joinTeamForm", new JoinTeamForm());

        Optional<User> user = userService.getCurrentUser();
        User currentUser = user.get();

        model.addAttribute("firstName", user.get().getFirstName());
        model.addAttribute("lastName", user.get().getLastName());
        model.addAttribute("displayPicture", user.get().getPictureString());
        model.addAttribute("navTeams", teamService.getTeamList());
        model.addAttribute("page", pageNo);

        if (teamRepository.findTeamsWithUser_List(currentUser).size() == 0) {
            model.addAttribute("noTeamsFlag", "You are not a member of any teams.");
            return "myTeams";
        }

        model.addAttribute("noTeamsFlag", null);

        if (pageNo < 1 || pageNo > teamService.findTeamsByUser(pageNo, maxPageSize, currentUser).getTotalPages() && teamService.findTeamsByUser(pageNo, maxPageSize, currentUser).getTotalPages() > 0) {
            pageNo = pageNo < 1 ? 1: teamService.findTeamsByUser(pageNo, maxPageSize, currentUser).getTotalPages();
            return "redirect:/my-teams?page=" + pageNo;
        }

        logger.info("GET /my-teams");

        Page<Team> page = teamService.findTeamsByUser(pageNo, maxPageSize, currentUser);
        List<Team> listTeams = page.getContent();

        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("displayTeams", listTeams);

        return "myTeams";
    }

    @PostMapping("/my-teams")
    public String joinTeamsForm(
            @RequestParam("token") String token,
            @Validated JoinTeamForm joinTeamForm,
            BindingResult bindingResult,
            Model model,
            HttpServletResponse httpServletResponse, HttpServletRequest request,
            RedirectAttributes redirectAttributes) throws IOException {

        model.addAttribute("token", token);
        model.addAttribute("httpServletRequest", request);

        User user = userService.getCurrentUser().get();
        Optional<Team> team = teamService.findByToken(token);


        if(team.isEmpty()) {
            redirectAttributes.addFlashAttribute("tokenInvalid", "Token is null");
        }

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("tokenInvalid", "Token is null");
            httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "redirect:/my-teams?page=1";
        }

        if(team.isPresent()) {
            userService.userJoinTeam(user, team.get());
        }
        
        return "redirect:/my-teams?page=1";
    }
}
