package nz.ac.canterbury.seng302.tab.unit.repository;

import nz.ac.canterbury.seng302.tab.entity.*;
import nz.ac.canterbury.seng302.tab.entity.lineUp.LineUp;
import nz.ac.canterbury.seng302.tab.entity.lineUp.LineUpPosition;
import nz.ac.canterbury.seng302.tab.enums.ActivityType;
import nz.ac.canterbury.seng302.tab.repository.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
public class LineUpPositionRepositoryTest {

    @Autowired
    private LineUpRepository lineUpRepository;

    @Autowired
    private LineUpPositionRepository lineUpPositionRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private FormationRepository formationRepository;

    private Location location;

    private Team team;

    private User user;

    private Activity activity;

    private LineUp lineUp;

    private Formation formation;

    private LineUpPosition lineUpPosition;

    @BeforeEach
    void beforeAll() throws Exception {
        this.location = new Location(null, null, null, "Christchurch", null, "New Zealand");
        this.team = new Team("testName","sport",this.location);
        this.user = new User("Another", "Test", "test12@test.com", "Password1!",
                new Location(null, null, null, "CHCH", null, "NZ"));
        this.activity = new Activity(ActivityType.Game, this.team, "A random activity",
                LocalDateTime.of(2023, 1,1,6,30),
                LocalDateTime.of(2023, 1,1,8,30), this.user, location);
        this.teamRepository.save(this.team);
        this.userRepository.save(this.user);
        this.activityRepository.save(this.activity);

        formationRepository.deleteAll();

        this.formation = new Formation ("4-2-2", this.team);
        this.lineUp = new LineUp(formation, this.team, this.activity);
        this.lineUpPosition = new LineUpPosition(this.lineUp, this.user, 1);
        this.formationRepository.save(formation);
        this.lineUpRepository.save(lineUp);
    }
    @Test
    public void getLineUpByPositionId() {
        LineUpPosition retreivedLineUpPosition = lineUpPositionRepository.findById(lineUpPosition.getLineUpPositionId()).get();
        Assertions.assertEquals(lineUpPosition, retreivedLineUpPosition);
    }

    @Test
    public void getLineUpPositionsByFormation() {
        List<LineUpPosition> retrievedLineupPositions = lineUpPositionRepository.findLineUpPositionsByLineUpId(formation.getFormationId()).get();
        Assertions.assertEquals(lineUpPosition, retrievedLineupPositions.get(0));
    }

}
