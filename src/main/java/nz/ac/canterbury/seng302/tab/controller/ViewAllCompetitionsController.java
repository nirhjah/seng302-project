package nz.ac.canterbury.seng302.tab.controller;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.tab.entity.Grade;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.entity.competition.Competition;
import nz.ac.canterbury.seng302.tab.entity.competition.UserCompetition;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import nz.ac.canterbury.seng302.tab.entity.competition.TeamCompetition;
import nz.ac.canterbury.seng302.tab.service.CompetitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Locale;
import java.util.Set;

@Controller
public class ViewAllCompetitionsController {

    @Autowired
    private CompetitionService competitionService;

    private static int PAGE_SIZE = 6;

    public static final Sort SORT = Sort.by(
            Sort.Order.asc("startDate").ignoreCase(),
            Sort.Order.asc("firstName").ignoreCase()
    );

    @GetMapping("/view-all-competitions")
    public String viewAllCompetitions( @RequestParam(name = "page", defaultValue = "1") int page,Model model, HttpServletRequest request) {
        model.addAttribute("httpServletRequest",request);
        PageRequest pageable = PageRequest.of(page - 1, PAGE_SIZE, SORT);

        Location location = new Location("94 mays road", "St Albans", "St Ablans", "Chch", "8054", "nznz");

        Set<User> users = Set.of();
        Competition comp1 = new UserCompetition("Test1", new Grade(Grade.Sex.OTHER), "football", location, users);
        Competition comp2= new UserCompetition("Test1", new Grade(Grade.Sex.OTHER), "football", location, users);
        Competition comp3 = new UserCompetition("Test1", new Grade(Grade.Sex.OTHER), "football", location, users);

        competitionService.updateOrAddCompetition(comp1);
        competitionService.updateOrAddCompetition(comp2);
        competitionService.updateOrAddCompetition(comp3);

        List<Competition> competitions = competitionService.findAll();

        model.addAttribute("listOfCompetitions", competitions);

        model.addAttribute("listOfSports", List.of());
        model.addAttribute("listOfCities", List.of());

        return "viewAllCompetitions";
    }

}
