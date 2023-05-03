package nz.ac.canterbury.seng302.tab.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nz.ac.canterbury.seng302.tab.entity.Location;
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

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Spring Boot Controller for View Teams Form
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

        // If the user has no teams show a message
        if (teamRepository.findTeamsWithUser_List(currentUser).size() == 0) {
            model.addAttribute("noTeamsFlag", "You are not a member of any teams.");
            return "myTeams";
        }

        model.addAttribute("noTeamsFlag", null);

        // If page number outside of page then reloads page with appropriate number
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
            HttpServletResponse httpServletResponse, HttpServletRequest request
    ) throws IOException {

        model.addAttribute("token", token);
        model.addAttribute("httpServletRequest",request);

        if (bindingResult.hasErrors()) {
            httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            model.addAttribute("submitted_form", null);
            //wsend a flag here in order for the modal to still be showing if there is an error then show error on the modal
            return "myTeams";

        }


        //test code to force team join
        Team team = new Team("test", "Hockey", new Location("1 Test Lane", "", "Ilam", "Christchurch", "8041", "New Zealand"));
        teamService.addTeam(team);
        User user = userService.getCurrentUser().get();
        user.joinTeam(team);
        userService.updateOrAddUser(user);
        

        return "myTeams";
    }




}
