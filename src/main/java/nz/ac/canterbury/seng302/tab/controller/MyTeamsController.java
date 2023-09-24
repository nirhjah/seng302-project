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
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Spring Boot Controller for My Teams Page
 */
@Controller
public class MyTeamsController {

    private static int maxPageSize = 10;

    Logger logger = LoggerFactory.getLogger(ViewAllTeamsController.class);

    private final TeamService teamService;

    private final TeamRepository teamRepository;

    private final UserService userService;

    public MyTeamsController(UserService userService, TeamService teamService, TeamRepository teamRepository) {
        this.userService = userService;
        this.teamRepository = teamRepository;
        this.teamService = teamService;
    }

    /**
     * Gets the page of teams the user has joined
     * @param pageNo  integer corresponding page to be displayed
     * @param model   representation of data for thymeleaf display
     * @param request required for the navbar implementation
     * @return my teams page
     */
    @GetMapping("/my-teams")
    public String myTeamsForm(@RequestParam(value = "page", defaultValue = "-1") int pageNo,
                                Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        model.addAttribute("httpServletRequest",request);
        model.addAttribute("type", "team"); // This means home and teams page can use the same post method to handle teams

        model.addAttribute("joinTeamForm", new JoinTeamForm());

        Optional<User> user = userService.getCurrentUser();
        User currentUser = user.get();

        model.addAttribute("page", pageNo);

        if (model.asMap().containsKey("formBindingResult"))
        {
            model.addAttribute("org.springframework.validation.BindingResult.joinTeamForm",
                    model.asMap().get("formBindingResult"));
        }

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

    /**
     * Posts a form response with the team token for user to join team by
     * @param token             token that identifies team for user to join
     * @param joinTeamForm      join team form that contains the token
     * @param bindingResult     Errors are stored here
     * @param model             model to store model attributes
     * @param httpServletResponse http response
     * @param request             http request
     * @param redirectAttributes  holds redirect attributes for when the page is redirected to a page
     * @return                    my teams page with the newly joined team
     */
    @PostMapping("/my-teams")
    public String joinTeamsForm(
            @RequestParam("token") String token,
            @RequestParam("type") String type,
            @Validated JoinTeamForm joinTeamForm,
            BindingResult bindingResult,
            Model model,
            HttpServletResponse httpServletResponse, HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        model.addAttribute("token", token);
        model.addAttribute("httpServletRequest", request);

        User user = userService.getCurrentUser().get();
        Optional<Team> team = teamService.findByToken(token);

        if(team.isEmpty()) {
            bindingResult.addError(new FieldError("joinTeamForm", "token", "Token is invalid"));
        }

        if (bindingResult.hasErrors()) {
            httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            redirectAttributes.addFlashAttribute("tokenInvalid", "Leave Modal Open");
            redirectAttributes.addFlashAttribute("formBindingResult", bindingResult);
            if (type.equals("team")) {
                return "redirect:/my-teams?page=1";
            } else {
                return "redirect:/home";
            }

        }

        if(team.isPresent()) {
            userService.userJoinTeam(user, team.get());
        }
        if (Objects.equals(type, "team")) {
            return "redirect:/my-teams?page=1";
        } else {
            return "redirect:/home";
        }
    }
}
