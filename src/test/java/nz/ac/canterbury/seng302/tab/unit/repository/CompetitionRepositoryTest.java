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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
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

    private Date now;
    private long nowSeconds; // Same as `now`, but in seconds since 1970 unix epoch

    private final String SOCCER = "soccer";
    private final String HOCKEY = "hockey";
    private final String BASKETBALL = "basketball";

    // no queries should return more than 100.
    private final PageRequest allRequest = PageRequest.of(0, 100);

    private static LocalDateTime addSeconds(Date date, long seconds) {
        LocalDateTime localDateTime = LocalDateTime.of(date, )
        return date.plusSeconds(seconds);
    }

    private void createWithSport(String sport) {
        for (int i=0; i<NUM_PAST; i++) {
            Competition c1 = new TeamCompetition("Past competition", Grade.randomGrade(), sport);
            c1.setDate(start, end);
            competitionRepository.save(c1);
        }

        for (int i=0; i<NUM_CURRENT; i++) {
            Competition c1 = new TeamCompetition("Current competition", Grade.randomGrade(), sport);
            long start = nowSeconds - 1000;
            long end = nowSeconds - 1000;
            c1.setDate(start, end);
            competitionRepository.save(c1);
        }

        for (int i=0; i<NUM_FUTURE; i++) {
            Competition c1 = new TeamCompetition("Future competition", Grade.randomGrade(), sport);
            c1.setDate(start, end);
            competitionRepository.save(c1);
        }
    }

    @BeforeEach
    public void beforeEach() {
        competitionRepository.deleteAll();
        now = Date.from(Instant.now());
        nowSeconds = now.getTime();

        createWithSport(SOCCER);
        createWithSport(HOCKEY);
        createWithSport(BASKETBALL);
    }

    @Test
    public void testCurrent() {
        // tests current competition querying
        var comps = competitionRepository.findCurrentCompetitionsBySports(allRequest, List.of(SOCCER), nowSeconds).toList();
        Assertions.assertEquals(NUM_CURRENT, comps.size());
    }

    @Test
    public void testPast() {
        // tests past competition querying
        var comps = competitionRepository.findPastCompetitionsBySports(allRequest, List.of(SOCCER), nowSeconds).toList();
        Assertions.assertEquals(NUM_PAST, comps.size());
    }

    @Test
    public void testPastMultiple() {
        // tests past competition querying, across multiple sports.
        var sports = List.of(HOCKEY, BASKETBALL);
        var comps = competitionRepository.findPastCompetitionsBySports(allRequest, sports, nowSeconds).toList();
        var size = NUM_PAST * 2; // x2 to account for both hockey AND bball.
        Assertions.assertEquals(size, comps.size());
    }

    @Test
    public void testCurrentMultiple() {
        // tests current competition querying, across multiple filtered sports.
        var sports = List.of(HOCKEY, BASKETBALL);
        var comps = competitionRepository.findCurrentCompetitionsBySports(allRequest, sports, nowSeconds).toList();
        var size = NUM_CURRENT * 2;
        Assertions.assertEquals(size, comps.size());
    }

    @Test
    public void testFutureMultiple() {
        // tests current competition querying, across multiple filtered sports.
        var sports = List.of(HOCKEY, SOCCER);
        var comps = competitionRepository.findCurrentCompetitionsBySports(allRequest, sports, nowSeconds).toList();
        var size = NUM_CURRENT * 2;
        Assertions.assertEquals(size, comps.size());
    }

    @Test
    public void testAll() {
        var comps = competitionRepository.findAll();
        var size = TOTAL * 3; // num sports is 3.
        Assertions.assertEquals(size, comps.size());
    }

    @Test
    public void testAllPastQuery() {
        // tests past competition querying, across multiple sports.
        var sports = List.of(HOCKEY, BASKETBALL, SOCCER);
        var comps = competitionRepository.findPastCompetitionsBySports(allRequest, sports, nowSeconds).toList();
        var size = NUM_PAST * 3;
        Assertions.assertEquals(size, comps.size());
    }

    @Test
    public void testAllCurrentQuery() {
        var sports = List.of(HOCKEY, BASKETBALL, SOCCER);
        var comps = competitionRepository.findCurrentCompetitionsBySports(allRequest, sports, nowSeconds).toList();
        var size = NUM_CURRENT * 3;
        Assertions.assertEquals(size, comps.size());
    }
}
