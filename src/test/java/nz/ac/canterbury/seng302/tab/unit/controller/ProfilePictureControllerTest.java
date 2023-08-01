package nz.ac.canterbury.seng302.tab.unit.controller;

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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
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

    // Arbitrary bytes representing an image
    private final byte[] fileBytes = new byte[] {56,65,65,78,54,45,32,54,67,87,11,9};

    @BeforeEach
    void setup() {
        // Generate user and pfp for user
        var u = generator.createRandomUser();
        userId = u.getUserId();
        MockMultipartFile mockMultipartFile = new MockMultipartFile("my_image.jpg", fileBytes);
        // We need to save the user before we save profile picture
        userService.updateOrAddUser(u);
        userImageService.updateProfilePicture(userId, mockMultipartFile);

        // Generate team and pfp for team.
        // remember to add to database!
        // ALSO note: You can only update the pfp if you are a manager/coach of team.
        // TODO.

        // Generate club and club-logo for club
        // remember to add to database, or it wont work!
        // ALSO note: You can only update the pfp if you are a manager of the club
        // TODO.
    }

    @Test
    void testUserProfilePicture() throws Exception {
        MvcResult result1 = mockMvc.perform(get("/user-profile-picture/{id}", userId))
                .andExpect(status().isOk())
                .andReturn();
        // TODO: check that the response is equal to the original fileBytes
    }

    @Test
    void testClubLogo() throws Exception {
        MvcResult result1 = mockMvc.perform(get("/club-logo/{id}", userId))
                .andExpect(status().isOk())
                .andReturn();
        // TODO: check that the response is equal to the original bytes
    }

    @Test
    void testTeamProfilePicture() throws Exception {
        MvcResult result1 = mockMvc.perform(get("/team-profile-picture/{id}", userId))
                .andExpect(status().isOk())
                .andReturn();
        // TODO: check that the response is equal to the original bytes
    }
}

