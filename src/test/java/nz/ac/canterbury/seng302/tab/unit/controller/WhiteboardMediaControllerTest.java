package nz.ac.canterbury.seng302.tab.unit.controller;


import nz.ac.canterbury.seng302.tab.service.image.WhiteboardScreenshotService;
import nz.ac.canterbury.seng302.tab.service.video.WhiteboardRecordingService;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

@SpringBootTest
public class WhiteboardMediaControllerTest {
    /*
    PLAN:
    What do we need to test?

    I guess we just Mockito.spy all the services,
    and intercept calls...?

    ^^^ Angela
     */

    @SpyBean
    WhiteboardRecordingService recordingService;

    @SpyBean
    WhiteboardScreenshotService screenshotService;



    public void testGetRecording() {

    }

}


