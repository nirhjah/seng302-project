package nz.ac.canterbury.seng302.tab.unit.service;

import nz.ac.canterbury.seng302.tab.entity.Grade;
import nz.ac.canterbury.seng302.tab.entity.Grade.Sex;
import nz.ac.canterbury.seng302.tab.entity.competition.Competition;
import nz.ac.canterbury.seng302.tab.entity.competition.TeamCompetition;
import nz.ac.canterbury.seng302.tab.entity.competition.UserCompetition;
import nz.ac.canterbury.seng302.tab.repository.CompetitionRepository;
import nz.ac.canterbury.seng302.tab.service.CompetitionService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Import(CompetitionService.class)
class CompetitionServiceTest {

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private CompetitionRepository competitionRepository;

    @AfterEach
    public void tearDown() {
        competitionRepository.deleteAll();
    }

    List<Competition> allCompetitions = new ArrayList<>();

    List<Competition> userCompetitions = new ArrayList<>();
    List<Competition> teamCompetitions = new ArrayList<>();

    // How many competitions we generate
    static final int COUNT = 5;

    // We generate 2 types: TeamCompetitions and UserCompetitions. This explains the x2
    static final int TOTAL_COUNT = COUNT * 2;

    static final List<String> SPORTS = List.of(
            "soccer", "hockey", "rugby", "golf", "cricket"
    );

    // If we generate X competitions, spread across Y sports, then
    // the number of competitions per sport is X/Y.
    static final int SPORT_COUNT = COUNT / SPORTS.size();

    PageRequest pageable = PageRequest.of(0, 100);

    public void generateDefault() {
        allCompetitions.clear();
        for (int i=0; i<COUNT; i++) {
            String name = "Test" + i;
            String sport = SPORTS.get(i);
            Competition teamComp = new TeamCompetition(name, new Grade(Sex.OTHER), sport);
            Competition userComp = new UserCompetition(name, new Grade(Sex.OTHER), sport);

            userCompetitions.add(userComp);
            teamCompetitions.add(teamComp);

            competitionService.updateOrAddCompetition(teamComp);
            competitionService.updateOrAddCompetition(userComp);
            allCompetitions.add(teamComp);
            allCompetitions.add(userComp);
        }
    }

    @Test
    void testGettingAllCompetitions() throws Exception {
        generateDefault();
        List<Competition> allCompetitions = competitionService.findAll();
        assertEquals(TOTAL_COUNT, allCompetitions.size());
    }

    @Test
    void testFindingCompetitionById() throws Exception {
        generateDefault();
        Competition competition = new TeamCompetition("Test", new Grade(Sex.OTHER), "football");
        competition = competitionService.updateOrAddCompetition(competition);

        Optional<Competition> foundCompetition = competitionService.findCompetitionById(competition.getCompetitionId());

        Assertions.assertTrue(foundCompetition.isPresent());
        assertEquals(competition.getCompetitionId(), foundCompetition.get().getCompetitionId());
    }

    @Test
    void testGettingAllTeamCompetitions() {
        generateDefault();
        List<Competition> foundCompetitions = competitionService.getAllTeamCompetitions();
        assertEquals(COUNT, foundCompetitions.size());
        for (var teamComp: teamCompetitions) {
            Assertions.assertTrue(allCompetitions.contains(teamComp));
        }
    }

    @Test
    void testGettingAllUserCompetitions() {
        generateDefault();
        List<Competition> foundCompetitions = competitionService.getAllUserCompetitions();
        assertEquals(COUNT, foundCompetitions.size());
        for (var teamComp: teamCompetitions) {
            Assertions.assertTrue(allCompetitions.contains(teamComp));
        }
    }

    private Competition generateCompetition(Instant time, int i) {
        String name = "Test" + i;
        String sport = SPORTS.get(i);
        Competition comp = new TeamCompetition(name, new Grade(Sex.OTHER), sport);
        long timeEpochSecond = time.getEpochSecond();
        long start = timeEpochSecond - 1000;
        long end = timeEpochSecond + 1000;
        comp.setDateAsEpochSecond(start, end);
        return comp;
    }

    /**
     * generates competitions at a specific time
     * @param instant The time to generate competitions for
     */
    private void generateCompetitionsByTime(Instant instant) {
        for (int i=0; i<COUNT; i++) {
            Competition comp = generateCompetition(instant, i);
            competitionService.updateOrAddCompetition(comp);
        }
    }

    private void generatePastFutureCurrent() {
        Instant now = Instant.now();
        long dt = 50000; // change in seconds
        generateCompetitionsByTime(now);

        Instant past = Instant.now().plusSeconds(dt);
        generateCompetitionsByTime(past);

        Instant future = Instant.now().minusSeconds(dt);
        generateCompetitionsByTime(future);
    }

    @Test
    void testFindPastCompetitionBySport() {
        generatePastFutureCurrent();
        List<String> sports = List.of("soccer");
        var comps = competitionService.findPastCompetitionsBySports(pageable, sports);
        assertEquals(SPORT_COUNT, comps.stream().toList().size());
    }

    @Test
    void testFindAll() {
        generateCompetitionsByTime(Instant.now());
        var list = competitionService.findAll();
        assertEquals(COUNT, list.size());
    }

    @Test
    void testFindPastCompetitionByMultipleSports() {
        generatePastFutureCurrent();
        List<String> sports = List.of("rugby", "hockey");
        var comps = competitionService.findPastCompetitionsBySports(pageable, sports);
        var expectedCount = SPORT_COUNT * 2; // times 2, because we filter by 2 sports
        assertEquals(expectedCount, comps.stream().toList().size());
    }

    @Test
    void testFindPastCompetitions() {
        generatePastFutureCurrent();
        var comps = competitionService.findPastCompetitionsBySports(pageable, List.of());
        var comps2 = competitionService.findPastCompetitionsBySports(pageable, null);
        assertEquals(COUNT, comps.stream().toList().size());
        assertEquals(COUNT, comps2.stream().toList().size());
    }

    @Test
    void testFindCurrentCompetitions() {
        generatePastFutureCurrent();
        var comps = competitionService.findCurrentCompetitionsBySports(pageable, List.of());
        var comps2 = competitionService.findCurrentCompetitionsBySports(pageable, null);
        assertEquals(COUNT, comps.stream().toList().size());
        assertEquals(COUNT, comps2.stream().toList().size());
    }

    @Test
    void testFindCurrentCompetitionBySport() {
        generatePastFutureCurrent();
        List<String> sports = List.of("soccer");
        var comps = competitionService.findCurrentCompetitionsBySports(pageable, sports);
        assertEquals(SPORT_COUNT, comps.stream().toList().size());
    }

    @Test
    void testFindCurrentCompetitionByMultipleSports() {
        generatePastFutureCurrent();
        List<String> sports = List.of("rugby", "hockey");
        var comps = competitionService.findCurrentCompetitionsBySports(pageable, sports);
        var expectedCount = SPORT_COUNT * 2; // times 2, because we filter by 2 sports
        assertEquals(expectedCount, comps.stream().toList().size());
    }
}
