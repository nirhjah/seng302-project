package nz.ac.canterbury.seng302.tab.controller;

import jakarta.servlet.http.HttpServletResponse;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Optional;

@Controller
public class ViewUserController {
    Logger logger = LoggerFactory.getLogger(ViewUserController.class);

    public static long userId;
    @Autowired
    UserService userService;

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
            HttpServletResponse httpServletResponse) {
        logger.info("GET /user-info");

        ViewUserController.userId = userId;
        Optional<User> user = userService.findUserById(userId);
        if (user.isEmpty()) { // If empty, throw a 404
            httpServletResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }

        // if the optional user is present then we want to get the pfp else we set the pfp to null
        String teamPicture = user.map(User::getPictureString).orElse(null);
        // Thymeleaf has no special support for optionals
        model.addAttribute("thisUser", user);
        model.addAttribute("displayPicture", teamPicture);
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
    @PostMapping("/user-info")
//    public RedirectView uploadPicture(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes, Model model)
    public String uploadPicture(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes, Model model)
    {
        Optional<User> user = userService.getCurrentUser();
        if (user.isEmpty())
        {
            return "redirect:/login";
        }
        User authUser = user.get();
        model.addAttribute("teamID", userId);
        if (!isSupportedContentType(file.getContentType())){
            redirectAttributes.addFlashAttribute("typeError", true);
            return "redirect:/user-info?name=" + authUser.getUserId();
        }
        else if (file.getSize()>10000000){
            redirectAttributes.addFlashAttribute("sizeError", true);
            return "redirect:/user-info?name=" + authUser.getUserId();
        }
        userService.updatePicture(file,userId );
        return "redirect:/user-info?name=" + authUser.getUserId();
    }

    /**
     * @param contentType The picture file type in string, e.g image/jpg, image/svg+xml etc
     * @return Boolean type if the contentType parameter matches either the image/png, image/jpg, image/svg+xml or image/jpeg string
     */
    private boolean isSupportedContentType(String contentType){
        return contentType.equals("image/png")|| contentType.equals("image/jpg")||contentType.equals("image/svg+xml")|| contentType.equals("image/jpeg");
    }

}
