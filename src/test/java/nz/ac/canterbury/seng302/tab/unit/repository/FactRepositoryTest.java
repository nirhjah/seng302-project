package nz.ac.canterbury.seng302.tab.unit.repository;

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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@DataJpaTest
public class FactRepositoryTest {

    @Autowired
    FactRepository factRepository;

    @Autowired
    ActivityRepository activityRepository;

    @Test
    public void getAllActivityFacts() throws Exception {
        Location location = new Location(null, null, null, "Christchurch", null,
                "New Zealand");
        Team team = new Team("Team 900", "Programming");
        User creator = new User("Test", "Account", "test@test.com", "Password1!", location);
        User player = new User("Another", "Test", "test12@test.com", "Password1!",
                new Location(null, null, null, "CHCH", null, "NZ"));
        Activity activity = new Activity(ActivityType.Game, team, "Game with Team",
                LocalDateTime.of(2023, 1,1,6,30),
                LocalDateTime.of(2023, 1,1,8,30),
                creator,  new Location(null, null, null,
                "Christchurch", null, "New Zealand"));
        activityRepository.save(activity);

        List<Fact> factList = new ArrayList<>();
        factList.add(new Fact("Someone fell over", "1h 30m", activity));
        factList.add(new Goal("Goal was scored", "1h 40m", activity, creator));
        factList.add(new Substitution("Player was taken off", "1h 40m", activity, creator, player));
        activity.addFactList(factList);
        activityRepository.save(activity);

        Assertions.assertEquals(factRepository.getFactByActivity(activity), factList);
    }

    @Test
    public void getActivityGoalsOnly() throws Exception {
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
        Goal goal = new Goal("Goal was scored", "1h 40m", activity, creator);
        factRepository.save(goal);
        factList.add(goal);
        factList.add(new Substitution("Player was taken off", "1h 40m", activity, creator, player));
        activity.addFactList(factList);
        activityRepository.save(activity);

        Assertions.assertEquals(factRepository.getFactByFactTypeAndActivity(FactType.GOAL.ordinal(), activity), List.of(goal));
    }

    @Test
    public void getActivitySubstitutionOnly() throws Exception {
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
        factList.add(new Goal("Goal was scored", "1h 40m", activity, creator));
        Substitution sub = new Substitution("Player was taken off", "1h 40m", activity, creator, player);
        factRepository.save(sub);
        factList.add(sub);
        activity.addFactList(factList);
        activityRepository.save(activity);

        Assertions.assertEquals(factRepository.getFactByFactTypeAndActivity(FactType.SUBSTITUTION.ordinal(), activity), List.of(sub));
    }

    @Test
    public void getActivityFactsOnly() throws Exception {
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
        Fact fact = new Fact("Someone fell over", "1h 30m", activity);
        factRepository.save(fact);
        factList.add(fact);
        factList.add(new Goal("Goal was scored", "1h 40m", activity, creator));
        factList.add(new Substitution("Player was taken off", "1h 40m", activity, creator, player));
        activity.addFactList(factList);
        activityRepository.save(activity);

        Assertions.assertEquals(factRepository.getFactByFactTypeAndActivity(FactType.FACT.ordinal(), activity), List.of(fact));
    }

}
