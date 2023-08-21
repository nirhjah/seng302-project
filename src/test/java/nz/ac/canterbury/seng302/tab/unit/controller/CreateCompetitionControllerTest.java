package nz.ac.canterbury.seng302.tab.unit.controller;

import nz.ac.canterbury.seng302.tab.entity.Grade;
import nz.ac.canterbury.seng302.tab.entity.competition.Competition;
import nz.ac.canterbury.seng302.tab.repository.CompetitionRepository;
import nz.ac.canterbury.seng302.tab.service.CompetitionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

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

    @Autowired
    CompetitionRepository competitionRepository;

    @Autowired
    CompetitionService competitionService;

    String VIEW = "/create-competition";

    String TEAMS_ENUM = "teams";
    String USERS_ENUM = "users";

    static MockHttpServletRequestBuilder addValues(MockHttpServletRequestBuilder builder, String name, String sport) {
        LocalDateTime start = LocalDateTime.now();
        return builder
                .param("name", name)
                .param("sport", sport)
                .param("startDateTime", start.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .param("endDateTime", start.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }

    static MockHttpServletRequestBuilder addGrade(MockHttpServletRequestBuilder builder, Grade grade) {
        return builder
                .param("age", grade.getAge().toString())
                .param("sex", grade.getSex().toString())
                .param("competitiveness", grade.getCompetitiveness().toString());
    }

    @BeforeEach
    void beforeEach() {
        competitionRepository.deleteAll();
    }

    @Test
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

        assertNotNull(competition.getCompetitionEnd());
        assertNotNull(competition.getCompetitionStart());

        assertEquals(competition.getName(), name);
        assertEquals(competition.getSport(), sport);
        assertEquals(competition.getGrade(), grade);
    }
}

