package nz.ac.canterbury.seng302.tab.unit.repository;

import nz.ac.canterbury.seng302.tab.entity.Activity;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.enums.ActivityType;
import nz.ac.canterbury.seng302.tab.repository.ActivityRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@DataJpaTest
public class ActivityRepositoryTest {

    @Autowired
    ActivityRepository activityRepository;

    private Location location;
    @BeforeEach
    void beforeEach() {
        location = new Location(null, null, null, "Christchurch", null, "New Zealand");

    }

    @Test
    public void getActivityById() throws Exception {
        Team team = new Team("TeamName", "Sport");
        User creator = new User("Test", "Account", "test@test.com", "Password1!", location);
        Activity activity = new Activity(ActivityType.Game, team, "A random activity",
                LocalDateTime.of(2023, 1,1,6,30),
                LocalDateTime.of(2023, 1,1,8,30), creator, location);
        activityRepository.save(activity);
        Assertions.assertEquals(activity, activityRepository.findById(activity.getId()).get());
    }

    @Test
    public void getActivityListById() throws Exception {
        Team team = new Team("TeamName", "Sport");
        User creator = new User("Test", "Account", "test@test.com", "Password1!", location);
        Activity activity = new Activity(ActivityType.Game, team, "A random activity",
                LocalDateTime.of(2023, 1,1,6,30),
                LocalDateTime.of(2023, 1,1,8,30), creator, location);
        Activity activityTwo = new Activity(ActivityType.Friendly, team, "A random friendly",
                LocalDateTime.of(2023, 2,1,6,30),
                LocalDateTime.of(2023, 2,1,8,30), creator, location);
        activityRepository.save(activity);
        activityRepository.save(activityTwo);
        Assertions.assertEquals(List.of(activity, activityTwo), activityRepository.findAll());
    }

    @Test
    public void findActivitiesByUser() throws Exception {
        Team team1 = new Team("ATeamName", "Sport");
        Team team2 = new Team("bTeamName", "Sport");
        User user = new User("Test", "Account", "test@test.com", "Password1!",
                new Location(null, null, null, "Christchurch", null, "New Zealand"));
        Activity activity1 = new Activity(ActivityType.Game, null, "First activity",
                LocalDateTime.of(2023, 1,1,6,30),
                LocalDateTime.of(2023, 1,1,8,30), user,
                new Location(null, null, null, "Christchurch", null, "New Zealand"));

        Activity activity2 = new Activity(ActivityType.Game, null, "Second activity",
                LocalDateTime.of(2023, 1,2,9,0),
                LocalDateTime.of(2023, 1,2,11,0), user,
                new Location(null, null, null, "Dunners", null, "New Zealand"));

        Activity activity3 = new Activity(ActivityType.Game, team1, "Third activity",
                LocalDateTime.of(2023, 1,3,13,0),
                LocalDateTime.of(2023, 1,3,15,0), user,
                new Location(null, null, null, "Taupo", null, "New Zealand"));

        Activity activity4 = new Activity(ActivityType.Game, team1, "Fourth activity",
                LocalDateTime.of(2023, 1,4,16,0),
                LocalDateTime.of(2023, 1,4,18,0), user,
                new Location(null, null, null, "Auckland", null, "New Zealand"));

        Activity activity5 = new Activity(ActivityType.Game, team2, "Fifth activity",
                LocalDateTime.of(2023, 1,5,19,0),
                LocalDateTime.of(2023, 1,5,21,0), user,
                new Location(null, null, null, "Tauranga", null, "New Zealand"));

        Activity activity6 = new Activity(ActivityType.Game, team2, "Sixth activity",
                LocalDateTime.of(2023, 1,6,22,0),
                LocalDateTime.of(2023, 1,6,23,0), user,
                new Location(null, null, null, "Nelson", null, "New Zealand"));

        List<Activity> activities = Arrays.asList(activity1, activity2, activity3, activity4, activity5, activity6);
        activityRepository.save(activity1);
        activityRepository.save(activity2);
        activityRepository.save(activity3);
        activityRepository.save(activity4);
        activityRepository.save(activity5);
        activityRepository.save(activity6);
        Assertions.assertEquals(activities, activityRepository.findActivitiesByUserSorted(PageRequest.of(0,10),user).getContent());
    }


}
