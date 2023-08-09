package nz.ac.canterbury.seng302.tab.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.image.UserImageService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class ViewUserController {
    Logger logger = LoggerFactory.getLogger(ViewUserController.class);

    @Autowired
    TeamService teamService;

    @Autowired
    UserService userService;

    @Autowired
    UserImageService userImageService;


    /**
     * Gets the thymeleaf page representing the /demo page (a basic welcome screen
     * with some links)
     *
     * @param userId url query parameter of user's id
     * @param model  (map-like) representation of data to be used in thymeleaf
     *               display
     * @return thymeleaf demoTemplate
     */
    @GetMapping("/user-info")
    public String getTemplate(
            @RequestParam(name = "name", required = false, defaultValue = "-1") int userId,
            Model model,
            HttpServletResponse httpServletResponse, HttpServletRequest request) {
        logger.info("GET /user-info");

        Optional<User> userOptional = userService.findUserById(userId);
        model.addAttribute("thisUser", userOptional);

        User user;
        if (userOptional.isEmpty()) { // If empty, throw a 404
            httpServletResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return "viewUserTemplate";
        } else {
            user = userOptional.get();
            model.addAttribute("userId", userId);
            model.addAttribute("favSportNames", user.getFavouriteSportNames());
        }

        // Thymeleaf has no special support for optionals
        model.addAttribute("firstName", user.getFirstName());
        model.addAttribute("lastName", user.getLastName());
        model.addAttribute("email", user.getEmail());
        model.addAttribute("dateOfBirth", user.getDateOfBirth());
        model.addAttribute("location", user.getLocation());
        model.addAttribute("navTeams", teamService.getTeamList());
        model.addAttribute("httpServletRequest",request);
        model.addAttribute("fedmanTokenMessage", (String)model.asMap().get("fedmanTokenMessage"));


        model.addAttribute("displayPicture", userImageService.readFileOrDefaultB64((long) userId));

        var curUser = userService.getCurrentUser();
        boolean canEdit = curUser.filter(value -> value.getUserId() == userId).isPresent();
        // canEdit = whether or not this profile can be edited (i.e. belongs to the User)
        model.addAttribute("canEdit", canEdit);

        return "viewUserForm";
    }

    /**
     * This method gets the details of the current user and puts it under the user-info/self tab
     * @param model reprsentation of data for thymeleaf display
     * @param httpServletResponse http response
     * @return thymeleaf template
     */
    @GetMapping("/user-info/self")
    public String getCurrentUser(Model model, HttpServletResponse httpServletResponse, RedirectAttributes redirectAttributes)
    {
        Optional<User> user = userService.getCurrentUser();
        if (user.isEmpty())
        {
            return "redirect:/login";
        }
        else {
            User authUser = user.get();
            //This redirect is 2 layers deep so have to add it again
            redirectAttributes.addFlashAttribute("fedmanTokenMessage", (String)model.asMap().get("fedmanTokenMessage"));
            return "redirect:/user-info?name=" + authUser.getUserId();
        }

    }

    /**
     * Gets the image file as a multipartfile and checks if it's a .jpg, .svg, or .png and within size limit. If no, an
     * error message is displayed. Else, the file will be saved in the database as a Byte array.
     * @param file uploaded MultipartFile file
     * @param redirectAttributes
     * @param model (map-like) representation of team id
     * @return
     */
    @PostMapping("/user-info/upload-pfp")
    public String uploadPicture(
            @RequestParam(name = "userId", defaultValue = "-1") long userId,
            @RequestParam("file") MultipartFile file,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        Optional<User> user = userService.getCurrentUser();
        if (user.isEmpty()) {
            logger.error("Current user non-existant!");
            return "redirect:/login";
        }
        User authUser = user.get();
        model.addAttribute("userId", userId);

        // Saving the file in the file system
        userImageService.updateProfilePicture(file);

        return "redirect:/user-info?name=" + authUser.getUserId();
    }
}
