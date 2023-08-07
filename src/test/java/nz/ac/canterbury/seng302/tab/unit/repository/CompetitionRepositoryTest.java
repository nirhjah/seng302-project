package nz.ac.canterbury.seng302.tab.unit.repository;

import nz.ac.canterbury.seng302.tab.entity.Grade;
import nz.ac.canterbury.seng302.tab.entity.competition.Competition;
import nz.ac.canterbury.seng302.tab.entity.competition.TeamCompetition;
import nz.ac.canterbury.seng302.tab.entity.competition.UserCompetition;
import nz.ac.canterbury.seng302.tab.repository.CompetitionRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class CompetitionRepositoryTest {

    @Autowired
    private CompetitionRepository competitionRepository;

    String SPORT = "soccer";

    private final int NUM_SOCCER_COMPETITIONS = 6;
    private final int NUM_SOLO_COMPETITIONS = 3;

    @BeforeEach
    public void beforeEach() {
        for (int i=0; i<NUM_SOCCER_COMPETITIONS; i++) {
            Competition c1 = new TeamCompetition("Fed cup", Grade.randomGrade(), SPORT);
            competitionRepository.save(c1);
        }

        for (int i=0; i<NUM_SOLO_COMPETITIONS; i++) {
            Competition c1 = new TeamCompetition("Fed cup", Grade.randomGrade(), SPORT);
            competitionRepository.save(c1);
        }
    }
}
