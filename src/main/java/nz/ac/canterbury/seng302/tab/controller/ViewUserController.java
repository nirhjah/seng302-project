package nz.ac.canterbury.seng302.tab.controller;

import java.io.IOException;
import java.util.Base64;
import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.tab.helper.FileDataSaver;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletResponse;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.service.UserService;

@Controller
public class ViewUserController {
    Logger logger = LoggerFactory.getLogger(ViewUserController.class);

    @Autowired
    TeamService teamService;

    @Autowired
    UserService userService;

    @Value("${spring.profiles.active}")
    private String profile;

    private final FileDataSaver fileDataSaver = new FileDataSaver("test", profile.replace("/", ""));

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

        Optional<User> user = userService.findUserById(userId);
        String userPicture = null;
        if (user.isEmpty()) { // If empty, throw a 404
            httpServletResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } else {
            userPicture = user.get().getPictureString();
            model.addAttribute("userId", userId);
            model.addAttribute("favSportNames", user.get().getFavouriteSportNames());
        }

        // Thymeleaf has no special support for optionals
        model.addAttribute("thisUser", user);
        model.addAttribute("firstName", user.get().getFirstName());
        model.addAttribute("lastName", user.get().getLastName());
        model.addAttribute("email", user.get().getEmail());
        model.addAttribute("dateOfBirth", user.get().getDateOfBirth());
        model.addAttribute("location", user.get().getLocation());
        model.addAttribute("navTeams", teamService.getTeamList());
        model.addAttribute("httpServletRequest",request);


        Optional<byte[]> fileData = fileDataSaver.readFile(user.get().getUserId());
        fileData.ifPresent(data -> {
            String base64Data = Base64.getEncoder().encodeToString(data);
            model.addAttribute("displayPicture", base64Data);
        });


        var curUser = userService.getCurrentUser();
        boolean canEdit = curUser.filter(value -> value.getUserId() == userId).isPresent();
        // canEdit = whether or not this profile can be edited (i.e. belongs to the User)
        model.addAttribute("canEdit", canEdit);

        return "viewUserTemplate";
    }

    /**
     * This method gets the details of the current user and puts it under the user-info/self tab
     * @param model reprsentation of data for thymeleaf display
     * @param httpServletResponse http response
     * @return thymeleaf template
     */
    @GetMapping("/user-info/self")
    public String getCurrentUser(Model model, HttpServletResponse httpServletResponse)
    {
        Optional<User> user = userService.getCurrentUser();
        if (user.isEmpty())
        {
            return "redirect:/login";
        }
        else {
            User authUser = user.get();
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
    ) throws IOException {
        Optional<User> user = userService.getCurrentUser();
        if (user.isEmpty()) {
            return "redirect:/login";
        }
        User authUser = user.get();
        model.addAttribute("userId", userId);

        // Saving the file in the file system
        fileDataSaver.saveFile(userId, file.getBytes());

        // userService.updatePicture(file, userId);
        return "redirect:/user-info?name=" + authUser.getUserId();
    }
}
