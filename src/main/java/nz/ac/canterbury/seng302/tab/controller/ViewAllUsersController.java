package nz.ac.canterbury.seng302.tab.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.data.domain.PageRequest;

import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.service.UserService;

@Controller
public class ViewAllUsersController {
    
    @Autowired
    UserService userService;

    static final int PAGE_SIZE = 10;

    /**
     * Takes user to the view all users page
     * @param page page number
     * @param currentSearch the search query param
     * @param model map representation of information to be passed to thymeleaf page
     * @return view all users page
     */
    @GetMapping("/view-all-users")
    public String viewPageOfUsers(@RequestParam(name="page", defaultValue = "1") int page, @RequestParam(value = "currentSearch", defaultValue = "") String currentSearch, Model model) {
        var userList = getUserList(page, currentSearch);
        model.addAttribute("currentSearch", currentSearch);
        model.addAttribute("page", page);
        model.addAttribute("listOfUsers", userList);
        return "viewAllUsers";
    }

    /**
     * Gets list of users matching a search query
     * @param page page number
     * @param nameQuery search query param
     * @return List of users matching the nameQuery
     */
    private List<User> getUserList(int page, String nameQuery) {
        var pageable = PageRequest.of(page-1, PAGE_SIZE); // add sort by alphabetical here
        if (nameQuery.length() <= 0) {
            if (page <= 0) {     // We want the user to think "Page 1" is the first page, even though Java starts at 0.
                return List.of();
            } else {
                return userService.getPaginatedUsers(pageable);
            }
        }

        if (nameQuery.contains(" ") && nameQuery.split(" ").length > 1) {
            // search via first and last name
            var names = nameQuery.split(" ");
            var firstName = names[0];
            var lastName = names[1];
            return userService.findUsersByName(pageable, firstName, lastName);
        } else {
            return userService.findUsersByName(pageable, nameQuery);
        }
    }
}
