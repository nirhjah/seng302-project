package nz.ac.canterbury.seng302.tab.unit.controller;


import nz.ac.canterbury.seng302.tab.service.image.WhiteboardScreenshotService;
import nz.ac.canterbury.seng302.tab.service.video.WhiteboardRecordingService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class WhiteboardMediaControllerTest {
    /*
    PLAN:
    What do we need to test?

    I guess we just Mockito.spy all the services,
    and intercept calls...?

    ^^^ Angela
     */

    private ResponseEntity<byte[]> dummy = ResponseEntity
            .ok(new byte[] {8, 4, 3,5});

    @SpyBean
    WhiteboardRecordingService recordingService;

    @SpyBean
    WhiteboardScreenshotService screenshotService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetRecording() throws Exception {
        Mockito.when(recordingService.getRecording(any())).thenReturn(dummy);
        mockMvc.perform(get("whiteboard-media/video/1"))
                .andExpect(status().isOk());
    }

    /**
      TODO:

     do the same for all the other endpoints
     */

}


