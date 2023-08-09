package nz.ac.canterbury.seng302.tab.unit.controller;

import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.repository.TeamRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class ViewAllTeamsControllerTest {
    @Autowired
    TeamRepository teamRepository;
    @Autowired
    MockMvc mockMvc;

    Team team;
    @BeforeEach
    public void beforeAll() throws IOException {
        Location location = new Location("1 Test Lane", "", "Ilam", "Christchurch", "8041", "New Zealand");
        team = new Team("test", "Hockey", location);
        teamRepository.save(team);
    }

    @Test
    public void testGetAllTeamsReturns200() throws Exception {
        mockMvc.perform(get("/view-teams"))
                .andExpect(status().isOk())
                .andExpect(view().name("viewAllTeams"));
    }
    @Test
    public void testGetAllTeamsWhenNoTeamsExist() throws Exception {
        teamRepository.deleteAll();

        mockMvc.perform(get("/view-teams"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));
    }

    @Test
    public void testGetAllTeamsWithSearchQuery() throws Exception {
        mockMvc.perform(get("/view-teams")
                        .param("currentSearch", "test"))
                .andExpect(status().isOk())
                .andExpect(view().name("viewAllTeams"))
                .andExpect(model().attribute("currentSearch", Matchers.is("test")));
    }

    @Test
    public void testGetAllTeamsWithSportsAndCitiesFilters() throws Exception {
        mockMvc.perform(get("/view-teams")
                        .param("sports", "Hockey", "Football")
                        .param("cities", "Christchurch", "Lincoln"))
                .andExpect(status().isOk())
                .andExpect(view().name("viewAllTeams"))
                .andExpect(model().attribute("currentSearch", Matchers.nullValue()));
    }
}
