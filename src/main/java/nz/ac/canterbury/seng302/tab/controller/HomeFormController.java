package nz.ac.canterbury.seng302.tab.controller;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.helper.FileDataSaver;
import nz.ac.canterbury.seng302.tab.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import nz.ac.canterbury.seng302.tab.service.TeamService;

import java.util.Arrays;
import java.util.Optional;

/**
 * Spring Boot Controller class for the Home Form class.
 */
@Controller
public class HomeFormController implements InitializingBean {
    Logger logger = LoggerFactory.getLogger(HomeFormController.class);

    @Autowired
    private TeamService teamService;

    @Autowired
    private SportService sportService;

    @Autowired
    private UserService userService;

    /**
     * Redirects GET default url '/' to '/home'
     *
     * @return redirect to /home
     */
    @GetMapping("/")
    public String home() {
        logger.info("GET /homeForm");
        return "redirect:./home";
    }

    @Value("${spring.profiles.active}")
    private String profile;

    private FileDataSaver saver;

    @Override
    public void afterPropertiesSet() {
        /*
        Explanation:
        The reason we need this here is because .profile is null when the controller is being constructed.
        We need to wait until everything is fully initialized before the @Value
        annotation works, hence this method here.
         */
        saver = new FileDataSaver(
                "homeFormTest",
                FileDataSaver.getDeploymentType(profile)
        );
    }

    @GetMapping("/save1")
    public String upload1() {
        System.out.println(saver.saveFile(1L, new byte[] {1,2,3,4,5,6,7}));
        return "redirect:./home";
    }

    @GetMapping("/read1")
    public String read() {
        logger.info(Arrays.toString(saver.readFile(1L).get()));
        return "redirect:./home";
    }


    /**
     * Gets the thymeleaf page representing the /home page (a basic welcome screen with nav bar)
     *
     * @param model (map-like) representation of data to be used in thymeleaf display
     * @return thymeleaf homeForm
     */
    @GetMapping("/home")
    public String getTemplate(Model model, HttpServletRequest request) {
        logger.info("GET /homeForm");
        Optional<User> user = userService.getCurrentUser();
        if (user.isPresent()) {
            model.addAttribute("firstName", user.get().getFirstName());
            model.addAttribute("lastName", user.get().getLastName());
            model.addAttribute("displayPicture", user.get().getPictureString());
        }
        model.addAttribute("httpServletRequest", request);
        model.addAttribute("navTeams", teamService.getTeamList());
        return "homeForm";
    }
}

