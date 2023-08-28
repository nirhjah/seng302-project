package nz.ac.canterbury.seng302.tab.unit.service;

import nz.ac.canterbury.seng302.tab.entity.Fact.Fact;
import nz.ac.canterbury.seng302.tab.entity.Fact.Goal;
import nz.ac.canterbury.seng302.tab.entity.Fact.Substitution;
import nz.ac.canterbury.seng302.tab.enums.*;
import nz.ac.canterbury.seng302.tab.entity.Activity;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.repository.ActivityRepository;
import nz.ac.canterbury.seng302.tab.repository.FactRepository;
import nz.ac.canterbury.seng302.tab.repository.TeamRepository;
import nz.ac.canterbury.seng302.tab.service.ActivityService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

    @Autowired
    FactRepository factRepository;
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
        Assertions.assertEquals(0, activityService.validateActivityScore(activityTeamScore, otherTeamScore));
    }


    /**
     * Tests if both teams scores for an activity are of different format where only first score has a hyphen, return false
     */
    @Test
    public void ifActivityScoreBothDifferentFormat_FirstScoreHyphen_returnFalse() {
        String activityTeamScore = "141-9";
        String otherTeamScore = "94";
        Assertions.assertEquals(1, activityService.validateActivityScore(activityTeamScore, otherTeamScore));
    }

    /**
     * Tests if both teams scores for an activity are of the same format with a number only, return true
     */
    @Test
    public void ifActivityScoreBothSameFormat_NumberOnly_returnTrue() {
        String activityTeamScore = "141";
        String otherTeamScore = "94";
        Assertions.assertEquals(0, activityService.validateActivityScore(activityTeamScore, otherTeamScore));
    }

    /**
     * Tests if both teams scores for an activity are both empty, return false
     */
    @Test
    public void ifActivityScoreBothEmpty_returnTrue() {
        String activityTeamScore = "";
        String otherTeamScore = "";
        Assertions.assertEquals(0, activityService.validateActivityScore(activityTeamScore, otherTeamScore));
    }

    /**
     * Tests if one team score is empty and the other is not, return false
     */
    @Test
    public void ifOneActivityScoreEmptyAndOtherNot_returnFalse() {
        String activityTeamScore = "3";
        String otherTeamScore = "";
        Assertions.assertEquals(2, activityService.validateActivityScore(activityTeamScore, otherTeamScore));
    }

    /**
     * Tests if both teams scores for an activity are of different format where only first team has score number only, return false
     */
    @Test
    public void ifActivityScoreBothDifferentFormat_OneScoreNumberOnly_returnFalse() {
        String activityTeamScore = "99";
        String otherTeamScore = "94-23";
        Assertions.assertEquals(1, activityService.validateActivityScore(activityTeamScore, otherTeamScore));
    }

    /**
     * A team is required for activity type game, therefore validation should return false if there is no team given.
     */
    @Test
    public void ifNoTeamAndActivityTypeGame_ReturnFalse() {
        Assertions.assertFalse(activityService.validateTeamSelection(ActivityType.Game, null));
    }

    /**
     * A team is required for activity type friendly, therefore validation should return false if there is no team given.
     */
    @Test
    public void ifNoTeamAndActivityTypeFriendly_ReturnFalse() {
        Assertions.assertFalse(activityService.validateTeamSelection(ActivityType.Friendly, null));
    }

    /**
     * A team is required for activity type friendly, therefore validation wil return true as a team is provided
     * @throws IOException - thrown for creation of team due to profile picture.
     */
    @Test
    public void ifTeamAndActivityTypeFriendly_ReturnTrue() throws IOException {
        Team team = new Team("Team 900", "Programming");
        Assertions.assertTrue(activityService.validateTeamSelection(ActivityType.Friendly, team));
    }

    /**
     * A team is required for activity type ganr, therefore validation wil return true as a team is provided
     * @throws IOException - thrown for creation of team due to profile picture.
     */
    @Test
    public void ifTeamAndActivityTypeGame_ReturnTrue() throws IOException {
        Team team = new Team("Team 900", "Programming");
        Assertions.assertTrue(activityService.validateTeamSelection(ActivityType.Game, team));
    }

    /**
     * A team is not required for activity type other, therefore validation will return true
     * @throws IOException - thrown for creation of team due to profile picture.
     */
    @Test
    public void ifTeamAndActivityTypeOther_ReturnTrue() throws IOException {
        Team team = new Team("Team 900", "Programming");
        Assertions.assertTrue(activityService.validateTeamSelection(ActivityType.Other, team));
    }

    /**
     * A team is not required for activity type other, therefore validation will return true
     */
    @Test
    public void ifNoTeamAndActivityTypeOther_ReturnTrue() {
        Assertions.assertTrue(activityService.validateTeamSelection(ActivityType.Other, null));
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
        Activity game = new Activity(ActivityType.Game, t, "A Test Game",
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
        Activity game = new Activity(ActivityType.Game, t, "A Test Game",
                LocalDateTime.of(2025, 1,1,6,30),
                LocalDateTime.of(2025, 1,1,8,30), u,
                new Location("Test", "Test", "Test", "test", "Tst", "test"));
        Activity training = new Activity(ActivityType.Training, null, "A Test Game",
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
        Activity training = new Activity(ActivityType.Training, null, "A Test Game",
                LocalDateTime.of(2026, 1,1,6,30),
                LocalDateTime.of(2026, 1,1,8,30), u,
                new Location("Jack Erskine", null, "Ilam", "Chch", "Test", "NZ"));
        activityRepository.save(training);
        Assertions.assertEquals(training, activityService.findActivityById(training.getId()));
    }

    @Test
    public void getTotalTimePlayerHasPlayedForTeam() throws Exception {
        Team team = new Team("adfds", "hello");
        User u = new User("Test", "Account", "tab.team900@gmail.com", "password",
                new Location("1 Place", "B", "Ilam", "CHCH", "808", "NZ"));
        Activity training = new Activity(ActivityType.Training, team, "A Test Game",
                LocalDateTime.of(2026, 1,1,6,30),
                LocalDateTime.of(2026, 1,1,8,30), u,
                new Location("Jack Erskine", null, "Ilam", "Chch", "Test", "NZ"));
        Activity game = new Activity(ActivityType.Game, team, "A Test Game",
                LocalDateTime.of(2026, 1,1,6,30),
                LocalDateTime.of(2026, 1,1,8,30), u,
                new Location("Jack Erskine", null, "Ilam", "Chch", "Test", "NZ"));
        activityRepository.save(training);
        activityRepository.save(game);
        Assertions.assertEquals(120, activityService.getTotalTimeAUserHasPlayedForATeam(u, team));
    }

    @Test
    public void testSortGoalTimesAscending() throws Exception {

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
        Assertions.assertEquals(expectedGoalList, activityService.sortGoalTimesAscending(goalsList));

    }


    @Test
    public void testcheckTimeOfFactWithinActivity_FactTimeOutOfDuration() throws Exception {
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
        Assertions.assertFalse(activityService.checkTimeOfFactWithinActivity(activity, 130));

    }

    @Test
    public void testcheckTimeOfFactWithinActivity_FactTimeWithinDuration() throws Exception {
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

        Assertions.assertTrue(activityService.checkTimeOfFactWithinActivity(activity, 20));
    }


    @Test
    public void testingOverallTimeUserPlayedBasedOnSubs() throws Exception {
        Location location = new Location(null, null, null, "Christchurch", null,
                "New Zealand");
        Team team = new Team("Team 900", "Programming");
        User creator = new User("Test", "Account", "test123@test.com", "Password1!", location);
        User player = new User("sam", "Account", "sam@test.com", "Password1!", location);
        User player2 = new User("bob", "Account", "bob@test.com", "Password1!", location);

        player.joinTeam(team);
        player2.joinTeam(team);
        Activity activity = new Activity(ActivityType.Game, team, "Game with Team",
                LocalDateTime.of(2023, 1,1,6,30),
                LocalDateTime.of(2023, 1,1,8,30),
                creator,  new Location(null, null, null,
                "Christchurch", null, "New Zealand"));

        List<Fact> factList1 = new ArrayList<>(); //player2 time 60
        Substitution sub = new Substitution("Player was taken off, player2 on", "10", activity, player, player2);
        Substitution sub2 = new Substitution("second fact: Player2 was taken off, player on", "30", activity, player2, player);
        Substitution sub3 = new Substitution("third fact: Player was taken off, player2 on", "40", activity, player, player2);
        Substitution sub4 = new Substitution("fourth fact: Player2 was taken off, player on", "80", activity, player2, player);

        factRepository.save(sub);
        factRepository.save(sub2);
        factRepository.save(sub3);
        factRepository.save(sub4);

        factList1.add(sub);
        factList1.add(sub2);
        factList1.add(sub3);
        factList1.add(sub4);

        activity.addFactList(factList1);
        activityRepository.save(activity);

        Activity activity2 = new Activity(ActivityType.Friendly, team, "FRIENDLY with Team",
                LocalDateTime.of(2024, 1,1,6,30),
                LocalDateTime.of(2024, 1,1,8,30),
                creator,  new Location(null, null, null,
                "dunedin", null, "New Zealand"));

        List<Fact> factList2 = new ArrayList<>(); //player2 time 30
        Substitution subf = new Substitution("Player was taken off, player2 on", "10", activity, player, player2);
        Substitution subf2 = new Substitution("second fact: Player2 was taken off, player on", "30", activity, player2, player);
        Substitution subf3 = new Substitution("third fact: Player was taken off, player2 on", "40", activity, player, player2);
        Substitution subf4 = new Substitution("fourth fact: Player2 was taken off, player on", "50", activity, player2, player);

        factRepository.save(subf);
        factRepository.save(subf2);
        factRepository.save(subf3);
        factRepository.save(subf4);

        factList2.add(subf);
        factList2.add(subf2);
        factList2.add(subf3);
        factList2.add(subf4);

        activity2.addFactList(factList2);
        activityRepository.save(activity2);


        Assertions.assertEquals(90, activityService.getOverallPlayTimeForUserBasedOnSubs(player2, team));
    }

}
