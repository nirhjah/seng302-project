package nz.ac.canterbury.seng302.tab.unit.controller;

import nz.ac.canterbury.seng302.tab.entity.*;
import nz.ac.canterbury.seng302.tab.entity.competition.TeamCompetition;
import nz.ac.canterbury.seng302.tab.entity.competition.UserCompetition;
import nz.ac.canterbury.seng302.tab.repository.CompetitionRepository;
import nz.ac.canterbury.seng302.tab.repository.TeamRepository;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import nz.ac.canterbury.seng302.tab.service.CompetitionService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser
public class CreateCompetitionControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private TeamRepository teamRepository;

    @MockBean
    private UserService mockUserService;

    @MockBean
    private CompetitionService mockCompetitionService;

    private TeamCompetition teamCompetition;

    private UserCompetition userCompetition;

    private Team team;

    private User user;


    @BeforeEach
    public void beforeAll() throws IOException {
        Location testLocation = new Location(null, null, null, "Chch",
                null, "NZ");
        user = new User("John", "Doe",
                new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime(), "johndoe@example.com", "Password123!", testLocation);
        team = new Team("Team 900", "Rugby");
        userRepository.save(user);
        teamRepository.save(team);

        teamCompetition = new TeamCompetition("Rugby Competition", new Grade(Grade.Age.UNDER_18S, Grade.Sex.MENS), "Rugby", new Location("5 Test Lane", "", "", "Christchurch", "8042", "New Zealand"), team);
        userCompetition = new UserCompetition("Rugby Competition", new Grade(Grade.Age.UNDER_18S, Grade.Sex.MENS), "Rugby", new Location("5 Test Lane", "", "", "Christchurch", "8042", "New Zealand"), user);

        competitionRepository.save(teamCompetition);
        competitionRepository.save(userCompetition);

        Mockito.when(mockUserService.getCurrentUser()).thenReturn(Optional.of(user));

        Mockito.when(mockCompetitionService.findCompetitionById(teamCompetition.getCompetitionId())).thenReturn(Optional.of(teamCompetition));
        Mockito.when(mockCompetitionService.findCompetitionById(userCompetition.getCompetitionId())).thenReturn(Optional.of(userCompetition));
    }

    @AfterEach
    public void afterEach() {
        competitionRepository.deleteAll();
        userRepository.deleteAll();
        teamRepository.deleteAll();
    }

    @WithMockUser(username = "johndoe@example.com", password = "Password123!", roles = "USER")
    @Test
    void testGettingCreateCompetitionPage() throws Exception {
        mockMvc.perform(get("/create-competition"))
                .andExpect(status().isOk())
                .andExpect(view().name("createCompetition"));
    }

    @WithMockUser(username = "johndoe@example.com", password = "Password123!", roles = "USER")
    @Test
    void testGettingUpdateCompetitionPageOfValidTeamCompetition() throws Exception {
        mockMvc.perform(get("/create-competition?edit={id}", teamCompetition.getCompetitionId()))
                .andExpect(status().isOk())
                .andExpect(view().name("createCompetition"));
    }

    @WithMockUser(username = "johndoe@example.com", password = "Password123!", roles = "USER")
    @Test
    void testGettingUpdateCompetitionPageOfValidUserCompetition() throws Exception {
        mockMvc.perform(get("/create-competition?edit={id}", userCompetition.getCompetitionId()))
                .andExpect(status().isOk())
                .andExpect(view().name("createCompetition"));
    }


}