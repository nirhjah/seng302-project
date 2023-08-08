package nz.ac.canterbury.seng302.tab.unit.service;

import nz.ac.canterbury.seng302.tab.entity.Activity;
import nz.ac.canterbury.seng302.tab.entity.Fact.Fact;
import nz.ac.canterbury.seng302.tab.entity.Fact.Goal;
import nz.ac.canterbury.seng302.tab.entity.Fact.Substitution;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
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
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        game.addFactList(List.of(new Substitution(null, game, player, sub, 1)));
        activityRepository.save(game);

        Assertions.assertEquals(List.of(LocalTime.of(1, 20)), factService.getUserSubOffForActivity(player, game));

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
        game.addFactList(List.of(new Substitution(null, game, player, sub, 1)));
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
        game.addFactList(List.of(new Substitution(null, game, player, sub, 1), new Substitution(null,game, player, sub, 2)));
        activityRepository.save(game);
        Assertions.assertEquals(List.of(1 , 2), factService.getUserSubOffForActivity(player, game));
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
        game.addFactList(List.of(new Substitution(null, game, player, sub, 1)));
        activityRepository.save(game);

        Assertions.assertEquals(List.of(1), factService.getUserSubOnsForActivity(sub, game));

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
        game.addFactList(List.of(new Substitution(null, game, player, sub, 1)));
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
        game.addFactList(List.of(new Substitution(null, game, player, sub, 1), new Substitution(null,game, player, sub, 2)));
        activityRepository.save(game);
        Assertions.assertEquals(List.of(1 ,2), factService.getUserSubOnsForActivity(sub, game));
    }

    @Test
    public void topScorerTest() throws Exception {
        Location location = new Location(null, null, null, "Christchurch", null,
                "New Zealand");
        Team team = new Team("Team 900", "Programming");
        User creator = new User("Test", "Account", "test123@test.com", "Password1!", location);
        User player = new User("Another", "Test", "test1234@test.com", "Password1!",
                new Location(null, null, null, "CHCH", null, "NZ"));
        Activity activity = new Activity(ActivityType.Game, team, "Game with Team",
                LocalDateTime.of(2023, 1,1,6,30),
                LocalDateTime.of(2023, 1,1,8,30),
                creator,  new Location(null, null, null,
                "Christchurch", null, "New Zealand"));
        activityRepository.save(activity);

        List<Fact> factList = new ArrayList<>();
        factList.add(new Fact("Someone fell over",activity, 1));
        Goal goal = new Goal("Goal was scored", activity, player, 1, 1);
        factRepository.save(goal);
        factList.add(goal);
        factList.add(new Substitution("Player was taken off", activity, creator, player, 2));
        activity.addFactList(factList);
        activityRepository.save(activity);

        Assertions.assertEquals(List.of(Map.of(player, 1L)), (factService.getTop5Scorers(team)));
    }


    @Test
    public void topScorerTest_MultipleScorers() throws Exception {
        Location location = new Location(null, null, null, "Christchurch", null,
                "New Zealand");
        Team team = new Team("Team 900", "Programming");
        User creator = new User("Test", "Account", "test123@test.com", "Password1!", location);
        User player = new User("Another", "Test", "test1234@test.com", "Password1!",
                new Location(null, null, null, "CHCH", null, "NZ"));
        Activity activity = new Activity(ActivityType.Game, team, "Game with Team",
                LocalDateTime.of(2023, 1,1,6,30),
                LocalDateTime.of(2023, 1,1,8,30),
                creator,  new Location(null, null, null,
                "Christchurch", null, "New Zealand"));
        activityRepository.save(activity);

        List<Fact> factList = new ArrayList<>();
        factList.add(new Fact("Someone fell over", activity, 1));
        Goal goal = new Goal("Goal was scored",activity, player, 1, 1);
        Goal goal1 = new Goal("A Goal was scored again",activity, creator, 1, 1);
        Goal goal2 = new Goal("A Goal",activity, creator, 1, 1);
        factRepository.save(goal1);
        factRepository.save(goal2);
        factRepository.save(goal);
        factList.add(goal);
        factList.add(goal1);
        factList.add(goal2);
        factList.add(new Substitution("Player was taken off", activity, creator, player, 1));
        activity.addFactList(factList);
        activityRepository.save(activity);

        Assertions.assertEquals(List.of(Map.of(creator, 2L), Map.of(player, 1L)), (factService.getTop5Scorers(team)));
    }

    @Test
    public void topScorerTest_MultipleScorers_MoreThan5() throws Exception {
        Location location = new Location(null, null, null, "Christchurch", null,
                "New Zealand");
        Team team = new Team("Team 900", "Programming");
        User creator = new User("Test", "Account", "test123@test.com", "Password1!", location);
        User player = new User("Another", "Test", "test1234@test.com", "Password1!",
                new Location(null, null, null, "CHCH", null, "NZ"));
        User player1 = new User("Another", "Test", "test12345@test.com", "Password1!",
                new Location(null, null, null, "CHCH", null, "NZ"));
        User player2 = new User("Another", "Test", "test123456@test.com", "Password1!",
                new Location(null, null, null, "CHCH", null, "NZ"));
        User player3 = new User("Another", "Test", "test1234567@test.com", "Password1!",
                new Location(null, null, null, "CHCH", null, "NZ"));
        User player4 = new User("Another", "Test", "test12348@test.com", "Password1!",
                new Location(null, null, null, "CHCH", null, "NZ"));
        Activity activity = new Activity(ActivityType.Game, team, "Game with Team",
                LocalDateTime.of(2023, 1,1,6,30),
                LocalDateTime.of(2023, 1,1,8,30),
                creator,  new Location(null, null, null,
                "Christchurch", null, "New Zealand"));
        activityRepository.save(activity);

        List<Fact> factList = new ArrayList<>();

        Goal goal = new Goal("Goal was scored", activity, player, 1, 1);

        Goal goal1 = new Goal("A Goal was scored again", activity, creator, 1, 1);
        Goal goal2 = new Goal("A Goal", activity, creator, 1, 1);

        Goal goal3 = new Goal("A Goal", activity, player1, 1, 1);
        Goal goal4 = new Goal("A Goal", activity, player1, 1, 1);
        Goal goal5 = new Goal("A Goal", activity, player1, 1, 1);

        Goal goal6 = new Goal("A Goal", activity, player2, 1, 1);
        Goal goal7 = new Goal("A Goal", activity, player2, 1, 1);
        Goal goal8 = new Goal("A Goal", activity, player2, 1, 1);
        Goal goal9 = new Goal("A Goal", activity, player2, 1, 1);

        Goal goal10 = new Goal("A Goal",activity, player3, 1, 1);
        Goal goal11 = new Goal("A Goal",activity, player3, 1, 1);
        Goal goal12 = new Goal("A Goal",activity, player3, 1, 1);
        Goal goal13 = new Goal("A Goal",activity, player3, 1, 1);
        Goal goal14 = new Goal("A Goal",activity, player3, 1, 1);

        Goal goal15 = new Goal("A Goal",activity, player4, 1, 1);
        Goal goal16 = new Goal("A Goal",activity, player4, 1, 1);
        Goal goal17 = new Goal("A Goal",activity, player4, 1, 1);
        Goal goal18 = new Goal("A Goal",activity, player4, 1, 1);
        Goal goal19 = new Goal("A Goal",activity, player4, 1, 1);
        Goal goal20 = new Goal("A Goal",activity, player4, 1, 1);

        factList.add(goal);
        factList.add(goal1);
        factList.add(goal2);
        factList.add(goal3);
        factList.add(goal4);
        factList.add(goal5);
        factList.add(goal6);
        factList.add(goal7);
        factList.add(goal8);
        factList.add(goal9);
        factList.add(goal10);
        factList.add(goal11);
        factList.add(goal12);
        factList.add(goal13);
        factList.add(goal14);
        factList.add(goal15);
        factList.add(goal16);
        factList.add(goal17);
        factList.add(goal18);
        factList.add(goal19);
        factList.add(goal20);
        activity.addFactList(factList);
        activityRepository.save(activity);

        Assertions.assertEquals(List.of(Map.of(player4, 6L), Map.of(player3, 5L), Map.of(player2, 4L), Map.of(player1, 3L), Map.of(creator, 2L)), (factService.getTop5Scorers(team)));
    }

    @Test
    public void testFactsSortedByTimeOfEventInAscendingOrder() throws Exception {
        Location location = new Location(null, null, null, "Christchurch", null,
                "New Zealand");

        Team team = new Team("Team 900", "Programming");
        User creator = new User("Test", "Account", "test123@test.com", "Password1!", location);

        Activity activity = new Activity(ActivityType.Game, team, "Game with Team",
                LocalDateTime.of(2023, 1,1,6,30),
                LocalDateTime.of(2023, 1,1,8,30),
                creator,  new Location(null, null, null,
                "Christchurch", null, "New Zealand"));

        List<Fact> factList = new ArrayList<>();
        factList.add(new Fact("Someone fell over", activity, 1));
        factList.add(new Fact("Someone fell over again",  activity, 2));
        factList.add(new Fact("Someone fell over yet again",  activity, 3));
        factList.add(new Substitution("Player was taken off", activity, creator, creator, 4));
        factList.add(new Fact("Testing scrollable feature",  activity, 5));

        factService.getFactsSortedByLocalTimeAscending(factList);
        Assertions.assertEquals(1 , factList.get(0).getTimeOfEvent());
        Assertions.assertEquals(2, factList.get(1).getTimeOfEvent());
        Assertions.assertEquals(3, factList.get(2).getTimeOfEvent());
        Assertions.assertEquals(4, factList.get(3).getTimeOfEvent());
        Assertions.assertEquals(5, factList.get(4).getTimeOfEvent());
    }

}
