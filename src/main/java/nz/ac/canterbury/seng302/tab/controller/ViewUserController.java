package nz.ac.canterbury.seng302.tab.controller;

import java.io.IOException;
import java.util.Base64;
import java.util.Optional;

import jakarta.annotation.PostConstruct;
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

    @Value("${spring.profiles.active:unknown}")
    private String profile;

    private FileDataSaver fileDataSaver;

    @PostConstruct
    public void init() {
        /*
        Explanation:
        The reason we need this here is because .profile is null when the controller is being constructed.
        We need to wait until everything is fully initialized before the @Value
        annotation works, hence this method here.
         */
        fileDataSaver = new FileDataSaver(
                FileDataSaver.SaveType.USER_PFP,
                FileDataSaver.getDeploymentType(profile)
        );
    }

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

        Optional<byte[]> fileData = fileDataSaver.readFile(user.getUserId());
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
