package nz.ac.canterbury.seng302.tab.controller;

import nz.ac.canterbury.seng302.tab.entity.competition.Competition;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class ViewAllCompetitionsController {

    @GetMapping("/view-all-competitions")
    public void viewAllCompetitions(Model model) {
        List<Competition> competitions = List.of();
        model.addAttribute("listOfCompetitions", competitions);
    }
}
