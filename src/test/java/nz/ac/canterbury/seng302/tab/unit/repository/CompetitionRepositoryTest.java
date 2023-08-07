package nz.ac.canterbury.seng302.tab.unit.repository;

import nz.ac.canterbury.seng302.tab.entity.Grade;
import nz.ac.canterbury.seng302.tab.entity.competition.Competition;
import nz.ac.canterbury.seng302.tab.entity.competition.TeamCompetition;
import nz.ac.canterbury.seng302.tab.repository.CompetitionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.Instant;

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

    @BeforeEach
    public void beforeEach() {
        long now = Instant.EPOCH.getEpochSecond();

        for (int i=0; i<NUM_PAST; i++) {
            Competition c1 = new TeamCompetition("Past competition", Grade.randomGrade(), SPORT);
            c1.setDate(now - SECONDS_PER_DAY, now - 1);
            competitionRepository.save(c1);
        }

        for (int i=0; i<NUM_CURRENT; i++) {
            Competition c1 = new TeamCompetition("Current competition", Grade.randomGrade(), SPORT);
            c1.setDate(now - SECONDS_PER_DAY, now + SECONDS_PER_DAY);
            competitionRepository.save(c1);
        }

        for (int i=0; i<NUM_FUTURE; i++) {
            Competition c1 = new TeamCompetition("Future competition", Grade.randomGrade(), SPORT);
            c1.setDate(now + SECONDS_PER_DAY, now + SECONDS_PER_DAY * 2);
            competitionRepository.save(c1);
        }
    }

}
