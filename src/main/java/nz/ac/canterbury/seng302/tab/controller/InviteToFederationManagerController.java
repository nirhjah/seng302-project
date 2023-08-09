package nz.ac.canterbury.seng302.tab.controller;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.tab.entity.FederationManagerInvite;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.mail.EmailService;
import nz.ac.canterbury.seng302.tab.service.FederationService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
public class InviteToFederationManagerController {

    private final  UserService userService;

    private final EmailService emailService;

    private final FederationService federationService;

    int PAGE_SIZE = 10;

    @Autowired
    public InviteToFederationManagerController(UserService userService, EmailService emailService, FederationService
                                                federationService) {
        this.emailService = emailService;
        this.userService = userService;
        this.federationService = federationService;
    }

    /**
     * Returns the page that allows federation managers to invite general users to become federation managers
     * @param page pagination number
     * @param currentSearch string to match first and last name as well as email
     * @param model structure for passing data to HTML
     * @param request httprequest
     * @return inviteToFederationManager page
     */
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
        return "inviteFederationManager";
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
        var pageable = PageRequest.of(page - 1, PAGE_SIZE);
        return userService.getAllUsersNotFedMansByNameAndEmail(pageable, nameQuery);
    }

    /**
     * Post the user id which is used to find the user to invite for federation manager and send email invite to user.
     * @param userId userid of the user to invite for federation manager
     * @param model map representation of information to be passed to thymeleaf page
     * @param request request
     * @return redirect back to the invite federation manager url
     * @throws MessagingException error when sending email
     */
    @PostMapping("/inviteToFederationManager")
    public String inviteToFederationManager(
            @RequestParam(name = "userId", defaultValue = "-1") Long userId,
            Model model, HttpServletRequest request) throws MessagingException {
        model.addAttribute("httpServletRequest", request);

        Optional<User> fedUser = userService.findUserById(userId);
        if (fedUser.isPresent()) {
            Optional<FederationManagerInvite> fedInvite = federationService.findFederationManagerByUser(fedUser.get());
            if (fedInvite.isEmpty()) {
                FederationManagerInvite federationManagerInvite = new FederationManagerInvite(fedUser.get());
                federationService.updateOrSave(federationManagerInvite);
                emailService.federationManagerInvite(fedUser.get(), request, federationManagerInvite.getToken());
            }
            return "redirect:/inviteToFederationManager";
        }
        return "redirect:/inviteToFederationManager";
    }

}
