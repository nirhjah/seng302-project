package nz.ac.canterbury.seng302.tab.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import nz.ac.canterbury.seng302.tab.entity.Club;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.service.ClubService;
import nz.ac.canterbury.seng302.tab.service.UserService;

@Controller
public class ViewAllClubs {
    
    private ClubService clubService;
    private UserService userService;

    public ViewAllClubs(ClubService clubService, UserService userService) {
        this.clubService = clubService;
        this.userService = userService;
    }

    // TEST METHOD, DELETE ME BEFORE ONCE BACKEND IS DONE
    private List<Club> generateClubs(int nClubs, User manager) throws IOException {
        Location location = new Location("Dummy", "Funny", "Test", "WhoDoWe", "2468", "Appreciate");
        List<Club> dummyClubs = new ArrayList<>(nClubs);
        for (int i = 0; i < nClubs; i++) {
            Club club = new Club(
                "Club #" + (i+1),
                location,
                "Testing Code",
                manager
            );
            dummyClubs.add(club);
        }

        return dummyClubs;
    }

    @GetMapping("/view-clubs")
    public String viewClubs(
            @RequestParam(name = "nClubs", defaultValue = "10") int nClubs,
            Model model) throws IOException {
        User currentUser = userService.getCurrentUser().orElseThrow();

        // THESE ARE JUST TEST CLUBS, DON'T ACTUALLY DO THIS
        List<Club> testListOfClubs = generateClubs(nClubs, currentUser);

        model.addAttribute("null", testListOfClubs);
        return "viewAllClubs";
    }
}
