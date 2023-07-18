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
        Goal goal = new Goal("Goal was scored", "1h 40m", activity, player);
        factRepository.save(goal);
        factList.add(goal);
        factList.add(new Substitution("Player was taken off", "1h 40m", activity, creator, player));
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
        factList.add(new Fact("Someone fell over", "1h 30m", activity));
        Goal goal = new Goal("Goal was scored", "1h 40m", activity, player);
        Goal goal1 = new Goal("A Goal was scored again", "1h 40m", activity, creator);
        Goal goal2 = new Goal("A Goal", "1h 40m", activity, creator);
        factRepository.save(goal1);
        factRepository.save(goal2);
        factRepository.save(goal);
        factList.add(goal);
        factList.add(goal1);
        factList.add(goal2);
        factList.add(new Substitution("Player was taken off", "1h 40m", activity, creator, player));
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

        Goal goal = new Goal("Goal was scored", "1h 40m", activity, player);

        Goal goal1 = new Goal("A Goal was scored again", "1h 40m", activity, creator);
        Goal goal2 = new Goal("A Goal", "1h 40m", activity, creator);

        Goal goal3 = new Goal("A Goal", "1h 40m", activity, player1);
        Goal goal4 = new Goal("A Goal", "1h 40m", activity, player1);
        Goal goal5 = new Goal("A Goal", "1h 40m", activity, player1);

        Goal goal6 = new Goal("A Goal", "1h 40m", activity, player2);
        Goal goal7 = new Goal("A Goal", "1h 40m", activity, player2);
        Goal goal8 = new Goal("A Goal", "1h 40m", activity, player2);
        Goal goal9 = new Goal("A Goal", "1h 40m", activity, player2);

        Goal goal10 = new Goal("A Goal", "1h 40m", activity, player3);
        Goal goal11 = new Goal("A Goal", "1h 40m", activity, player3);
        Goal goal12 = new Goal("A Goal", "1h 40m", activity, player3);
        Goal goal13 = new Goal("A Goal", "1h 40m", activity, player3);
        Goal goal14 = new Goal("A Goal", "1h 40m", activity, player3);

        Goal goal15 = new Goal("A Goal", "1h 40m", activity, player4);
        Goal goal16 = new Goal("A Goal", "1h 40m", activity, player4);
        Goal goal17 = new Goal("A Goal", "1h 40m", activity, player4);
        Goal goal18 = new Goal("A Goal", "1h 40m", activity, player4);
        Goal goal19 = new Goal("A Goal", "1h 40m", activity, player4);
        Goal goal20 = new Goal("A Goal", "1h 40m", activity, player4);

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
}