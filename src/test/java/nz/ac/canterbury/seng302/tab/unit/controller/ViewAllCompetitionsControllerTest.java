package nz.ac.canterbury.seng302.tab.unit.controller;

import nz.ac.canterbury.seng302.tab.controller.ViewAllCompetitionsController;
import nz.ac.canterbury.seng302.tab.entity.Grade;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.entity.competition.Competition;
import nz.ac.canterbury.seng302.tab.entity.competition.UserCompetition;
import nz.ac.canterbury.seng302.tab.service.CompetitionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class ViewAllCompetitionsControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @SpyBean
    private CompetitionService competitionService;

    private static final String PAST = ViewAllCompetitionsController.Timing.PAST.toString();
    private static final String CURRENT = ViewAllCompetitionsController.Timing.CURRENT.toString();
    private static final String BOTH = PAST + "," + CURRENT;

    @BeforeEach
    public void beforeAll() throws IOException {
        Location location = new Location("94 mays road", "St Albans", "St Ablans", "Chch", "8054", "nznz");

        Set<User> users = Set.of();
        LocalDateTime now = LocalDateTime.now();
        Competition comp1 = new UserCompetition("Test1", new Grade(Grade.Age.UNDER_14S, Grade.Sex.MENS), "football", location, now, now, users);
        competitionService.updateOrAddCompetition(comp1);
    }

    @Test
    public void testViewAllCompetitionsReturns200() throws Exception {
        mockMvc.perform(get("/view-all-competitions"))
                .andExpect(status().isOk())
                .andExpect(view().name("viewAllCompetitions")).andExpect(model().attributeExists("listOfCompetitions"));
    }
    @Test
    public void testViewAllCompetitionsWithParamFindAllReturns200() throws Exception {
        mockMvc.perform(get("/view-all-competitions")
                        .param("page", "2")
                        .param("sports", "Football,Basketball"))
                .andExpect(status().isOk())
                .andExpect(view().name("viewAllCompetitions"))
                .andExpect(model().attributeExists("listOfCompetitions"));

        Mockito.verify(competitionService, Mockito.atLeast(1)).findAllCompetitionsBySports(any(), any());
    }

    @Test
    public void testViewAllCompetitionsWithParamFindCurrentReturns200() throws Exception {
        mockMvc.perform(get("/view-all-competitions")
                        .param("page", "2")
                        .param("sports", "Football,Basketball")
                        .param("times", CURRENT))
                .andExpect(status().isOk())
                .andExpect(view().name("viewAllCompetitions"))
                .andExpect(model().attributeExists("listOfCompetitions"));

        Mockito.verify(competitionService, Mockito.atLeast(1)).findCurrentCompetitionsBySports(any(), any());
    }

    @Test
    public void testViewAllCompetitionsWithParamFindPastReturns200() throws Exception {
        mockMvc.perform(get("/view-all-competitions")
                        .param("page", "2")
                        .param("sports", "Football,Baseball")
                        .param("times", PAST))
                .andExpect(status().isOk())
                .andExpect(view().name("viewAllCompetitions"))
                .andExpect(model().attributeExists("listOfCompetitions"));

        Mockito.verify(competitionService, Mockito.atLeast(1)).findPastCompetitionsBySports(any(), any());
    }

    @Test
    public void testViewAllCompetitionsWithParamFindPastAndCurrentReturns200() throws Exception {
        mockMvc.perform(get("/view-all-competitions")
                        .param("page", "2")
                        .param("sports", "Football,Baseball")
                        .param("times", BOTH))
                .andExpect(status().isOk())
                .andExpect(view().name("viewAllCompetitions"))
                .andExpect(model().attributeExists("listOfCompetitions"));

        Mockito.verify(competitionService, Mockito.atLeast(1)).findAllCompetitionsBySports(any(), any());
    }
}

