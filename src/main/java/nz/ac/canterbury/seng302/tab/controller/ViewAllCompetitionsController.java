package nz.ac.canterbury.seng302.tab.controller;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.tab.entity.Grade;
import nz.ac.canterbury.seng302.tab.entity.competition.Competition;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import nz.ac.canterbury.seng302.tab.entity.competition.TeamCompetition;
import nz.ac.canterbury.seng302.tab.service.CompetitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.awt.print.Pageable;
import java.util.List;

@Controller
public class ViewAllCompetitionsController {

    private static int PAGE_SIZE = 6;

    public static final Sort SORT = Sort.by(
            Sort.Order.asc("startDate").ignoreCase(),
            Sort.Order.asc("firstName").ignoreCase()
    );

    @Autowired
    private CompetitionService competitionService;

    @GetMapping("/view-all-competitions")
    public void viewAllCompetitions(Model model, int page) {
        List<Competition> competitions = List.of();

        var pageable = PageRequest.of(page - 1, PAGE_SIZE, SORT);

    public String viewAllCompetitions(Model model, HttpServletRequest request) {
        model.addAttribute("httpServletRequest",request);
        Competition comp1 = new TeamCompetition("Test1", new Grade(Grade.Sex.OTHER), "football");
        Competition comp2= new TeamCompetition("Test1", new Grade(Grade.Sex.OTHER), "football");
        Competition comp3 = new TeamCompetition("Test1", new Grade(Grade.Sex.OTHER), "football");

        competitionService.updateOrAddCompetition(comp1);
        competitionService.updateOrAddCompetition(comp2);
        competitionService.updateOrAddCompetition(comp3);

        List<Competition> competitions = competitionService.findAll();
        model.addAttribute("listOfCompetitions", competitions);

        return "viewAllCompetition";
    }

}
