package nz.ac.canterbury.seng302.tab.unit.controller;

import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.repository.TeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class WhiteboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    TeamRepository teamRepository;

    private Team team;

    private static final Long TEAM_ID = 1L;


    @BeforeEach
    void beforeAll() throws IOException {
        Location teamLocation = new Location("1 Test Lane", "", "Ilam", "Christchurch", "8041", "New Zealand");
        team = new Team("test", "Hockey", teamLocation);
        teamRepository.save(team);

    }

    @Test
    @WithMockUser
    public void testGettingWhiteboardWithValidTeam() throws Exception {
        mockMvc.perform(get("/whiteboard").param("teamID", TEAM_ID.toString())
        ).andExpect(status().isOk()).andExpect(model().attributeExists("teamFormations"))
                .andExpect(model().attributeExists("teamLineUps"))
                        .andExpect(model().attributeExists("teamMembers"));

        ;
    }

    @Test
    @WithMockUser
    public void testGettingWhiteboardWithInvalidTeam() throws Exception {
        mockMvc.perform(get("/whiteboard").param("teamID", String.valueOf(2))
        ).andExpect(view().name("homeForm"));
    }
}
