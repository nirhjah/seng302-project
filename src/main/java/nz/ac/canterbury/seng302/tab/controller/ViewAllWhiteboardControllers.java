package nz.ac.canterbury.seng302.tab.controller;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Sport;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.WhiteboardScreenshot;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
@Controller
public class ViewAllWhiteboardControllers {

    @GetMapping("/view-whiteboards")
    public String allWhiteboards(@RequestParam(name = "page", defaultValue = "1") int page,
                               @RequestParam(name = "currentSearch", required = false) String currentSearch,
                               @RequestParam(name = "sports", required=false) List<String> sports,
                               Model model, HttpServletRequest request) {

        Team t900 = new Team("Team 900", "Programming", new Location(null, null,
                null, "Chc", null, "nz"));
        List<WhiteboardScreenshot> wbs = List.of(new WhiteboardScreenshot(t900));
        model.addAttribute("currentSearch", currentSearch);
        model.addAttribute("page", page);
        model.addAttribute("listOfWhiteboards", wbs);
        model.addAttribute("listOfSports", List.of("hockey"));
        model.addAttribute("totalPages", 1);
        model.addAttribute("httpServletRequest", request);
        return "viewAllWhiteboards";
    }
}
