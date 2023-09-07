package nz.ac.canterbury.seng302.tab.unit.controller;

import nz.ac.canterbury.seng302.tab.entity.Grade;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.entity.competition.Competition;
import nz.ac.canterbury.seng302.tab.entity.competition.TeamCompetition;
import nz.ac.canterbury.seng302.tab.entity.competition.UserCompetition;
import nz.ac.canterbury.seng302.tab.repository.CompetitionRepository;
import nz.ac.canterbury.seng302.tab.service.CompetitionService;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;


@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class CreateCompetitionControllerTest {

    @Autowired
    MockMvc mockMvc;

    @SpyBean
    UserService userService;

    @Autowired
    CompetitionRepository competitionRepository;

    @Autowired
    TeamService teamService;

    @Autowired
    CompetitionService competitionService;

    User user;
    Team team;
    Location location = new Location("50 Road Street", "", "Ilam", "Christchurch", "8052", "New Zealand");

    String VIEW = "/create-competition";

    String TEAMS_ENUM = "teams";
    String USERS_ENUM = "users";

    MockHttpServletRequestBuilder addValues(MockHttpServletRequestBuilder builder, String name, String sport, LocalDateTime time) {
        if (time == null) {
            time = LocalDateTime.now();
        }

        return builder
                .param("name", name)
                .param("sport", sport)
                .param("startDateTime", time.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .param("endDateTime", time.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }

    MockHttpServletRequestBuilder addUserValues(MockHttpServletRequestBuilder builder) {
        return builder.param("usersOrTeams", USERS_ENUM)
                .param("userTeamID", String.valueOf(user.getUserId()));
                // ^^^ The "list" of users who are participating.
                // For now, we just do the user
    }

    MockHttpServletRequestBuilder addTeamValues(MockHttpServletRequestBuilder builder, Collection<Team> teams) {
        String teamList = String.join(",", teams.stream().map(team -> team.getTeamId().toString()).toList());
        return builder.param("usersOrTeams", TEAMS_ENUM)
                .param("userTeamID", teamList);
        // ^^^ The "list" of users who are participating
    }

    MockHttpServletRequestBuilder addGrade(MockHttpServletRequestBuilder builder, Grade grade) {
        return builder
                .param("age", grade.getAge().toString())
                .param("sex", grade.getSex().toString())
                .param("competitiveness", grade.getCompetitiveness().toString());
    }

    @BeforeEach
    void beforeEach() throws IOException {
        user = User.defaultDummyUser();
        Optional<User> optUser = Optional.of(user);
        Mockito.when(userService.getCurrentUser()).thenReturn(optUser);
        competitionRepository.deleteAll();
        team = new Team("test1", "Hockey", location);
        teamService.addTeam(team);
    }


    @Test
    @WithMockUser
    void testCreateTeamCompetition() throws Exception {
        MockHttpServletRequestBuilder builder = post(VIEW);

        String name = "Le Epic Competition";
        String sport = "Running";
        Grade grade = Grade.randomGrade();

        LocalDateTime time = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);
        builder = addValues(builder, name, sport, time);
        builder = addGrade(builder, grade);
        builder = addTeamValues(builder, List.of(team));

        mockMvc.perform(builder);

        Competition competition = competitionRepository.findAll().get(0);

        assertEquals(competition.getClass(), TeamCompetition.class);

        assertEquals(competition.getCompetitionEndDate(), time);
        assertEquals(competition.getCompetitionStartDate(), time);
        assertEquals(competition.getName(), name);
        assertEquals(competition.getSport(), sport);
        assertEquals(competition.getGrade(), grade);
    }

    Competition createAndTestUserCompetition() {
        MockHttpServletRequestBuilder builder = post(VIEW);

        String name = "Comp2";
        String sport = "Running";
        Grade grade = Grade.randomGrade();

        LocalDateTime time = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);
        builder = addValues(builder, name, sport, time);
        builder = addGrade(builder, grade);
        builder = addUserValues(builder);

        try {
            mockMvc.perform(builder);
        } catch (Exception e) {
            fail("error thrown");
        }

        Competition competition = competitionRepository.findAll().get(0);

        assertEquals(competition.getClass(), UserCompetition.class);

        assertEquals(competition.getCompetitionEndDate(), time);
        assertEquals(competition.getCompetitionStartDate(), time);
        assertEquals(competition.getName(), name);
        assertEquals(competition.getSport(), sport);
        assertEquals(competition.getGrade(), grade);
        return competition;
    }

    @Test
    @WithMockUser
    void testCreateUserCompetition() {
        createAndTestUserCompetition();
    }

    @Test
    @WithMockUser
    void testBasicEditCompetition() throws Exception {
        Competition competition = createAndTestUserCompetition();
        int nComp = competitionService.getAllUserCompetitions().size();
        String id = String.valueOf(competition.getCompetitionId());

        MockHttpServletRequestBuilder builder = post(VIEW)
                .param("competitionID", id);

        addUserValues(builder);
        addGrade(builder, Grade.randomGrade());
        addValues(builder, "basicComp", "soccer", LocalDateTime.now());

        mockMvc.perform(builder)
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name("redirect:/view-competition?competitionID=" + id));

        int nCompAfter = competitionService.getAllUserCompetitions().size();
        // Ensure that we don't have duplicate competitions
        assertEquals(nCompAfter, nComp);
    }

    @Test
    @WithMockUser
    void testEditCompetitionValues() throws Exception {
        Competition competition = createAndTestUserCompetition();
        int nComp = competitionService.getAllUserCompetitions().size();
        String id = String.valueOf(competition.getCompetitionId());

        MockHttpServletRequestBuilder builder = post(VIEW)
                .param("competitionID", id);

        String name = "edited name 1";
        String sport = "Hockey";
        LocalDateTime time = LocalDateTime.now();
        Grade grade = Grade.randomGrade();
        addValues(builder, name, sport, time);
        addGrade(builder, grade);
        addUserValues(builder);

        mockMvc.perform(builder)
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name("redirect:/view-competition?competitionID=" + id));

        int nCompAfter = competitionService.getAllUserCompetitions().size();
        // Ensure that we don't have duplicate competitions
        assertEquals(nCompAfter, nComp);
    }

}

