package nz.ac.canterbury.seng302.tab.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.tab.authentication.AutoLogin;
import nz.ac.canterbury.seng302.tab.entity.FederationManagerInvite;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.enums.AuthorityType;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import nz.ac.canterbury.seng302.tab.service.FederationService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class FederationManagerInviteController {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    FederationService federationService;

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    private AutoLogin autoLogin;

    FederationManagerInvite fedInvite;

    /**
     * TEMPORARY ENDPOINT
     * @param request
     * @return
     */
    @GetMapping("/invite")
    public String fedToUser(HttpServletRequest request) {
        User u = userService.getCurrentUser().get();
        userService.inviteToFederationManger(u, request);
        logger.info("sent");
        return "redirect:user-info/self";
    }

    /**
     * Controller handles processing the token and takes user to the page where they can accept or decline the invitation.
     * @param token the users unique token for becoming a federation manager
     * @param request the HTTPRrequest
     * @param model storage structure
     * @param redirectAttributes used to display messages on redirection
     * @return
     */
    @GetMapping("/federationManager")
    public String fedManagerInvitation(@RequestParam("token") String token, HttpServletRequest request, Model model,
                                       RedirectAttributes redirectAttributes) {
        model.addAttribute("httpServletRequest", request);
        fedInvite = federationService.getByToken(token);
        User u =  userService.getCurrentUser().get();
        if (fedInvite != null && fedInvite.getUser().equals(u)) {
            return "federationManagerInvite";
        } else {
            redirectAttributes.addFlashAttribute("fedmanTokenMessage", "Error: Invalid Federation Manager Token");
            return "redirect:user-info/self";
        }
    }

    @PostMapping("/federationManager")
    public String federationManager(@RequestParam(name = "decision") String decision, HttpServletRequest request,
                                    RedirectAttributes redirectAttributes) {
        boolean choice = Boolean.parseBoolean(decision);
        if (choice) {
            User user = userService.getCurrentUser().get();
            user.grantAuthority(AuthorityType.FEDERATION_MANAGER);
            userRepository.save(user);
            try {
                request.logout();
            } catch (ServletException e) {
                throw new RuntimeException(e);
            }
            autoLogin.forceLogin(user.getEmail(), user.getAuthorities(), request);
            redirectAttributes.addFlashAttribute("fedmanTokenMessage", "Sucess! You are now a federation manager");
            logger.info("FED MANAGER NOW");
        } else {
            logger.info("NOT FED MANAGER");
        }
        federationService.delete(fedInvite);
        return "redirect:user-info/self";
    }
}
