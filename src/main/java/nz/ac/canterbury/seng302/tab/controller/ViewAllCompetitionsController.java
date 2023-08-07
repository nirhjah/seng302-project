package nz.ac.canterbury.seng302.tab.controller;

import nz.ac.canterbury.seng302.tab.entity.competition.Competition;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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

    @GetMapping("/view-all-competitions")
    public void viewAllCompetitions(Model model, int page) {
        List<Competition> competitions = List.of();

        var pageable = PageRequest.of(page - 1, PAGE_SIZE, SORT);

        model.addAttribute("listOfCompetitions", competitions);
    }
}
