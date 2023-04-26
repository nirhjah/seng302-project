package nz.ac.canterbury.seng302.tab.controller;

import java.util.List;
import java.util.Optional;

import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import nz.ac.canterbury.seng302.tab.entity.Sport;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.service.LocationService;
import nz.ac.canterbury.seng302.tab.service.SportService;
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
    UserRepository userRepository;

    @Autowired
    LocationService locationService;

    final Logger logger = LoggerFactory.getLogger(getClass());

    private static final int PAGE_SIZE = 10;

    private static final Sort SORT_BY_LAST_AND_FIRST_NAME = Sort.by(
            new Sort.Order(Sort.Direction.ASC, "lastName"),
            new Sort.Order(Sort.Direction.ASC, "firstName"));

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
            Model model, HttpServletRequest request,@RequestParam(name = "sports", required = false) List<String> sports,
            @RequestParam(name = "cities", required = false) List<String> cities) {
        Page<User> userPage = getUserPage(page, currentSearch, sports,cities);
        List<User> userList = userPage.toList();
        Optional<User> user = userService.getCurrentUser();
        model.addAttribute("firstName", user.get().getFirstName());
        model.addAttribute("lastName", user.get().getLastName());
        model.addAttribute("displayPicture", user.get().getPictureString());


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
        model.addAttribute("listOfSports", userService.findSportBysearch(currentSearch).stream().map(Sport::getName).toList()); //nirhjah
        model.addAttribute("listOfCities", listOfCities);
        model.addAttribute("totalPages", userPage.getTotalPages());
        model.addAttribute("navTeams", teamService.getTeamList());
        model.addAttribute("httpServletRequest",request);
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
            logger.info("Invalid page no., returning empty list");
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
        var pageable = PageRequest.of(page - 1, PAGE_SIZE, SORT_BY_LAST_AND_FIRST_NAME);

        if (nameQuery.isEmpty() && favSports.isEmpty() && favCities.isEmpty()) {
            logger.info("Empty query string, empty sports list AND empty city list, returning all users...");
            return userService.getPaginatedUsers(pageable);
        } else {
            logger.info("Query string: {}", nameQuery);
            logger.info("Sports: {}", favSports);
            logger.info("cities: {}", favCities);
            return userService.findUsersByNameOrSportOrCity(pageable, favSports, favCities, nameQuery);
        }
    }
}
