package nz.ac.canterbury.seng302.tab.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Sport;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.service.LocationService;
import nz.ac.canterbury.seng302.tab.service.SportService;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;

@Controller
public class ViewAllUsersController {

    @Autowired
    TeamService teamService;

    @Autowired
    UserService userService;

    @Autowired
    SportService sportService;

    @Autowired
    LocationService locationService;

    private static final int PAGE_SIZE = 10;


    /**
     * Takes user to the view all users page
     *
     * @param page          page number
     * @param currentSearch the search query param
     * @param model         map representation of information to be passed to
     *                      thymeleaf page
     * @return view all users page
     */
    @GetMapping("/view-users")
    public String viewPageOfUsers(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "currentSearch", required = false) String currentSearch,
            @RequestParam(name = "sports", required=false) List<String> sports,
            @RequestParam(name = "cities", required = false) List<String> cities,
            Model model, HttpServletRequest request) {
        Page<User> userPage = getUserPage(page, currentSearch, sports, cities);
        List<User> userList = userPage.toList();

        // get all the cities that populate the dropdown
        List<Location> locations = userService.findLocationBysearch(currentSearch);
        List<String> listOfCities = locations.stream()
                .map(Location::getCity)
                .distinct()
                .sorted()
                .toList();
        model.addAttribute("currentSearch", currentSearch);
        model.addAttribute("page", page);
        model.addAttribute("listOfUsers", userList);
        model.addAttribute("listOfSports", userService.findSportBysearch(currentSearch).stream().map(Sport::getName).toList());
        model.addAttribute("listOfCities", listOfCities);
        model.addAttribute("totalPages", userPage.getTotalPages());
        model.addAttribute("httpServletRequest", request);
        return "viewAllUsers";
    }

    /**
     * Gets page of users matching a search query
     *
     * @param page      page number
     * @param nameQuery search query param
     * @param favSports list of sports, the user should have at least one of these
     *                  as their fav
     * @return List of users matching the nameQuery
     */
    private Page<User> getUserPage(int page, @Nullable String nameQuery, @Nullable List<String> favSports,
            @Nullable List<String> favCities) {
        if (page <= 0) { // We want the user to think "Page 1" is the first page, even though Java starts
                         // at 0.
            return Page.empty();
        }
        if (nameQuery == null) {
            nameQuery = "";
        }
        if (favSports == null) {
            favSports = List.of();
        }
        if (favCities == null) {
            favCities = List.of();
        }
        var pageable = PageRequest.of(page - 1, PAGE_SIZE, UserService.SORT_BY_LAST_AND_FIRST_NAME);

        if (nameQuery.isEmpty() && favSports.isEmpty() && favCities.isEmpty()) {
            return userService.getPaginatedUsers(pageable);
        } else {

            return userService.findUsersByNameOrSportOrCity(pageable, favSports, favCities, nameQuery);
        }
    }
}
