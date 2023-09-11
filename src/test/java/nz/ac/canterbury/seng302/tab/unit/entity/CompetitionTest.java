package nz.ac.canterbury.seng302.tab.unit.entity;


import nz.ac.canterbury.seng302.tab.entity.Grade;
import nz.ac.canterbury.seng302.tab.entity.competition.Competition;
import nz.ac.canterbury.seng302.tab.entity.competition.TeamCompetition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CompetitionTest {

    Competition competition;

    @BeforeEach
    void beforeEach() {
        competition = new TeamCompetition("test", new Grade(Grade.Sex.OTHER), "soccer");
    }

    @Test
    void testDatesSettersGetters() {
        /*
        In competitions,
        Date setters/getters are a bit more complex,
        since they manually parse to/from longs. Best to test em.
         */
        long dt = 10;
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);
        LocalDateTime end = now.plusSeconds(dt);

        long nowEpoch = now.toEpochSecond(ZoneOffset.ofHours(0));
        long endEpoch = nowEpoch + dt;

        competition.setDate(now, end);
        assertEquals(nowEpoch, competition.getCompetitionStart());
        assertEquals(endEpoch, competition.getCompetitionEnd());

        assertEquals(now, competition.getCompetitionStartDate());
        assertEquals(end, competition.getCompetitionEndDate());
    }
}
