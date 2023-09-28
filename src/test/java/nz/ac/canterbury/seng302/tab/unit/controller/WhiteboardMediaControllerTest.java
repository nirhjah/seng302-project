package nz.ac.canterbury.seng302.tab.unit.controller;

import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import nz.ac.canterbury.seng302.tab.service.image.WhiteboardScreenshotService;
import nz.ac.canterbury.seng302.tab.service.video.WhiteboardRecordingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class WhiteboardMediaControllerTest {
    @SpyBean
    private WhiteboardRecordingService whiteboardRecordingService;

    @SpyBean
    private WhiteboardScreenshotService whiteboardScreenshotService;

    @SpyBean
    private UserService userService;

    @SpyBean
    private TeamService teamService;

    @Autowired
    private MockMvc mockMvc;
    private ResponseEntity<byte[]> videoTest = ResponseEntity
            .ok(new byte[] {8, 4, 3,5});

    private ResponseEntity<byte[]> thumbnailtest = ResponseEntity
            .ok(new byte[] {11, 2, 1,2});

    private ResponseEntity<byte[]> screenshotTest = ResponseEntity
            .ok(new byte[] {1, 2, 3,4});

    @Test
    public void testGetRecording() throws Exception {
        when(whiteboardRecordingService.getRecording(anyLong())).thenReturn(videoTest);
        mockMvc.perform(get("/whiteboard-media/video/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM))
                .andExpect(content().bytes(videoTest.getBody()));
    }

    @Test
    public void testGetThumbnail() throws Exception {
        when(whiteboardScreenshotService.getScreenshot(anyLong())).thenReturn(thumbnailtest);
        mockMvc.perform(get("/whiteboard-media/thumbnail/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM))
                .andExpect(content().bytes(thumbnailtest.getBody()));

    }

    @Test
    public void testGetScreenShot() throws Exception {
        when(whiteboardScreenshotService.getScreenshot(anyLong())).thenReturn(screenshotTest);
        mockMvc.perform(get("/whiteboard-media/screenshot/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM))
                .andExpect(content().bytes(screenshotTest.getBody()));
    }

    @Test
    public void testPostScreenshot() throws Exception {
        MockMultipartFile file = new MockMultipartFile("screenshot-input", "screenshot.png", MediaType.IMAGE_PNG_VALUE, "content".getBytes());
        Location testLocation = new Location(null, null, null, "chch", null, "nz");
        User mockUser = new User("John", "Doe", new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime(),
                "johndoe@example.com", "Password123!", testLocation);
        when(userService.getCurrentUser()).thenReturn(Optional.of(mockUser));

        Team mockTeam = new Team("test3", "Rugby", testLocation);
        teamService.addTeam(mockTeam);
        when(teamService.getTeam(anyLong())).thenReturn(mockTeam);
        mockMvc.perform(multipart("/whiteboard-media/save/screenshot")
                                .file(file)
                                .param("teamId", "1")
                                .param("screenshot-name", "ScreenshotName")
                                .param("isPublic", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/whiteboard?teamID="+ mockTeam.getId()));
    }

    @Test
    public void testPostVideoRecording() throws Exception {
        MockMultipartFile file = new MockMultipartFile("recording-input", "recording.mp4", MediaType.APPLICATION_OCTET_STREAM_VALUE, "content".getBytes());
        Location testLocation = new Location(null, null, null, "chch", null, "nz");
        User mockUser = new User("John", "Doe", new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime(),
                "johndoe@example.com", "Password123!", testLocation);
        when(userService.getCurrentUser()).thenReturn(Optional.of(mockUser));
        Team mockTeam = new Team("test3", "Rugby", testLocation);
        teamService.addTeam(mockTeam);
        when(teamService.getTeam(anyLong())).thenReturn(mockTeam);
        mockMvc.perform( multipart("/whiteboard-media/save/video")
                                .file(file)
                                .param("teamIdForRecording", "1")
                                .param("recording-name", "RecordingName")
                                .param("isPublic", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/whiteboard?teamID="+mockTeam.getId()));
    }

}


