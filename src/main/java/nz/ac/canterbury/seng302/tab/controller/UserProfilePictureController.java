package nz.ac.canterbury.seng302.tab.controller;

import nz.ac.canterbury.seng302.tab.service.UserImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class UserProfilePictureController {
    private final UserImageService userImageService;

    @Autowired
    public UserProfilePictureController(UserImageService userImageService) {
        this.userImageService = userImageService;
    }

    @GetMapping("/user-profile-picture/{id}")
    public @ResponseBody String getTeamProfilePicture(@PathVariable long id) {
        return userImageService.readFileOrDefaultB64(id);
    }
}

