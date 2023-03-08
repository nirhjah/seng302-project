package nz.ac.canterbury.seng302.tab.controller;

import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.repository.TeamRepository;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ProfileFormControllerTest {
    //TODO currently researching how to integrate test database

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TeamService teamService;

    @Autowired
    private TeamRepository teamRepository;

    @Test
    @SqlGroup({
            @Sql(value = "classpath:/reset.sql", executionPhase = BEFORE_TEST_METHOD),
            @Sql(value = "classpath:/team-data.sql", executionPhase = BEFORE_TEST_METHOD)
    })
    void testProfileFormGet() throws Exception {
        mockMvc.perform(get("/profile?teamID=1"))
                .andExpect(status().isOk())
                .andExpect(view().name("profileForm"))
                .andExpect(model().attributeExists("displayLocation"));
    }
}
//
//    @Test
//    void testUploadPicture(){
//
//    }
//
//    @Test
//    void testUploadPictureWithInvalidFileType(){
//
//    }
//
//    @Test
//    void testUploadPictureWithoutFile(){
//
//    }
//    @Test
//    void testUploadPictureOverMaxLimit(){
//
//    }
