package nz.ac.canterbury.seng302.tab.unit.service;

import nz.ac.canterbury.seng302.tab.entity.*;
import nz.ac.canterbury.seng302.tab.entity.lineUp.LineUp;
import nz.ac.canterbury.seng302.tab.entity.lineUp.LineUpPosition;
import nz.ac.canterbury.seng302.tab.enums.ActivityType;
import nz.ac.canterbury.seng302.tab.repository.*;
import nz.ac.canterbury.seng302.tab.service.LineUpPositionService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@DataJpaTest
@Import(LineUpPositionService.class)
public class LineUpPositionServiceTest {

    @Autowired
    private LineUpPositionService lineUpPositionService;

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
        this.team = new Team("testName", "sport", this.location);
        this.user = new User("Another", "Test", "test12@test.com", "Password1!",
                new Location(null, null, null, "CHCH", null, "NZ"));
        this.activity = new Activity(ActivityType.Game, this.team, "A random activity",
                LocalDateTime.of(2023, 1, 1, 6, 30),
                LocalDateTime.of(2023, 1, 1, 8, 30), this.user, location);
        this.teamRepository.save(this.team);
        this.userRepository.save(this.user);
        this.activityRepository.save(this.activity);

        formationRepository.deleteAll();

        this.formation = new Formation("4-2-2", this.team);
        this.lineUp = new LineUp(formation, this.team, this.activity);
        this.lineUpPosition = new LineUpPosition(this.lineUp, this.user, 1);
        this.formationRepository.save(formation);
        this.lineUpRepository.save(lineUp);
        this.lineUpPositionRepository.save(lineUpPosition);
    }

    @Test
    public void testFindLineUpPositionsByLineUp() {
        Optional<List<LineUpPosition>> actualLineUpPosition = lineUpPositionService.findLineUpPositionsByLineUp(lineUp.getLineUpId());
        Assertions.assertTrue(actualLineUpPosition.isPresent());
        Assertions.assertEquals(lineUpPosition, actualLineUpPosition.get().get(0));
    }

    @Test
    public void testAddLineUpPosition() {
        LineUpPosition newLineUpPosition = new LineUpPosition(this.lineUp, this.user, 1);
        lineUpPositionService.addLineUpPosition(newLineUpPosition);
        Optional<List<LineUpPosition>> actualLineUpPosition = lineUpPositionService.findLineUpPositionsByLineUp(lineUp.getLineUpId());
        Assertions.assertTrue(actualLineUpPosition.isPresent());
        Assertions.assertEquals(lineUpPosition, actualLineUpPosition.get().get(0));
    }
}

