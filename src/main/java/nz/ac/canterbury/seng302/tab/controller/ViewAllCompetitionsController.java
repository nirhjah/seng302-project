package nz.ac.canterbury.seng302.tab.controller;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.tab.entity.Grade;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.entity.competition.Competition;
import nz.ac.canterbury.seng302.tab.entity.competition.TeamCompetition;
import nz.ac.canterbury.seng302.tab.entity.competition.UserCompetition;
import nz.ac.canterbury.seng302.tab.service.SportService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import nz.ac.canterbury.seng302.tab.service.CompetitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Instant;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Controller
public class ViewAllCompetitionsController {

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private SportService sportService;

    private static final int PAGE_SIZE = 8;

    public static final Sort SORT = Sort.by(
            Sort.Order.asc("name").ignoreCase()
    );


    private boolean hasBeenTestPopulated = false;

    private void testModel() {
        if (hasBeenTestPopulated) {
            return;
        }

        hasBeenTestPopulated = true;
        Location location = new Location("94 mays road", "St Albans", "St Ablans", "Chch", "8054", "nznz");
        Set<User> users = Set.of();
        var sports = sportService.getAllSports();
        var r = new Random();
        var now = Instant.now().getEpochSecond();

        for (int i=0; i<50; i++) {
            var sport = sports.get(r.nextInt(sports.size()-1));
            Competition comp1 = new UserCompetition("Test1", new Grade(Grade.Age.UNDER_14S, Grade.Sex.MENS), sport.getName(), location, users);
            Competition comp2= new TeamCompetition("Test1", new Grade(Grade.Sex.OTHER), sport.getName(), location);

            // just for testing
            if (i < 20) {
                if (i < 10) {
                    // set to future
                    comp1.setDate(now + 10000, now + 20000);
                    comp2.setDate(now + 10000, now + 20000);
                } else {
                    // set to past
                    comp1.setDate(now - 20000, now - 18000);
                    comp2.setDate(now - 22000, now - 20000);
                }
            } else {
                comp1.setDate(now - 100, now + 4000);
                comp2.setDate(now - 100, now + 4000);
            }

            competitionService.updateOrAddCompetition(comp1);
            competitionService.updateOrAddCompetition(comp2);
        }
    }

    public enum Timing {
        ALL, PAST, CURRENT
    }
    List<Timing> timingValues = List.of(Timing.values());

    private Page<Competition> getPageResult(int page, String time, List<String> sports) {
        // pages are 0 indexed.
        PageRequest pageable = PageRequest.of(page - 1, PAGE_SIZE, SORT);

        Timing timing = Timing.ALL;
        for (Timing tim: timingValues) {
            if (time.equalsIgnoreCase(tim.toString())) {
                timing = tim;
            }
        }

        return switch (timing) {
            case PAST -> competitionService.findPastCompetitionsBySports(pageable, sports);
            case CURRENT -> competitionService.findCurrentCompetitionsBySports(pageable, sports);
            default -> competitionService.findAllCompetitionsBySports(pageable, sports);
        };
    }

    @GetMapping("/view-all-competitions")
    public String viewAllCompetitions(@RequestParam(name = "page", defaultValue = "1") int page,
                                      @RequestParam(name = "sports", required=false) List<String> sports,
                                      @RequestParam(name = "time", required = false, defaultValue = "ALL") String time,
                                      Model model, HttpServletRequest request) {

        testModel();
        model.addAttribute("httpServletRequest",request);

        Page<Competition> pageResult = getPageResult(page, time, sports);

        List<Competition> competitions = pageResult.stream().toList();

        model.addAttribute("listOfCompetitions", competitions);

        model.addAttribute("listOfSports", sportService.getAllSportNames());
        model.addAttribute("listOfTimes", List.of("All", "Past", "Current"));

        model.addAttribute("page", page);
        model.addAttribute("totalPages", pageResult.getTotalPages());

        System.out.println("SIZE: " + pageResult.getTotalPages());

        return "viewAllCompetitions";
    }

}
