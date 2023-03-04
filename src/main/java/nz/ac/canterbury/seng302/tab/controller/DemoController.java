package nz.ac.canterbury.seng302.tab.controller;

import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

/**
 * This is a basic spring boot controller, note the @link{Controller} annotation which defines this.
 * This controller defines endpoints as functions with specific HTTP mappings
 */
@Controller
public class DemoController {
    @Autowired
    private TeamService teamService;
    public static String uploadDirectory=System.getProperty("user.dir")+"/resources/image";
    Logger logger = LoggerFactory.getLogger(DemoController.class);
    /**
     * Redirects GET default url '/' to '/demo'
     * @return redirect to /demo
     */
    @GetMapping("/")
    public String home() {
        logger.info("GET /");

        return "redirect:./demo";
    }

    /**
     * Gets the thymeleaf page representing the /demo page (a basic welcome screen with some links)
     * @param name url query parameter of user's name
     * @param model (map-like) representation of data to be used in thymeleaf display
     * @return thymeleaf demoTemplate
     */
    @GetMapping("/demo")
    public String getTemplate(@RequestParam(name = "name", required = false, defaultValue = "World") String name, Model model) throws IOException {
        logger.info("GET /demo");
        model.addAttribute("name", name);
        //Testing image being encoded as a byteArray and being decoded and displayed. Currently working.
        Resource resource = new ClassPathResource("/static/image/default-profile.png");
        File file = resource.getFile();
        String fileEncoded= Base64.getEncoder().encodeToString(Files.readAllBytes(file.toPath()));

        Team team = new Team("test","test","test",fileEncoded);
        teamService.addTeam(team);


        return "demoTemplate";
    }
    @PostMapping("/demo")
    public String saveProduct(@RequestParam("file") MultipartFile file, Model model)
    {
        if (file.isEmpty()){
            model.addAttribute("emptyFileError", true);
            return "demoTemplate";
        }

        if (!isSupportedContentType(file.getContentType())){
            model.addAttribute("typeError", true);
            return "demoTemplate";
        }
        if (file.getSize()>1000000){
            model.addAttribute("sizeError",true);
            System.out.println(file.getSize()>0);
            return "demoTemplate";
        }
        System.out.println(file.getContentType());
        teamService.updatePicture(file, 1);
        return "demoTemplate";
    }

    private boolean isSupportedContentType(String contentType){
        return contentType.equals("image/png")|| contentType.equals("image/jpg")||contentType.equals("image/svg");
    }
}
