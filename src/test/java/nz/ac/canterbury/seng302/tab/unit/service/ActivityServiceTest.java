package nz.ac.canterbury.seng302.tab.unit.service;

import nz.ac.canterbury.seng302.tab.entity.Activity;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.repository.ActivityRepository;
import nz.ac.canterbury.seng302.tab.repository.TeamRepository;
import nz.ac.canterbury.seng302.tab.service.ActivityService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
@Import(ActivityService.class)
public class ActivityServiceTest {

    @Autowired
    ActivityService activityService;

    @Autowired
    ActivityRepository activityRepository;

    @Autowired
    TeamRepository teamRepository;

    /**
     * Tests validator for if a start date is before the end
     */
    @Test
    public void ifStartDateIsBeforeEnd_returnTrue() throws IOException {
        LocalDateTime start =   LocalDateTime.of(2023, 1,1,6,30);
        LocalDateTime end = LocalDateTime.of(2023, 1,1,8,30);
        Assertions.assertTrue(activityService.validateStartAndEnd(start, end));
    }

    /**
     * Tests validator for if a start date if after the end
     */
    @Test
    public void ifStartDateIsAfterEnd_returnFalse() throws IOException {
        LocalDateTime start =   LocalDateTime.of(2023, 1,1,6,30);
        LocalDateTime end = LocalDateTime.of(2023, 1,1,4,30);
        Assertions.assertFalse(activityService.validateStartAndEnd(start, end));
    }

    /**
     * Tests that if the start date is before the team creation, it'll not accept
     * @throws IOException - Exception because of profile picture upload
     */
    @Test
    public void ifStartDateIsBeforeTeamCreation_returnFalse() throws IOException {
        LocalDateTime teamCreation = LocalDateTime.of(2022, 1,1, 10, 30);
        LocalDateTime start = LocalDateTime.of(2021, 1,1,10,30);
        LocalDateTime end = LocalDateTime.of(2023, 1,1,8,30);
        Assertions.assertFalse(activityService.validateActivityDateTime(teamCreation, start, end));
    }

    /**
     * Tests that if the start date and end date are after team creation, return true
     * @throws IOException - Exception because of profile picture upload
     */
    @Test
    public void ifStartAndEndDateIsAfterTeamCreation_returnTrue() throws IOException {
        LocalDateTime teamCreation = LocalDateTime.of(2022, 1,1, 10, 30);
        LocalDateTime start = LocalDateTime.of(2023, 1,1,10,30);
        LocalDateTime end = LocalDateTime.of(2023, 1,1,8,30);
        Assertions.assertTrue(activityService.validateActivityDateTime(teamCreation, start, end));
    }


    /**
     * Tests that if the end date is before the team creation, it'll not accept
     * @throws IOException - Exception due to profile pictures
     */
    @Test
    public void ifEndDateIsBeforeTeamCreation_returnFalse() throws IOException {
        LocalDateTime teamCreation = LocalDateTime.of(2022, 1,1, 10, 30);
        LocalDateTime start = LocalDateTime.of(2021, 1,1,6,30);
        LocalDateTime end = LocalDateTime.of(2021, 1,1,8,30);
    }

    @Test
    public void ifNoActivitiesForTeamReturnEmpty() throws IOException {
        Team t = new Team("Test Team", "Hockey");
        teamRepository.save(t);
        Assertions.assertEquals(List.of(), activityService.getAllTeamActivitiesPage(t, 1, 10).toList());
    }

    @Test
    public void ifATeamHasActivites_ReturnsPageOfThem() throws Exception {
        Team t = new Team("Test Team", "Hockey");
        teamRepository.save(t);
        User u = new User("Test", "Account", "tab.team900@gmail.com", "password", new Location("1 Place", "B", "Ilam", "CHCH", "808", "NZ"));
        Activity game = new Activity(Activity.ActivityType.Game, t, "A Test Game",
                LocalDateTime.of(2025, 1,1,6,30),
                LocalDateTime.of(2025, 1,1,8,30), u,
                new Location("Test", "Test", "Test", "test", "Tst", "test"));
        activityRepository.save(game);
        Assertions.assertEquals(List.of(game), activityService.getAllTeamActivitiesPage(t, 1, 10).toList());
    }

    @Test
    public void ifATeamHasActivitesAndUserHasPersonalActivities_OnlyReturnTeamActivities() throws Exception {
        Team t = new Team("Test Team", "Hockey");
        teamRepository.save(t);
        User u = new User("Test", "Account", "tab.team900@gmail.com", "password", new Location("1 Place", "B", "Ilam", "CHCH", "808", "NZ"));
        Activity game = new Activity(Activity.ActivityType.Game, t, "A Test Game",
                LocalDateTime.of(2025, 1,1,6,30),
                LocalDateTime.of(2025, 1,1,8,30), u,
                new Location("Test", "Test", "Test", "test", "Tst", "test"));
        Activity training = new Activity(Activity.ActivityType.Training, null, "A Test Game",
                LocalDateTime.of(2026, 1,1,6,30),
                LocalDateTime.of(2026, 1,1,8,30), u,
                new Location("Jack Erskine", null, "Ilam", "Chch", "Test", "NZ"));
        activityRepository.save(game);
        activityRepository.save(training);
        Assertions.assertEquals(List.of(game), activityService.getAllTeamActivitiesPage(t, 1, 10).toList());
    }

}
