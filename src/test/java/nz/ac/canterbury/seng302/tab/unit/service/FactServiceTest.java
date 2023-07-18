package nz.ac.canterbury.seng302.tab.unit.service;

import nz.ac.canterbury.seng302.tab.entity.Activity;
import nz.ac.canterbury.seng302.tab.entity.Fact.Substitution;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.enums.ActivityType;
import nz.ac.canterbury.seng302.tab.repository.ActivityRepository;
import nz.ac.canterbury.seng302.tab.repository.FactRepository;
import nz.ac.canterbury.seng302.tab.service.FactService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
@Import(FactService.class)
public class FactServiceTest {

    @Autowired
    FactService factService;

    @Autowired
    FactRepository factRepository;

    @Autowired
    ActivityRepository activityRepository;
    @Test
    public void testActivityLength() throws Exception {
        User u = new User("Test", "Account", "tab.team900@gmail.com", "password",
                new Location("1 Place", "B", "Ilam", "CHCH", "808", "NZ"));
        Activity training = new Activity(ActivityType.Training, null, "A Test Game",
                LocalDateTime.of(2026, 1,1,6,30),
                LocalDateTime.of(2026, 1,1,8,30), u,
                new Location("Jack Erskine", null, "Ilam", "Chch", "Test", "NZ"));
        Assertions.assertEquals(120, factService.getTimePlayed(training));
    }

    @Test
    public void testGettingSubOffTimes() throws Exception {
        User sub = new User("Hee", "Account", "tab@gmail.com", "password",
                new Location("1 Place", "B", "Ilam", "CHCH", "808", "NZ"));
        User player = new User("Test", "Account", "tab.team900@gmail.com", "password",
                new Location("1 Place", "B", "Ilam", "CHCH", "808", "NZ"));
        Activity game = new Activity(ActivityType.Game, null, "A Test Game",
                LocalDateTime.of(2026, 1,1,6,30),
                LocalDateTime.of(2026, 1,1,8,30), player,
                new Location("Jack Erskine", null, "Ilam", "Chch", "Test", "NZ"));
        game.addFactList(List.of(new Substitution(null, "1h 20m", game, player, sub)));
        activityRepository.save(game);

        Assertions.assertEquals(List.of("1h 20m"), factService.getUserSubOffForActivity(player, game));

    }

    @Test
    public void testGettingSubOffTimes_testingSubOnPlayer() throws Exception {
        User sub = new User("Hee", "Account", "tab@gmail.com", "password",
                new Location("1 Place", "B", "Ilam", "CHCH", "808", "NZ"));
        User player = new User("Test", "Account", "tab.team900@gmail.com", "password",
                new Location("1 Place", "B", "Ilam", "CHCH", "808", "NZ"));
        Activity game = new Activity(ActivityType.Game, null, "A Test Game",
                LocalDateTime.of(2026, 1,1,6,30),
                LocalDateTime.of(2026, 1,1,8,30), player,
                new Location("Jack Erskine", null, "Ilam", "Chch", "Test", "NZ"));
        game.addFactList(List.of(new Substitution(null, "1h 20m", game, player, sub)));
        activityRepository.save(game);

        Assertions.assertEquals(List.of(), factService.getUserSubOffForActivity(sub, game));

    }

    @Test
    public void testGettingSubOffTimes_multipleSubsOffForSamePlayer() throws Exception {
        User sub = new User("Hee", "Account", "tab@gmail.com", "password",
                new Location("1 Place", "B", "Ilam", "CHCH", "808", "NZ"));
        User player = new User("Test", "Account", "tab.team900@gmail.com", "password",
                new Location("1 Place", "B", "Ilam", "CHCH", "808", "NZ"));
        Activity game = new Activity(ActivityType.Game, null, "A Test Game",
                LocalDateTime.of(2026, 1,1,6,30),
                LocalDateTime.of(2026, 1,1,8,30), player,
                new Location("Jack Erskine", null, "Ilam", "Chch", "Test", "NZ"));
        game.addFactList(List.of(new Substitution(null, "20m", game, player, sub), new Substitution(null, "1h 20m", game, player, sub)));
        activityRepository.save(game);
        Assertions.assertEquals(List.of("20m", "1h 20m"), factService.getUserSubOffForActivity(player, game));
    }

    @Test
    public void testGettingSubOnTimes() throws Exception {
        User sub = new User("Hee", "Account", "tab@gmail.com", "password",
                new Location("1 Place", "B", "Ilam", "CHCH", "808", "NZ"));
        User player = new User("Test", "Account", "tab.team900@gmail.com", "password",
                new Location("1 Place", "B", "Ilam", "CHCH", "808", "NZ"));
        Activity game = new Activity(ActivityType.Game, null, "A Test Game",
                LocalDateTime.of(2026, 1,1,6,30),
                LocalDateTime.of(2026, 1,1,8,30), player,
                new Location("Jack Erskine", null, "Ilam", "Chch", "Test", "NZ"));
        game.addFactList(List.of(new Substitution(null, "1h 20m", game, player, sub)));
        activityRepository.save(game);

        Assertions.assertEquals(List.of("1h 20m"), factService.getUserSubOnsForActivity(sub, game));

    }

    @Test
    public void testGettingSubOnTimes_testingSubOnPlayer() throws Exception {
        User sub = new User("Hee", "Account", "tab@gmail.com", "password",
                new Location("1 Place", "B", "Ilam", "CHCH", "808", "NZ"));
        User player = new User("Test", "Account", "tab.team900@gmail.com", "password",
                new Location("1 Place", "B", "Ilam", "CHCH", "808", "NZ"));
        Activity game = new Activity(ActivityType.Game, null, "A Test Game",
                LocalDateTime.of(2026, 1,1,6,30),
                LocalDateTime.of(2026, 1,1,8,30), player,
                new Location("Jack Erskine", null, "Ilam", "Chch", "Test", "NZ"));
        game.addFactList(List.of(new Substitution(null, "1h 20m", game, player, sub)));
        activityRepository.save(game);

        Assertions.assertEquals(List.of(), factService.getUserSubOnsForActivity(player, game));

    }

    @Test
    public void testGettingSubOnTimes_multipleSubsOffForSamePlayer() throws Exception {
        User sub = new User("Hee", "Account", "tab@gmail.com", "password",
                new Location("1 Place", "B", "Ilam", "CHCH", "808", "NZ"));
        User player = new User("Test", "Account", "tab.team900@gmail.com", "password",
                new Location("1 Place", "B", "Ilam", "CHCH", "808", "NZ"));
        Activity game = new Activity(ActivityType.Game, null, "A Test Game",
                LocalDateTime.of(2026, 1,1,6,30),
                LocalDateTime.of(2026, 1,1,8,30), player,
                new Location("Jack Erskine", null, "Ilam", "Chch", "Test", "NZ"));
        game.addFactList(List.of(new Substitution(null, "20m", game, player, sub), new Substitution(null, "1h 20m", game, player, sub)));
        activityRepository.save(game);
        Assertions.assertEquals(List.of("20m", "1h 20m"), factService.getUserSubOnsForActivity(sub, game));
    }
}
