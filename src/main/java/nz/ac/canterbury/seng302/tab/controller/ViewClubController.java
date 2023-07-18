package nz.ac.canterbury.seng302.tab.controller;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.tab.entity.Club;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.service.ClubService;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class ViewClubController {

    @Autowired
    private TeamService teamService;

    @Autowired
    private UserService userService;

    @Autowired
    private ClubService clubService;
    Logger logger = LoggerFactory.getLogger(getClass());
    @GetMapping("/view-club")
    public String profileForm(
            Model model,
            @RequestParam(value = "clubID") Long clubId,
            HttpServletRequest request) {

        model.addAttribute("httpServletRequest", request);
        logger.info("GET /view club");

        Optional<User> user = userService.getCurrentUser();
        if (user.isEmpty()) {
            logger.error("No current user?");
            return "redirect:/home";
        }
        currentUserNavDisplay(user.get(), model);
        Optional<Club> club= clubService.findClubById(clubId);
        if (club.isEmpty()){
            return "redirect:/home";
        }
        model.addAttribute("club",club.get());
        return "viewClub";
    }

    public void currentUserNavDisplay(User user, Model model){
        model.addAttribute("firstName", user.getFirstName());
        model.addAttribute("lastName", user.getLastName());
        model.addAttribute("displayPicture", user.getPictureString());
        model.addAttribute("navTeams", teamService.getTeamList());
    }
}
