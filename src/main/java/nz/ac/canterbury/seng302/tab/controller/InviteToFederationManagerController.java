package nz.ac.canterbury.seng302.tab.controller;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class InviteToFederationManagerController {

    @Autowired
    UserService userService;

    int PAGE_SIZE = 10;

    @GetMapping("/inviteToFederationManager")
    public String inviteToFederationManager(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "currentSearch", required = false) String currentSearch,
            Model model, HttpServletRequest request) {
        Page<User> userPage = getUserPage(page, currentSearch);
        List<User> userList = userPage.toList();
        model.addAttribute("listOfUsers", userList);
        model.addAttribute("currentSearch", currentSearch);
        model.addAttribute("page", page);
        model.addAttribute("totalPages", userPage.getTotalPages());
        model.addAttribute("httpServletRequest", request);
        return "inviteToFederationManager";
    }

    /**
     * Gets page of users matching a search query
     *
     * @param page      page number
     * @param nameQuery search query param
     * @return List of users matching the nameQuery
     */
    private Page<User> getUserPage(int page, @Nullable String nameQuery) {
        if (page <= 0) { // We want the user to think "Page 1" is the first page, even though Java starts
            // at 0.
            return Page.empty();
        }
        if (nameQuery == null) {
            nameQuery = "";
        }
        var pageable = PageRequest.of(page - 1, PAGE_SIZE, UserService.SORT_BY_LAST_AND_FIRST_NAME);

        if (nameQuery.isEmpty()) {
            return userService.getPaginatedUsers(pageable);
        } else {
            return userService.findUsersByNameOrSportOrCity(pageable, null, null, nameQuery);
        }
    }
}
