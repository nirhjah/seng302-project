package nz.ac.canterbury.seng302.tab.unit.controller;

import nz.ac.canterbury.seng302.tab.service.UserImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class ProfilePictureControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserImageService userImageService;
    private long userId=1L;

    // Arbitrary bytes representing an image
    private final byte[] fileBytes = new byte[] {56,65,65,78,54,45,32,54,67,87,11,9};

    @BeforeEach
    void setup() {
        when(userImageService.readFileOrDefault(this.userId)).thenReturn(this.fileBytes);

        // Generate club and club-logo for club
        // remember to add to database, or it wont work!
        // ALSO note: You can only update the pfp if you are a manager of the club
        // TODO.
    }

    @Test
    void testUserProfilePicture() throws Exception {
        mockMvc.perform(get("/user-profile-picture/{id}", this.userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_JPEG))
                .andExpect(content().bytes(this.fileBytes));
    }

    //Why is there no validation check on controller? no 404 checks?
//    @Test
//    public void testGetUserProfilePictureForNotFound() throws Exception {
//        long userId = 2L;
//
//        when(userImageService.readFileOrDefault(userId)).thenReturn(null);
//        mockMvc.perform(get("/user-profile-picture/{id}", userId))
//                .andExpect(status().isNotFound());
//    }
    @Test
    void testClubLogo() throws Exception {
        MvcResult result1 = mockMvc.perform(get("/club-logo/{id}", userId))
                .andExpect(status().isOk())
                .andReturn();
        // TODO: check that the response is equal to the original bytes
    }

    @Test
    void testTeamProfilePicture() throws Exception {
        mockMvc.perform(get("/team-profile-picture/{id}", userId))
                .andExpect(status().isOk())
                .andReturn();
        // TODO: check that the response is equal to the original bytes
    }
}

