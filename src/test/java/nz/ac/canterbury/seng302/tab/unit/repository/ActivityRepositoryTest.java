package nz.ac.canterbury.seng302.tab.unit.repository;

import nz.ac.canterbury.seng302.tab.entity.Activity;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.repository.ActivityRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
public class ActivityRepositoryTest {

    @Autowired
    ActivityRepository activityRepository;

    @Test
    public void getActivityById() throws IOException {
        Team team = new Team("TeamName", "Sport");
        User creator = new User("Test", "Account", "test@test.com", "Password1!",
                new Location(null, null, null, "Christchurch", null, "New Zealand"));
        Activity activity = new Activity(Activity.ActivityType.Game, team, "A random activity",
                LocalDateTime.of(2023, 1,1,6,30),
                LocalDateTime.of(2023, 1,1,8,30), creator);
        activityRepository.save(activity);
        Assertions.assertEquals(activity, activityRepository.findById(activity.getId()).get());
    }

    @Test
    public void getActivityListById() throws IOException {
        Team team = new Team("TeamName", "Sport");
        User creator = new User("Test", "Account", "test@test.com", "Password1!",
                new Location(null, null, null, "Christchurch", null, "New Zealand"));
        Activity activity = new Activity(Activity.ActivityType.Game, team, "A random activity",
                LocalDateTime.of(2023, 1,1,6,30),
                LocalDateTime.of(2023, 1,1,8,30), creator);
        Activity activityTwo = new Activity(Activity.ActivityType.Friendly, team, "A random friendly",
                LocalDateTime.of(2023, 2,1,6,30),
                LocalDateTime.of(2023, 2,1,8,30), creator);
        activityRepository.save(activity);
        activityRepository.save(activityTwo);
        Assertions.assertEquals(List.of(activity, activityTwo), activityRepository.findAll());
    }

}
