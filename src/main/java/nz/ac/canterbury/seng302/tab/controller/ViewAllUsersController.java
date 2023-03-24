package nz.ac.canterbury.seng302.tab.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import nz.ac.canterbury.seng302.tab.service.SportService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import nz.ac.canterbury.seng302.tab.entity.Sport;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.service.UserService;

@Controller
public class ViewAllUsersController {
    
    @Autowired
    UserService userService;

    @Autowired
    SportService sportService;

    final Logger logger = LoggerFactory.getLogger(getClass());

    private static final int PAGE_SIZE = 10;

    private static final Sort SORT_BY_LAST_AND_FIRST_NAME = Sort.by(
        new Sort.Order(Sort.Direction.ASC, "lastName"),
        new Sort.Order(Sort.Direction.ASC, "firstName")
    );

    /**
     * Takes user to the view all users page
     * @param page page number
     * @param currentSearch the search query param
     * @param model map representation of information to be passed to thymeleaf page
     * @return view all users page
     */
    @GetMapping("/view-all-users")
    public String viewPageOfUsers(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "currentSearch", defaultValue = "") String currentSearch,
            @RequestParam(name = "filterByHockey", defaultValue = "true") boolean filterByHockey,
            @RequestParam(name = "filterByRugby", defaultValue = "true") boolean filterByRugby,
            Model model) {
        // A TERRIBLE MOCK "FITLER BY SPORTS" THING FOR TESTING
        ArrayList<String> favSports = new ArrayList<>();
        if (filterByHockey) {
            favSports.add("Hockey");
        }
        if (filterByRugby) {
            favSports.add("Rugby");
        }
        var userList = getUserList(page, currentSearch, favSports);
        model.addAttribute("currentSearch", currentSearch);
        model.addAttribute("filterByHockey", filterByHockey);
        model.addAttribute("filterByRugby", filterByRugby);
        model.addAttribute("page", page);
        model.addAttribute("listOfUsers", userList);
        model.addAttribute("listOfSports", sportService.getAllSports());
        return "viewAllUsers";
    }

    /**
     * Gets list of users matching a search query
     * @param page page number
     * @param nameQuery search query param
     * @param favSports list of sports, the user should have at least one of these as their fav
     * @return List of users matching the nameQuery
     */
    private List<User> getUserList(int page, String nameQuery, List<String> favSports) {
        if (page <= 0) {     // We want the user to think "Page 1" is the first page, even though Java starts at 0.
            logger.info("Invalid page no., returning empty list");
            return List.of();
        }
        var pageable = PageRequest.of(page-1, PAGE_SIZE, SORT_BY_LAST_AND_FIRST_NAME);

        if (nameQuery.isEmpty() && favSports.isEmpty()) {
            logger.info("Empty query string AND empty sports list, returning all users...");
            return userService.getPaginatedUsers(pageable);
        } else {
            // TODO: Patch in the 'favourite sports' check once that's working
            logger.info("Query string: {}", nameQuery);
            logger.info("Sports: {}", favSports);
            return userService.findUsersByNameOrSport(pageable, favSports, nameQuery);
        }
    }
}
