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

    // A userId that isn't going to be used.
    // TODO: this is kinda bad
    private static final long unusedUserId = 34849534;

    private final byte[] fileBytes = new byte[] {56,65,65,78,54,45,32,54,67,87,11,9};

    @Test
    public void a() throws Exception {
        MvcResult result1 = mockMvc.perform(get("/user-profile-picture/{id}", userId))
                .andExpect(status().isOk())
                .andReturn();
        System.out.println(result1.getResponse().getHeaderNames());
        result1.getResponse().
    }

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
    public void testUserProfilePictureConsistency() throws Exception {
        MvcResult result1 = mockMvc.perform(get("/user-profile-picture/{id}", userId))
                .andExpect(status().isOk())
                .andReturn();
        byte[] picture1 = result1.getResponse().getContentAsByteArray();

        MvcResult result2 = mockMvc.perform(get("/user-profile-picture/{id}", unusedUserId))
                .andExpect(status().isOk())
                .andReturn();
        byte[] picture2 = result2.getResponse().getContentAsByteArray();

        assertArrayEquals(picture1, picture2);
    }

    /*
    TODO: tests for teams and clubs too here.
     */
}

