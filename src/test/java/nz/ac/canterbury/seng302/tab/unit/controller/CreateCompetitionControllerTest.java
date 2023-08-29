package nz.ac.canterbury.seng302.tab.unit.controller;

import nz.ac.canterbury.seng302.tab.entity.Grade;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.entity.competition.Competition;
import nz.ac.canterbury.seng302.tab.repository.CompetitionRepository;
import nz.ac.canterbury.seng302.tab.service.CompetitionService;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class CreateCompetitionControllerTest {

    @Autowired
    MockMvc mockMvc;

    @SpyBean
    UserService userService;

    @Autowired
    CompetitionRepository competitionRepository;

    @Autowired
    CompetitionService competitionService;

    User user;

    String VIEW = "/create-competition";

    String TEAMS_ENUM = "teams";

    MockHttpServletRequestBuilder addValues(MockHttpServletRequestBuilder builder, String name, String sport) {
        LocalDateTime start = LocalDateTime.now();
        return builder
                .param("name", name)
                .param("sport", sport)
                .param("startDateTime", start.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .param("endDateTime", start.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .param("userOrTeams", "users")
                .param("userTeamID", String.valueOf(user.getUserId()));
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
    }

    @Test
    @WithMockUser
    void testCreateTeamCompetition() throws Exception {
        MockHttpServletRequestBuilder builder = post(VIEW)
                        .param("usersOrTeams", TEAMS_ENUM);
        String name = "Le Epic Competition";
        String sport = "Running";
        Grade grade = Grade.randomGrade();

        builder = addValues(builder, name, sport);
        builder = addGrade(builder, grade);


        mockMvc.perform(builder);

        Optional<Competition> optionalCompetition = competitionService.findCompetitionById(1L);

        assertTrue(optionalCompetition.isPresent());
        Competition competition = optionalCompetition.get();

        assertNotNull(competition.getCompetitionEndDate());
        assertNotNull(competition.getCompetitionStartDate());

        assertEquals(competition.getName(), name);
        assertEquals(competition.getSport(), sport);
        assertEquals(competition.getGrade(), grade);
    }
}

