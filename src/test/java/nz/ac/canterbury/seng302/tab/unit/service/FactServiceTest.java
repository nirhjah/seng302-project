package nz.ac.canterbury.seng302.tab.unit.service;

import nz.ac.canterbury.seng302.tab.entity.Activity;
import nz.ac.canterbury.seng302.tab.entity.Fact.Fact;
import nz.ac.canterbury.seng302.tab.entity.Fact.Goal;
import nz.ac.canterbury.seng302.tab.entity.Fact.Substitution;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.enums.ActivityType;
import nz.ac.canterbury.seng302.tab.enums.FactType;
import nz.ac.canterbury.seng302.tab.repository.ActivityRepository;
import nz.ac.canterbury.seng302.tab.repository.FactRepository;
import nz.ac.canterbury.seng302.tab.service.FactService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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
        factList.add(new Fact("Someone fell over", "1h 30m", activity));
        Goal goal = new Goal("Goal was scored", "1h 40m", activity, player, 1);
        factRepository.save(goal);
        factList.add(goal);
        factList.add(new Substitution("Player was taken off", "1h 40m", activity, creator, player));
        activity.addFactList(factList);
        activityRepository.save(activity);

        Map<User, Long> scoreInformation = new HashMap<>();
        scoreInformation.put(player, 1L);


        Assertions.assertEquals(scoreInformation, (factService.getTop5Scorers(team)));

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
        factList.add(new Fact("Someone fell over", "1h 30m", activity));
        Goal goal = new Goal("Goal was scored", "1h 40m", activity, player, 1);
        Goal goal1 = new Goal("A Goal was scored again", "1h 40m", activity, creator, 1);
        Goal goal2 = new Goal("A Goal", "1h 40m", activity, creator, 1);
        factRepository.save(goal1);
        factRepository.save(goal2);
        factRepository.save(goal);
        factList.add(goal);
        factList.add(goal1);
        factList.add(goal2);
        factList.add(new Substitution("Player was taken off", "1h 40m", activity, creator, player));
        activity.addFactList(factList);
        activityRepository.save(activity);


        Map<User, Long> scoreInformation = new HashMap<>();
        scoreInformation.put(creator, 2L);
        scoreInformation.put(player, 1L);


        Assertions.assertEquals(scoreInformation, (factService.getTop5Scorers(team)));

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

        Goal goal = new Goal("Goal was scored", "1h 40m", activity, player, 1);

        Goal goal1 = new Goal("A Goal was scored again", "1h 40m", activity, creator, 1);
        Goal goal2 = new Goal("A Goal", "1h 40m", activity, creator, 1);

        Goal goal3 = new Goal("A Goal", "1h 40m", activity, player1, 1);
        Goal goal4 = new Goal("A Goal", "1h 40m", activity, player1, 1);
        Goal goal5 = new Goal("A Goal", "1h 40m", activity, player1, 1);

        Goal goal6 = new Goal("A Goal", "1h 40m", activity, player2, 1);
        Goal goal7 = new Goal("A Goal", "1h 40m", activity, player2, 1);
        Goal goal8 = new Goal("A Goal", "1h 40m", activity, player2, 1);
        Goal goal9 = new Goal("A Goal", "1h 40m", activity, player2, 1);

        Goal goal10 = new Goal("A Goal", "1h 40m", activity, player3, 1);
        Goal goal11 = new Goal("A Goal", "1h 40m", activity, player3, 1);
        Goal goal12 = new Goal("A Goal", "1h 40m", activity, player3, 1);
        Goal goal13 = new Goal("A Goal", "1h 40m", activity, player3, 1);
        Goal goal14 = new Goal("A Goal", "1h 40m", activity, player3, 1);

        Goal goal15 = new Goal("A Goal", "1h 40m", activity, player4, 1);
        Goal goal16 = new Goal("A Goal", "1h 40m", activity, player4, 1);
        Goal goal17 = new Goal("A Goal", "1h 40m", activity, player4, 1);
        Goal goal18 = new Goal("A Goal", "1h 40m", activity, player4, 1);
        Goal goal19 = new Goal("A Goal", "1h 40m", activity, player4, 1);
        Goal goal20 = new Goal("A Goal", "1h 40m", activity, player4, 1);

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

        Map<User, Long> scoreInformation = new HashMap<>();
        scoreInformation.put(player4, 6L);
        scoreInformation.put(player3, 5L);
        scoreInformation.put(player2, 4L);
        scoreInformation.put(player1, 3L);
        scoreInformation.put(creator, 2L);



        Assertions.assertEquals(scoreInformation, (factService.getTop5Scorers(team)));
    }

    @Test
    void testGoalsInCorrectOrder() throws Exception {
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
        List<Goal> goalsList = new ArrayList<>();

        Goal goal1 = new Goal("Goal was scored", "40", activity, player, 1);
        Goal goal2 = new Goal("Goal was scored again", "25", activity, player, 1);
        Goal goal3 = new Goal("Goal was scored yet again", "27", activity, player, 1);

        factRepository.save(goal1);
        factRepository.save(goal2);
        factRepository.save(goal3);

        factList.add(goal1);
        factList.add(goal2);
        factList.add(goal3);

        activity.addFactList(factList);

        activityRepository.save(activity);

        for (Object fact : activity.getFactList()) {
            if(fact instanceof Goal) {
                goalsList.add((Goal) fact);
            }
        }

        List<Goal> expectedGoalList = List.of(goal2, goal3, goal1);
        Assertions.assertEquals(expectedGoalList, factService.getAllFactsOfGivenTypeForActivity(FactType.GOAL.ordinal(), activity));
    }

    @Test
    void factTimeOrdering() throws Exception {
        Location location = new Location(null, null, null, "Christchurch", null,
                "New Zealand");
        Team team = new Team("Team 900", "Programming");
        User creator = new User("Test", "Account", "test123@test.com", "Password1!", location);
        Activity activity = new Activity(ActivityType.Game, team, "Game with Team",
                LocalDateTime.of(2023, 1,1,6,30),
                LocalDateTime.of(2023, 1,1,8,30),
                creator,  new Location(null, null, null,
                "Christchurch", null, "New Zealand"));
        activityRepository.save(activity);

        Goal goal1 = new Goal("Goal was scored", "1", activity, creator, 1);
        Goal goal2 = new Goal("Goal was scored again", null, activity, creator, 1);
        Goal goal3 = new Goal("Goal was scored yet again", "2", activity, creator, 1);

        factRepository.save(goal1);
        factRepository.save(goal2);
        factRepository.save(goal3);
        activityRepository.save(activity);

        List<Goal> expectedGoalList = List.of(goal2, goal1, goal3);
        Assertions.assertEquals(expectedGoalList, factService.getAllFactsOfGivenTypeForActivity(FactType.GOAL.ordinal(), activity));
    }

    @Test
    void factTimeOrderingIncluding10() throws Exception {
        Location location = new Location(null, null, null, "Christchurch", null,
                "New Zealand");
        Team team = new Team("Team 900", "Programming");
        User creator = new User("Test", "Account", "test123@test.com", "Password1!", location);
        Activity activity = new Activity(ActivityType.Game, team, "Game with Team",
                LocalDateTime.of(2023, 1,1,6,30),
                LocalDateTime.of(2023, 1,1,8,30),
                creator,  new Location(null, null, null,
                "Christchurch", null, "New Zealand"));
        activityRepository.save(activity);

        Goal goal1 = new Goal("Goal was scored", "1", activity, creator, 1);
        Goal goal2 = new Goal("Goal was scored again", "10", activity, creator, 1);
        Goal goal3 = new Goal("Goal was scored yet again", "2", activity, creator, 1);

        factRepository.save(goal1);
        factRepository.save(goal2);
        factRepository.save(goal3);
        activityRepository.save(activity);

        List<Goal> expectedGoalList = List.of(goal1, goal3, goal2);
        Assertions.assertEquals(expectedGoalList, factService.getAllFactsOfGivenTypeForActivity(FactType.GOAL.ordinal(), activity));
    }
}
