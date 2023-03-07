package nz.ac.canterbury.seng302.tab.controller;

import nz.ac.canterbury.seng302.tab.entity.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@WebMvcTest(ProfileFormController.class)
public class ProfileFormControllerTest {
    //TODO currently researching how to use database in testing and converting Multipart file
    private final long TEAM_ID =2;

    @Autowired
    private MockMvc mockMvc;
    @InjectMocks
    private ProfileFormController profileFormController;
    @BeforeEach
    public void setUp(){

    }
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
