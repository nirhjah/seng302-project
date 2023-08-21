package nz.ac.canterbury.seng302.tab.controller;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.tab.entity.competition.Competition;
import nz.ac.canterbury.seng302.tab.service.CompetitionService;
import nz.ac.canterbury.seng302.tab.service.SportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static nz.ac.canterbury.seng302.tab.controller.ViewAllCompetitionsController.Timing.CURRENT;

@Controller
public class ViewAllCompetitionsController {

    private CompetitionService competitionService;

    private SportService sportService;

    @Autowired
    public ViewAllCompetitionsController(CompetitionService competitionService, SportService sportService) {
        this.competitionService = competitionService;
        this.sportService = sportService;
    }

    private static final int PAGE_SIZE = 8;

    public static final Sort SORT = Sort.by(
            Sort.Order.asc("name").ignoreCase()
    );

    public enum Timing {
        PAST, CURRENT
    }
    List<Timing> timingValues = List.of(Timing.values());

    private Page<Competition> getPageResult(int page, List<String> times, List<String> sports) {
        // pages are 0 indexed.
        PageRequest pageable = PageRequest.of(page - 1, PAGE_SIZE, SORT);
        if (times == null) {
            times = List.of();
        }
        if (times.size() == 0 || times.size() == timingValues.size()) {
            return competitionService.findAllCompetitionsBySports(pageable, sports);
        }
        String selectedTime = times.get(0);
        Timing timing = CURRENT;

        for (Timing tim: timingValues) {
            if (selectedTime.equalsIgnoreCase(tim.toString())) {
                timing = tim;
            }
        }
        return switch (timing) {
            case PAST -> competitionService.findPastCompetitionsBySports(pageable, sports);
            case CURRENT -> competitionService.findCurrentCompetitionsBySports(pageable, sports);
        };
    }

    @GetMapping("/view-all-competitions")
    public String viewAllCompetitions(@RequestParam(name = "page", defaultValue = "1") int page,
                                      @RequestParam(name = "sports", required=false) List<String> sports,
                                      @RequestParam(name = "times", required = false) List<String> times,
                                      Model model, HttpServletRequest request) {
        model.addAttribute("httpServletRequest",request);

        page = Math.max(1, page);
        Page<Competition> pageResult = getPageResult(page, times, sports);

        List<Competition> competitions = pageResult.stream().toList();

        model.addAttribute("listOfCompetitions", competitions);

        model.addAttribute("listOfSports", sportService.getAllSportNames());
        model.addAttribute("listOfTimes", List.of("Past", "Current"));

        model.addAttribute("page", page);
        model.addAttribute("totalPages", pageResult.getTotalPages());

        return "viewAllCompetitions";
    }

}
