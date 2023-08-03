package nz.ac.canterbury.seng302.tab.unit.controller;

import nz.ac.canterbury.seng302.tab.service.ClubImageService;
import nz.ac.canterbury.seng302.tab.service.TeamImageService;
import nz.ac.canterbury.seng302.tab.service.UserImageService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Base64;

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

    @MockBean
    private TeamImageService teamImageService;

    @MockBean
    private ClubImageService clubImageService;

    private final long id=1L;

    // Arbitrary bytes representing an image
    private final byte[] fileBytes = new byte[] {56,65,65,78,54,45,32,54,67,87,11,9};

    @BeforeEach
    void setup() {
        when(userImageService.readFileOrDefault(this.id)).thenReturn(this.fileBytes);
        // The teamImageService seems to call a different method for some reason - not sure why.
        when(teamImageService.readFileOrDefaultB64(this.id)).thenReturn(Base64.getEncoder().encodeToString(this.fileBytes));
        when(clubImageService.readFileOrDefault(this.id)).thenReturn(this.fileBytes);
    }

    @Test
    void testUserProfilePicture() throws Exception {
        mockMvc.perform(get("/user-profile-picture/{id}", this.id))
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
//        mockMvc.perform(get("/user-profile-picture/{id}", id))
//                .andExpect(status().isNotFound());
//    }
    @Test
    void testClubLogo() throws Exception {
        mockMvc.perform(get("/club-logo/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_JPEG))
                .andExpect(content().bytes(this.fileBytes))
                .andReturn();
    }

    @Test
    void testTeamProfilePicture() throws Exception {
        String encodedFileBytes = Base64.getEncoder().encodeToString(this.fileBytes);
        MvcResult result = mockMvc.perform(get("/team-profile-picture/{id}", id))
                .andExpect(status().isOk())
                .andReturn();
        Assertions.assertEquals(result.getResponse().getContentAsString(), encodedFileBytes);
    }
}

