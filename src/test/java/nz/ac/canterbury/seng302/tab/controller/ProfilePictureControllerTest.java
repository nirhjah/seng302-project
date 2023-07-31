package nz.ac.canterbury.seng302.tab.controller;

import nz.ac.canterbury.seng302.tab.helper.GenerateRandomUsers;
import nz.ac.canterbury.seng302.tab.service.UserImageService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;

import static org.springframework.test.util.AssertionErrors.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class ProfilePictureControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserImageService userImageService;

    @Autowired
    private UserService userService;

    private final GenerateRandomUsers generator = new GenerateRandomUsers();

    private long userId;

    private final byte[] fileBytes = new byte[] {56,65,65,78,54,45,32,54,67,87,11,9};

    @BeforeEach
    public void setup() {
        // Generate user and pfp for user
        var u = generator.createRandomUser();
        userId = u.getUserId();
        MockMultipartFile mockMultipartFile = new MockMultipartFile("my_image.jpg", fileBytes);
        userService.updateOrAddUser(u);
        userImageService.updateProfilePicture(userId, mockMultipartFile);

        // Generate team and pfp for team
        // TODO.

        // Generate club and pfp for club
        // TODO.
    }

    @Test
    public void testUserProfilePicture() throws Exception {
        MvcResult result = mockMvc.perform(get("/user-profile-picture/{id}", userId))
                .andExpect(status().isOk())
                .andReturn();
        byte[] picture = result.getResponse().getContentAsByteArray();
        assertTrue("Picture not equal!", Arrays.equals(picture, fileBytes));
    }

    /*
    TODO: tests for teams and clubs too here.
     */
}

