package nz.ac.canterbury.seng302.tab.unit.service;

import nz.ac.canterbury.seng302.tab.entity.Grade;
import nz.ac.canterbury.seng302.tab.entity.Grade.Sex;
import nz.ac.canterbury.seng302.tab.entity.competition.Competition;
import nz.ac.canterbury.seng302.tab.entity.competition.TeamCompetition;
import nz.ac.canterbury.seng302.tab.entity.competition.UserCompetition;
import nz.ac.canterbury.seng302.tab.repository.CompetitionRepository;
import nz.ac.canterbury.seng302.tab.service.CompetitionService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootTest
@Import(CompetitionService.class)
class CompetitionServiceTest {
    Logger logger = LoggerFactory.getLogger(CompetitionService.class);

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private CompetitionRepository competitionRepository;

    @AfterEach
    public void tearDown() {
        competitionRepository.deleteAll();
    }

    @Test
    void testGettingAllCompetitions() throws Exception {
        Competition competition1 = new TeamCompetition("Test1", new Grade(Sex.OTHER), "football");
        Competition competition2 = new UserCompetition("Test2", new Grade(Sex.OTHER), "football");
        competitionService.updateOrAddCompetition(competition1);
        competitionService.updateOrAddCompetition(competition2);

        List<Competition> allCompetitions = competitionService.findAll();

        Assertions.assertEquals(2, allCompetitions.size());
        Assertions.assertTrue(allCompetitions.contains(competition1));
        Assertions.assertTrue(allCompetitions.contains(competition2));
    }

    @Test
    void testFindingCompetitionById() throws Exception {
        Competition competition = new TeamCompetition("Test", new Grade(Sex.OTHER), "football");
        competition = competitionService.updateOrAddCompetition(competition);

        Optional<Competition> foundCompetition = competitionService.findCompetitionById(competition.getCompetitionId());

        Assertions.assertTrue(foundCompetition.isPresent());
        Assertions.assertEquals(competition.getCompetitionId(), foundCompetition.get().getCompetitionId());
    }

    @Test
    void testGettingAllTeamCompetitions() throws Exception {
        Competition competition1 = new TeamCompetition("Test1", new Grade(Sex.OTHER), "football");
        Competition competition2 = new UserCompetition("Test2", new Grade(Sex.OTHER), "football");
        competition1 = competitionService.updateOrAddCompetition(competition1);
        competition2 = competitionService.updateOrAddCompetition(competition2);

        List<Competition> expectedCompetitions = new ArrayList<Competition>();
        expectedCompetitions.add(competition1);
        expectedCompetitions.add(competition2);

        List<Competition> foundCompetitions = competitionService.getAllTeamCompetitions();

        Assertions.assertEquals(1, foundCompetitions.size());
        Assertions.assertTrue(expectedCompetitions.contains(competition1));
    }

    @Test
    void testGettingAllUserCompetitions() throws Exception {
        Competition competition1 = new TeamCompetition("Test1", new Grade(Sex.OTHER), "football");
        Competition competition2 = new UserCompetition("Test2", new Grade(Sex.OTHER), "football");
        competition1 = competitionService.updateOrAddCompetition(competition1);
        competition1 = competitionService.updateOrAddCompetition(competition1);
        competition2 = competitionService.updateOrAddCompetition(competition2);

        List<Competition> expectedCompetitions = new ArrayList<Competition>();
        expectedCompetitions.add(competition1);
        expectedCompetitions.add(competition2);

        List<Competition> foundCompetitions = competitionService.getAllUserCompetitions();

        Assertions.assertEquals(1, foundCompetitions.size());
        Assertions.assertTrue(expectedCompetitions.contains(competition2));
    }
}
