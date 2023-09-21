package nz.ac.canterbury.seng302.tab.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nz.ac.canterbury.seng302.tab.entity.Team;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.tab.entity.Club;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.service.ClubService;
import nz.ac.canterbury.seng302.tab.service.UserService;

@Controller
public class ViewAllClubsController {
    
    private static final Logger logger = LoggerFactory.getLogger(ViewAllClubsController.class);

    private ClubService clubService;
    private UserService userService;

    private static final int PAGE_SIZE = 10;

    public ViewAllClubsController(ClubService clubService, UserService userService) {
        this.clubService = clubService;
        this.userService = userService;
    }

    @GetMapping("/view-clubs")
    public String viewClubs(
            @RequestParam(name = "nClubs", defaultValue = "10") int nClubs,
            @RequestParam(name = "page", defaultValue = "1") int pageNo,
            @RequestParam(name = "currentSearch", required = false) String currentSearch,
            @RequestParam(name = "sports", required = false) List<String> sports,
            @RequestParam(name = "cities", required = false) List<String> cities,
            Model model,
            HttpServletRequest httpServletRequest) throws IOException {
        logger.info("GET /view-clubs");

        PageRequest pageable = PageRequest.of(pageNo - 1, PAGE_SIZE);
        Page<Club> clubs = Page.empty(); //Replace with q
        int maxPage = clubs.getTotalPages();
        pageNo = Math.max(Math.min(pageNo, maxPage), 1);

        User currentUser = userService.getCurrentUser().orElseThrow();
        model.addAttribute("httpServletRequest", httpServletRequest);

        List<Club> listOfClubs = clubs.getContent();
        List<String> listOfClubLocations = List.of();
        List<String> listOfClubSports = List.of();
        // FOR THE BACKEND PERSON: MODIFY THESE
        model.addAttribute("listOfClubs", listOfClubs);
        // For the search dropdowns
        model.addAttribute("cities", listOfClubLocations);
        model.addAttribute("sports", listOfClubSports);
        // For the paginator
        model.addAttribute("page", clubs);
        model.addAttribute("totalPages", 1);
        model.addAttribute("currentSearch", currentSearch);

        return "viewAllClubs";
    }
}
