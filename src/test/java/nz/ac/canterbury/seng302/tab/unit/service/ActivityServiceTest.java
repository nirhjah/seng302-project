package nz.ac.canterbury.seng302.tab.unit.service;

import nz.ac.canterbury.seng302.tab.entity.Activity;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.service.ActivityService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.io.IOException;
import java.time.LocalDateTime;

@DataJpaTest
@Import(ActivityService.class)
public class ActivityServiceTest {

    @Autowired
    ActivityService activityService;


    /**
     * Tests validator for if a start date is before the end
     */
    @Test
    public void ifStartDateIsBeforeEnd_returnTrue() throws IOException {
        Team team = new Team("TeamName", "Sport");
        User creator = new User("Test", "Account", "test@test.com", "Password1!",
                new Location(null, null, null, "Christchurch", "New Zealand", null));
        Activity activity = new Activity(Activity.ActivityType.Game, team, "A random activity",
                LocalDateTime.of(2023, 1,1,6,30),
                LocalDateTime.of(2023, 1,1,8,30), creator);
        Assertions.assertTrue(activityService.validateStartAndEnd(activity));
    }

    /**
     * Tests validator for if a start date if after the end
     */
    @Test
    public void ifStartDateIsAfterEnd_returnFalse() throws IOException {
        Team team = new Team("TeamName", "Sport");
        User creator = new User("Test", "Account", "test@test.com", "Password1!",
                new Location(null, null, null, "Christchurch", "New Zealand", null));
        Activity activity = new Activity(Activity.ActivityType.Game, team, "A random activity",
                LocalDateTime.of(2023, 1,1,10,30),
                LocalDateTime.of(2023, 1,1,8,30), creator);
        Assertions.assertFalse(activityService.validateStartAndEnd(activity));
    }

    /**
     * Tests that if the start date is before the team creation, it'll not accept
     * @throws IOException - Exception because of profile picture upload
     */
    @Test
    public void ifStartDateIsBeforeTeamCreation_returnFalse() throws IOException {
        Team team = new Team("TeamName", "Sport");
        User creator = new User("Test", "Account", "test@test.com", "Password1!",
                new Location(null, null, null, "Christchurch", "New Zealand", null));
        Activity activity = new Activity(Activity.ActivityType.Game, team, "A random activity",
                LocalDateTime.of(2020, 1,1,10,30),
                LocalDateTime.of(2023, 1,1,8,30), creator);
        Assertions.assertFalse(activityService.validateActivityDateTime(activity));
    }

    /**
     * Tests that if the start date and end date are after team creation, return true
     * @throws IOException - Exception because of profile picture upload
     */
    @Test
    public void ifStartAndEndDateIsAfterTeamCreation_returnTrue() throws IOException {
        Team team = new Team("TeamName", "Sport");
        User creator = new User("Test", "Account", "test@test.com", "Password1!",
                new Location(null, null, null, "Christchurch", "New Zealand", null));
        Activity activity = new Activity(Activity.ActivityType.Game, team, "A random activity",
                LocalDateTime.of(2023, 1,1,10,30),
                LocalDateTime.of(2023, 1,1,8,30), creator);
        Assertions.assertFalse(activityService.validateActivityDateTime(activity));
    }


    /**
     * Tests that if the end date is before the team creation, it'll not accept
     * @throws IOException - Exception due to profile pictures
     */
    @Test
    public void ifEndDateIsBeforeTeamCreation_returnFalse() throws IOException {
        Team team = new Team("TeamName", "Sport");
        User creator = new User("Test", "Account", "test@test.com", "Password1!",
                new Location(null, null, null, "Christchurch", "New Zealand", null));
        Activity activity = new Activity(Activity.ActivityType.Game, team, "A random activity",
                LocalDateTime.of(2023, 1,1,10,30),
                LocalDateTime.of(2020, 1,1,8,30), creator);
        Assertions.assertFalse(activityService.validateActivityDateTime(activity));
    }

}