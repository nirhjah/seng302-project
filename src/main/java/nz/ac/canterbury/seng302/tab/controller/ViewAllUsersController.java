package nz.ac.canterbury.seng302.tab.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.service.UserService;

@Controller
public class ViewAllUsersController {
    
    @Autowired
    UserService userService;

    private static final int PAGE_SIZE = 10;

    private static final Sort sort = Sort.by(
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
    @GetMapping("/view-users")
    public String viewPageOfUsers(@RequestParam(name="page", defaultValue = "1") int page, @RequestParam(value = "currentSearch", defaultValue = "") String currentSearch, Model model) {
        var userPage = getUserPage(page, currentSearch);
        var userList = userPage.toList();
        model.addAttribute("currentSearch", currentSearch);
        model.addAttribute("page", page);
        model.addAttribute("listOfUsers", userList);
        model.addAttribute("totalPages", userPage.getTotalPages());
        return "viewAllUsers";
    }

    /**
     * Gets page of users matching a search query
     * @param page page number
     * @param nameQuery search query param
     * @return Page of users matching the nameQuery
     */
    private Page<User> getUserPage(int page, String nameQuery) {
        var pageable = PageRequest.of(page-1, PAGE_SIZE, sort);
        if (nameQuery.length() <= 0) {
            if (page <= 0) {     // We want the user to think "Page 1" is the first page, even though Java starts at 0.
                return Page.empty();
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
