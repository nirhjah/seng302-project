package nz.ac.canterbury.seng302.tab.unit.repository;

import nz.ac.canterbury.seng302.tab.entity.Grade;
import nz.ac.canterbury.seng302.tab.entity.competition.Competition;
import nz.ac.canterbury.seng302.tab.entity.competition.TeamCompetition;
import nz.ac.canterbury.seng302.tab.repository.CompetitionRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;

import java.time.Instant;
import java.util.List;


@DataJpaTest
public class CompetitionRepositoryTest {

    @Autowired
    private CompetitionRepository competitionRepository;

    String SPORT = "soccer";

    private final int NUM_PAST = 6;
    private final int NUM_FUTURE = 4;
    private final int NUM_CURRENT = 5;

    private final int TOTAL = NUM_PAST + NUM_CURRENT + NUM_FUTURE;

    private final int SECONDS_PER_DAY = 86400;

    private long now;

    private final String SOCCER = "soccer";
    private final String HOCKEY = "hockey";
    private final String BASKETBALL = "basketball";

    // no queries should return more than 100.
    private final PageRequest allRequest = PageRequest.of(0, 100);

    private void createWithSport(String sport) {
        for (int i=0; i<NUM_PAST; i++) {
            Competition c1 = new TeamCompetition("Past competition", Grade.randomGrade(), sport);
            c1.setDate(now - SECONDS_PER_DAY, now - 1);
            competitionRepository.save(c1);
        }

        for (int i=0; i<NUM_CURRENT; i++) {
            Competition c1 = new TeamCompetition("Current competition", Grade.randomGrade(), sport);
            c1.setDate(now - SECONDS_PER_DAY, now + SECONDS_PER_DAY);
            competitionRepository.save(c1);
        }

        for (int i=0; i<NUM_FUTURE; i++) {
            Competition c1 = new TeamCompetition("Future competition", Grade.randomGrade(), sport);
            c1.setDate(now + SECONDS_PER_DAY, now + SECONDS_PER_DAY * 2);
            competitionRepository.save(c1);
        }
    }

    @BeforeEach
    public void beforeEach() {
        now = Instant.EPOCH.getEpochSecond();

        createWithSport(SOCCER);
        createWithSport(HOCKEY);
        createWithSport(BASKETBALL);
    }

    @Test
    public void testFuture() {
        // tests future competition querying
        var comps = competitionRepository.findUpcomingCompetitionsBySports(allRequest, List.of(SOCCER), now).toList();
        Assertions.assertEquals(comps.size(), NUM_FUTURE);
    }

    @Test
    public void testCurrent() {
        // tests current competition querying
        var comps = competitionRepository.findCurrentCompetitionsBySports(allRequest, List.of(SOCCER), now).toList();
        Assertions.assertEquals(comps.size(), NUM_CURRENT);
    }

    @Test
    public void testPast() {
        // tests past competition querying
        var comps = competitionRepository.findPastCompetitionsBySports(allRequest, List.of(SOCCER), now).toList();
        Assertions.assertEquals(comps.size(), NUM_PAST);
    }

    @Test
    public void testPastMultiple() {
        // tests past competition querying, across multiple sports.
        var sports = List.of(HOCKEY, BASKETBALL);
        var comps = competitionRepository.findPastCompetitionsBySports(allRequest, sports, now).toList();
        var size = NUM_PAST * 2; // x2 to account for both hockey AND bball.
        Assertions.assertEquals(comps.size(), size);
    }

    @Test
    public void testCurrentMultiple() {
        // tests current competition querying, across multiple filtered sports.
        var sports = List.of(HOCKEY, BASKETBALL);
        var comps = competitionRepository.findCurrentCompetitionsBySports(allRequest, sports, now).toList();
        var size = NUM_CURRENT * 2;
        Assertions.assertEquals(comps.size(), size);
    }

    @Test
    public void testFutureMultiple() {
        // tests current competition querying, across multiple filtered sports.
        var sports = List.of(HOCKEY, SOCCER);
        var comps = competitionRepository.findCurrentCompetitionsBySports(allRequest, sports, now).toList();
        var size = NUM_CURRENT * 2;
        Assertions.assertEquals(comps.size(), size);
    }

    @Test
    public void testAll() {
        var comps = competitionRepository.findAll();
        var size = TOTAL * 3; // num sports is 3.
        Assertions.assertEquals(comps.size(), size);
    }

    @Test
    public void testAllPastQuery() {
        // tests past competition querying, across multiple sports.
        var sports = List.of(HOCKEY, BASKETBALL, SOCCER);
        var comps = competitionRepository.findPastCompetitionsBySports(allRequest, sports, now).toList();
        var size = NUM_PAST * 3;
        Assertions.assertEquals(comps.size(), size);
    }


    @Test
    public void testAllCurrentQuery() {
        var sports = List.of(HOCKEY, BASKETBALL, SOCCER);
        var comps = competitionRepository.findCurrentCompetitionsBySports(allRequest, sports, now).toList();
        var size = NUM_CURRENT * 3;
        Assertions.assertEquals(comps.size(), size);
    }

    @Test
    public void testFutureQuery() {
        // tests past competition querying, across multiple sports.
        var sports = List.of(HOCKEY, BASKETBALL, SOCCER);
        var comps = competitionRepository.findUpcomingCompetitionsBySports(allRequest, sports, now).toList();
        var size = NUM_FUTURE * 3;
        Assertions.assertEquals(comps.size(), size);
    }
}
