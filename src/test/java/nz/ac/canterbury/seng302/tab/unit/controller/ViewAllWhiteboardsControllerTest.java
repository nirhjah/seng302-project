package nz.ac.canterbury.seng302.tab.unit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.WhiteBoardRecording;
import nz.ac.canterbury.seng302.tab.service.video.WhiteboardRecordingService;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser
public class ViewAllWhiteboardsControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private WhiteboardRecordingService mockWBRecService;

    private static final int PAGE_SIZE = 10;
    
    private List<WhiteBoardRecording> generateRecordings() throws Exception {
        Team team = new Team("TestName", "TestSport");
        ArrayList<WhiteBoardRecording> items = new ArrayList<>(PAGE_SIZE);

        for (int i=0; i < PAGE_SIZE; i++) {
            items.add(new WhiteBoardRecording("Evil Name", team));
        }

        return items;
    }

    @Test
    void viewAllWhiteboards_noParams_returnsAll() throws Exception {
        Pageable page = PageRequest.of(0, PAGE_SIZE);
        List<WhiteBoardRecording> items = generateRecordings();
        when(mockWBRecService.findPublicPaginatedWhiteboardsBySports(any(), isNull(), isNull())).thenReturn(new PageImpl<>(items, page, PAGE_SIZE));


        mockMvc.perform(get("/view-whiteboards"))
                .andExpect(status().isOk())
                .andExpect(view().name("viewAllWhiteboards"));

        verify(mockWBRecService).findPublicPaginatedWhiteboardsBySports(any(), isNull(), isNull());
    }


}
