package nz.ac.canterbury.seng302.tab;

import nz.ac.canterbury.seng302.tab.controller.ProfileFormController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ProfileFormController.class)
public class ProfileFormControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testProfileFormGet(){

    }

    @Test
    void testUploadPicture(){

    }

    @Test
    void testUploadPictureWithInvalidFileType(){

    }

    @Test
    void testUploadPictureWithoutFile(){

    }
    @Test
    void testUploadPictureOverMaxLimit(){

    }

}
