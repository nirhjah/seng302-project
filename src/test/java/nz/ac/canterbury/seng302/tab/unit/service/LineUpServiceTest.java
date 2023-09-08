package nz.ac.canterbury.seng302.tab.unit.service;

import nz.ac.canterbury.seng302.tab.entity.*;
import nz.ac.canterbury.seng302.tab.entity.lineUp.LineUp;
import nz.ac.canterbury.seng302.tab.enums.ActivityType;
import nz.ac.canterbury.seng302.tab.repository.ActivityRepository;
import nz.ac.canterbury.seng302.tab.service.LineUpService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@DataJpaTest
@Import(LineUpService.class)
public class LineUpServiceTest {

    @Autowired
    private LineUpService lineUpService;

    private LineUp lineUp;

    private LineUp lineUp2;


    private Formation formation;

    private Formation formation2;


    @Autowired
    private ActivityRepository activityRepository;

    @Test
    void testFindLineUpByActivityAndFormation() throws Exception {
        Team team = new Team("adfds", "hello");
        formation = new Formation("4-5-6", team);

        User u = new User("Test", "Account", "tab.team900@gmail.com", "password",
                new Location("1 Place", "B", "Ilam", "CHCH", "808", "NZ"));
        Activity game = new Activity(ActivityType.Game, team, "A Test Game",
                LocalDateTime.of(2026, 1,1,6,30),
                LocalDateTime.of(2026, 1,1,8,30), u,
                new Location("Jack Erskine", null, "Ilam", "Chch", "Test", "NZ"));
        game.setFormation(formation);
        activityRepository.save(game);
        lineUp = new LineUp(game.getFormation().get(), team, game);
        lineUpService.updateOrAddLineUp(lineUp);
        Assertions.assertEquals(lineUp, lineUpService.findLineUpByActivityAndFormation(game.getId(), formation));
    }


    @Test
    void testGettingLineupsForTeamPerFormation() throws Exception {
        Team team = new Team("adfds", "hello");
        formation = new Formation("4-5-6", team);
        formation2 = new Formation("1-2-3", team);

        User u = new User("Test", "Account", "tab.team900@gmail.com", "password",
                new Location("1 Place", "B", "Ilam", "CHCH", "808", "NZ"));
        Activity game = new Activity(ActivityType.Game, team, "A Test Game",
                LocalDateTime.of(2026, 1,1,6,30),
                LocalDateTime.of(2026, 1,1,8,30), u,
                new Location("Jack Erskine", null, "Ilam", "Chch", "Test", "NZ"));
        game.setFormation(formation);
        activityRepository.save(game);
        lineUp = new LineUp(game.getFormation().get(), team, game);
        lineUpService.updateOrAddLineUp(lineUp);
        game.setFormation(formation2);
        lineUp2 = new LineUp(game.getFormation().get(), team, game);
        lineUpService.updateOrAddLineUp(lineUp2);

        Map<Formation, LineUp> expectedFormationLineUpMap = new HashMap<>();

        expectedFormationLineUpMap.put(formation, lineUp);
        expectedFormationLineUpMap.put(formation2, lineUp2);


        Assertions.assertEquals(expectedFormationLineUpMap, lineUpService.getLineUpsForTeam(team, game));

    }
}

