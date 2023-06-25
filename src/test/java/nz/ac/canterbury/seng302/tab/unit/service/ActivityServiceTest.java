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
    public void ifStartDateIsBeforeEnd_returnTrue(){
        LocalDateTime start =   LocalDateTime.of(2023, 1,1,6,30);
        LocalDateTime end = LocalDateTime.of(2023, 1,1,8,30);
        Assertions.assertTrue(activityService.validateStartAndEnd(start, end));
    }

    /**
     * Tests validator for if a start date if after the end
     */
    @Test
    public void ifStartDateIsAfterEnd_returnFalse(){
        LocalDateTime start =   LocalDateTime.of(2023, 1,1,6,30);
        LocalDateTime end = LocalDateTime.of(2023, 1,1,4,30);
        Assertions.assertFalse(activityService.validateStartAndEnd(start, end));
    }

    /**
     * Tests that if the start date is before the team creation, it'll not accept
     */
    @Test
    public void ifStartDateIsBeforeTeamCreation_returnFalse(){
        LocalDateTime teamCreation = LocalDateTime.of(2022, 1,1, 10, 30);
        LocalDateTime start = LocalDateTime.of(2021, 1,1,10,30);
        LocalDateTime end = LocalDateTime.of(2023, 1,1,8,30);
        Assertions.assertFalse(activityService.validateActivityDateTime(teamCreation, start, end));
    }

    /**
     * Tests that if the start date and end date are after team creation, return true
     */
    @Test
    public void ifStartAndEndDateIsAfterTeamCreation_returnTrue()  {
        LocalDateTime teamCreation = LocalDateTime.of(2022, 1,1, 10, 30);
        LocalDateTime start = LocalDateTime.of(2023, 1,1,10,30);
        LocalDateTime end = LocalDateTime.of(2023, 1,1,8,30);
        Assertions.assertTrue(activityService.validateActivityDateTime(teamCreation, start, end));
    }


    /**
     * Tests that if the end date is before the team creation, it'll not accept
     */
    @Test
    public void ifEndDateIsBeforeTeamCreation_returnFalse(){
        LocalDateTime teamCreation = LocalDateTime.of(2022, 1,1, 10, 30);
        LocalDateTime start = LocalDateTime.of(2021, 1,1,6,30);
        LocalDateTime end = LocalDateTime.of(2021, 1,1,8,30);
        Assertions.assertFalse(activityService.validateActivityDateTime(teamCreation, start, end));

    }


    /**
     * Tests if both teams scores for an activity are of the same format with a hyphen, return true
     */
    @Test
    public void ifActivityScoreBothSameFormat_Hyphen_returnTrue() {
        String activityTeamScore = "141-9";
        String otherTeamScore = "94-3";
        Assertions.assertTrue(activityService.validateActivityScore(activityTeamScore, otherTeamScore));
    }


    /**
     * Tests if both teams scores for an activity are of different format where only first score has a hyphen, return false
     */
    @Test
    public void ifActivityScoreBothDifferentFormat_FirstScoreHyphen_returnFalse() {
        String activityTeamScore = "141-9";
        String otherTeamScore = "94";
        Assertions.assertFalse(activityService.validateActivityScore(activityTeamScore, otherTeamScore));
    }

    /**
     * Tests if both teams scores for an activity are of the same format with a number only, return true
     */
    @Test
    public void ifActivityScoreBothSameFormat_NumberOnly_returnTrue() {
        String activityTeamScore = "141";
        String otherTeamScore = "94";
        Assertions.assertTrue(activityService.validateActivityScore(activityTeamScore, otherTeamScore));
    }

    /**
     * Tests if both teams scores for an activity are of different format where only first team has score number only, return false
     */
    @Test
    public void ifActivityScoreBothDifferentFormat_OneScoreNumberOnly_returnFalse() {
        String activityTeamScore = "99";
        String otherTeamScore = "94-23";
        Assertions.assertFalse(activityService.validateActivityScore(activityTeamScore, otherTeamScore));
    }

    /**
     * A team is required for activity type game, therefore validation should return false if there is no team given.
     */
    @Test
    public void ifNoTeamAndActivityTypeGame_ReturnFalse() {
        Assertions.assertFalse(activityService.validateTeamSelection(Activity.ActivityType.Game, null));
    }

    /**
     * A team is required for activity type friendly, therefore validation should return false if there is no team given.
     */
    @Test
    public void ifNoTeamAndActivityTypeFriendly_ReturnFalse() {
        Assertions.assertFalse(activityService.validateTeamSelection(Activity.ActivityType.Friendly, null));
    }

    /**
     * A team is required for activity type friendly, therefore validation wil return true as a team is provided
     * @throws IOException - thrown for creation of team due to profile picture.
     */
    @Test
    public void ifTeamAndActivityTypeFriendly_ReturnTrue() throws IOException {
        Team team = new Team("Team 900", "Programming");
        Assertions.assertTrue(activityService.validateTeamSelection(Activity.ActivityType.Friendly, team));
    }

    /**
     * A team is required for activity type ganr, therefore validation wil return true as a team is provided
     * @throws IOException - thrown for creation of team due to profile picture.
     */
    @Test
    public void ifTeamAndActivityTypeGame_ReturnTrue() throws IOException {
        Team team = new Team("Team 900", "Programming");
        Assertions.assertTrue(activityService.validateTeamSelection(Activity.ActivityType.Game, team));
    }

    /**
     * A team is not required for activity type other, therefore validation will return true
     * @throws IOException - thrown for creation of team due to profile picture.
     */
    @Test
    public void ifTeamAndActivityTypeOther_ReturnTrue() throws IOException {
        Team team = new Team("Team 900", "Programming");
        Assertions.assertTrue(activityService.validateTeamSelection(Activity.ActivityType.Other, team));
    }

    /**
     * A team is not required for activity type other, therefore validation will return true
     */
    @Test
    public void ifNoTeamAndActivityTypeOther_ReturnTrue() {
        Assertions.assertTrue(activityService.validateTeamSelection(Activity.ActivityType.Other, null));
    }

    /**
     * Testing to see that if a team has no activities, an empty page is returned
     * @throws Exception could be thrown by team creation due to profile picture encoding for database.
     */
    @Test
    public void ifNoActivitiesForTeamReturnEmpty() throws IOException {
        Team t = new Team("Test Team", "Hockey");
        teamRepository.save(t);
        Assertions.assertEquals(List.of(), activityService.getAllTeamActivitiesPage(t, 1, 10).toList());
    }

    /**
     * Testing to see that if a team has an activity, an page with only that activity is returned
     * @throws Exception could be thrown by user or team creation due to profile picture encoding for database.
     */
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

    /**
     * Testing to see that if a team has an activity, and it's manager has activities, a page with only the is returned
     * when the manager requests all activities
     * @throws Exception could be thrown by user or team creation due to profile picture encoding for database.
     */
    @Test
    public void ifATeamHasActivitiesAndUserHasPersonalActivities_OnlyReturnTeamActivities() throws Exception {
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

    @Test
    public void getActivityThatDoesNotExist_returnNull() {
        Assertions.assertNull(activityService.findActivityById(-1L));
    }

    @Test
    public void getActivityThatDoesExist_returnActivity() throws Exception {
        User u = new User("Test", "Account", "tab.team900@gmail.com", "password",
                new Location("1 Place", "B", "Ilam", "CHCH", "808", "NZ"));
        Activity training = new Activity(Activity.ActivityType.Training, null, "A Test Game",
                LocalDateTime.of(2026, 1,1,6,30),
                LocalDateTime.of(2026, 1,1,8,30), u,
                new Location("Jack Erskine", null, "Ilam", "Chch", "Test", "NZ"));
        activityRepository.save(training);
        Assertions.assertEquals(training, activityService.findActivityById(training.getId()));
    }

}
