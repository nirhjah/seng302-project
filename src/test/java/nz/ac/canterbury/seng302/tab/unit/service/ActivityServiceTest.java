package nz.ac.canterbury.seng302.tab.unit.service;

import nz.ac.canterbury.seng302.tab.entity.*;
import nz.ac.canterbury.seng302.tab.entity.Fact.Fact;
import nz.ac.canterbury.seng302.tab.entity.Fact.Goal;
import nz.ac.canterbury.seng302.tab.entity.Fact.Substitution;
import nz.ac.canterbury.seng302.tab.entity.lineUp.LineUp;
import nz.ac.canterbury.seng302.tab.entity.lineUp.LineUpPosition;
import nz.ac.canterbury.seng302.tab.enums.*;
import nz.ac.canterbury.seng302.tab.repository.*;
import nz.ac.canterbury.seng302.tab.service.ActivityService;
import nz.ac.canterbury.seng302.tab.service.FactService;
import nz.ac.canterbury.seng302.tab.service.LineUpPositionService;
import nz.ac.canterbury.seng302.tab.service.LineUpService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DataJpaTest
@Import({ActivityService.class, FactService.class, LineUpService.class, LineUpPositionService.class})
public class ActivityServiceTest {

    @Autowired
    ActivityService activityService;

    @Autowired
    ActivityRepository activityRepository;

    @Autowired
    LineUpRepository lineUpRepository;

    @Autowired
    LineUpPositionRepository lineUpPositionRepository;

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    FormationRepository formationRepository;

    @Autowired
    LineUpPositionService lineUpPositionService;

    @Autowired
    LineUpService lineUpService;

    @Autowired
    FactService factService;

    @Autowired
    FactRepository factRepository;

    @Autowired
    UserRepository userRepository;

    private User player;

    private User player2;

    private User player3;

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
    void testingOverallTimeUserPlayedBasedOnSubs() throws Exception {
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

        List<Fact> factList1 = new ArrayList<>();
        Substitution sub = new Substitution("Player was taken off, player2 on", "10", activity, player, player2); //
        Substitution sub2 = new Substitution("second fact: Player2 was taken off, player on", "30", activity, player2, player); // +20
        Substitution sub3 = new Substitution("third fact: Player was taken off, player2 on", "40", activity, player, player2);
        Substitution sub4 = new Substitution("fourth fact: Player2 was taken off, player on", "80", activity, player2, player); // +40 = 60

        Substitution sub5 = new Substitution("fourth fact: Player was taken off, player2 on", "100", activity, player, player2); // +20 ? = 80

        factRepository.save(sub);
        factRepository.save(sub2);
        factRepository.save(sub3);
        factRepository.save(sub4);
        factRepository.save(sub5);

        factList1.add(sub);
        factList1.add(sub2);
        factList1.add(sub3);
        factList1.add(sub4);
        factList1.add(sub5);


        Activity activity2 = new Activity(ActivityType.Friendly, team, "FRIENDLY with Team",
                LocalDateTime.of(2024, 1,1,6,30),
                LocalDateTime.of(2024, 1,1,8,30),
                creator,  new Location(null, null, null,
                "dunedin", null, "New Zealand"));

        List<Fact> factList2 = new ArrayList<>();
        Substitution subf = new Substitution("Player2 was taken off 10 mins in, player on", "10", activity, player2, player); // +10 = 70 = 90
        Substitution subf2 = new Substitution("second fact: Player was taken off, player2 on 30 mins in", "30", activity, player, player2);
        Substitution subf3 = new Substitution("third fact: Player2 was taken off after 40 mins, player on", "40", activity, player2, player); // +10 = 80 100
        Substitution subf4 = new Substitution("fourth fact: Player was taken off 50 mins in, player2 on", "50", activity, player, player2); // + 70 (120-50) = 150

        factRepository.save(subf);
        factRepository.save(subf2);
        factRepository.save(subf3);
           factRepository.save(subf4);

        factList2.add(subf);
        factList2.add(subf2);
        factList2.add(subf3);
        factList2.add(subf4);
        activity2.addFactList(factList2);

        Formation formation = new Formation("1", team);
        formationRepository.save(formation);
        activity.setFormation(formation);
        activity2.setFormation(formation);
        activity.addFactList(factList1);
        activityRepository.save(activity);
        activityRepository.save(activity2);


        LineUp activityLineUp = new LineUp(activity.getFormation().get(), team, activity);
        LineUp activity2LineUp = new LineUp(activity2.getFormation().get(), team, activity2);

        lineUpRepository.save(activityLineUp);
        lineUpRepository.save(activity2LineUp);

        LineUpPosition lineUpPosition = new LineUpPosition(activityLineUp, player, 1);
        LineUpPosition lineUpPosition2 = new LineUpPosition(activity2LineUp, player2, 1);

        lineUpPositionRepository.save(lineUpPosition);
        lineUpPositionRepository.save(lineUpPosition2);

        Assertions.assertEquals(170, activityService.getOverallPlayTimeForUserBasedOnSubs(player2, team));
    }


    @Test
    void testGetTotalGamesUserPlayed() throws Exception {
        Location location = new Location(null, null, null, "Christchurch", null,
                "New Zealand");
        Team team = new Team("Team 900", "Programming");
        User creator = new User("Test", "Account", "test123@test.com", "Password1!", location);
        User player = new User("sam", "Account", "sam@test.com", "Password1!", location);
        player.joinTeam(team);
        Activity game = new Activity(ActivityType.Game, team, "Game with Team",
                LocalDateTime.of(2023, 1,1,6,30),
                LocalDateTime.of(2023, 1,1,8,30),
                creator,  new Location(null, null, null,
                "Christchurch", null, "New Zealand"));
        Activity friendly = new Activity(ActivityType.Friendly, team, "Friendly with Team",
                LocalDateTime.of(2023, 1,2,6,30),
                LocalDateTime.of(2023, 1,2,7,30),
                creator,  new Location(null, null, null,
                "Christchurch", null, "New Zealand"));

        List<Fact> factList1 = new ArrayList<>();
        Substitution sub = new Substitution("Player was taken off, player2 on", "10", game, player, creator);
        Substitution sub2 = new Substitution("second fact: Player2 was taken off, player on", "30", game, creator, player);
        factRepository.save(sub);
        factRepository.save(sub2);
        factList1.add(sub);
        factList1.add(sub2);
        game.addFactList(factList1);

        List<Fact> factList2 = new ArrayList<>();
        Substitution subf1 = new Substitution("Player was taken off, player2 on", "10", friendly, player, creator);
        Substitution subf2 = new Substitution("second fact: Player2 was taken off, player on", "30", friendly, creator, player);
        factRepository.save(subf1);
        factRepository.save(subf2);
        factList2.add(subf1);
        factList2.add(subf2);
        friendly.addFactList(factList2);

        Formation formation = new Formation("1", team);
        formationRepository.save(formation);
        game.setFormation(formation);
        friendly.setFormation(formation);
        activityRepository.save(game);
        activityRepository.save(friendly);

        LineUp activityLineUp = new LineUp(game.getFormation().get(), team, game);
        lineUpRepository.save(activityLineUp);
        LineUpPosition lineUpPosition = new LineUpPosition(activityLineUp, player, 1);
        lineUpPositionRepository.save(lineUpPosition);

        LineUp activityLineUp2 = new LineUp(friendly.getFormation().get(), team, friendly);
        lineUpRepository.save(activityLineUp2);
        LineUpPosition lineUpPosition2 = new LineUpPosition(activityLineUp2, player, 1);
        lineUpPositionRepository.save(lineUpPosition2);

        Assertions.assertEquals(2, activityService.getTotalGamesUserPlayed(player, team));
    }

    @Test
    void testGettingSubFactsUserIsIn() throws Exception {
        Location location = new Location(null, null, null, "Christchurch", null,
                "New Zealand");
        Team team = new Team("Team 900", "Programming");
        User creator = new User("Test", "Account", "test123@test.com", "Password1!", location);
        User player = new User("sam", "Account", "sam@test.com", "Password1!", location);
        User player2 = new User("bob", "Account", "bob@test.com", "Password1!", location);
        User player3 = new User("john", "Account", "john@test.com", "Password1!", location);

        player.joinTeam(team);
        player2.joinTeam(team);
        player3.joinTeam(team);

        Activity game = new Activity(ActivityType.Game, team, "Game with Team",
                LocalDateTime.of(2023, 1,1,6,30),
                LocalDateTime.of(2023, 1,1,8,30),
                creator,  new Location(null, null, null,
                "Christchurch", null, "New Zealand"));
        Activity friendly = new Activity(ActivityType.Friendly, team, "Friendly with Team",
                LocalDateTime.of(2023, 1,2,6,30),
                LocalDateTime.of(2023, 1,2,7,30),
                creator,  new Location(null, null, null,
                "Christchurch", null, "New Zealand"));

        List<Fact> factList1 = new ArrayList<>();
        Substitution sub = new Substitution("test", "10", game, player, creator);
        Substitution sub2 = new Substitution("test", "30", game, player2, player);
        Substitution sub3 = new Substitution("test", "40", game, creator, player3);
        Substitution sub4 = new Substitution("test", "50", game, player, player2);

        factRepository.save(sub);
        factRepository.save(sub2);
        factRepository.save(sub3);
        factRepository.save(sub4);

        factList1.add(sub);
        factList1.add(sub2);
        factList1.add(sub3);
        factList1.add(sub4);

        game.addFactList(factList1);

        activityRepository.save(game);

        List<Fact> expectedSubList = new ArrayList<>();
        expectedSubList.add(sub);
        expectedSubList.add(sub2);
        expectedSubList.add(sub4);


        Assertions.assertEquals(expectedSubList, activityService.subFactsUserIsIn(game, player));

    }

    @Test
    void testGettingAveragePlaytimeForUser() throws Exception {
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

        List<Fact> factList1 = new ArrayList<>();
        Substitution sub = new Substitution("Player was taken off, player2 on", "10", activity, player, player2);
        Substitution sub2 = new Substitution("second fact: Player2 was taken off, player on", "30", activity, player2, player);
        Substitution sub3 = new Substitution("third fact: Player was taken off, player2 on", "40", activity, player, player2);
        Substitution sub4 = new Substitution("fourth fact: Player2 was taken off, player on", "80", activity, player2, player);

        Substitution sub5 = new Substitution("fourth fact: Player was taken off, player2 on", "100", activity, player, player2);

        factRepository.save(sub);
        factRepository.save(sub2);
        factRepository.save(sub3);
        factRepository.save(sub4);
        factRepository.save(sub5);

        factList1.add(sub);
        factList1.add(sub2);
        factList1.add(sub3);
        factList1.add(sub4);
        factList1.add(sub5);


        Activity activity2 = new Activity(ActivityType.Friendly, team, "FRIENDLY with Team",
                LocalDateTime.of(2024, 1,1,6,30),
                LocalDateTime.of(2024, 1,1,8,30),
                creator,  new Location(null, null, null,
                "dunedin", null, "New Zealand"));

        List<Fact> factList2 = new ArrayList<>();
        Substitution subf = new Substitution("Player2 was taken off 10 mins in, player on", "10", activity, player2, player);
        Substitution subf2 = new Substitution("second fact: Player was taken off, player2 on 30 mins in", "30", activity, player, player2);
        Substitution subf3 = new Substitution("third fact: Player2 was taken off after 40 mins, player on", "40", activity, player2, player);
        Substitution subf4 = new Substitution("fourth fact: Player was taken off 50 mins in, player2 on", "50", activity, player, player2);

        factRepository.save(subf);
        factRepository.save(subf2);
        factRepository.save(subf3);
        factRepository.save(subf4);

        factList2.add(subf);
        factList2.add(subf2);
        factList2.add(subf3);
        factList2.add(subf4);
        activity2.addFactList(factList2);

        Formation formation = new Formation("1", team);
        formationRepository.save(formation);
        activity.setFormation(formation);
        activity2.setFormation(formation);
        activity.addFactList(factList1);
        activityRepository.save(activity);
        activityRepository.save(activity2);


        LineUp activityLineUp = new LineUp(activity.getFormation().get(), team, activity);
        LineUp activity2LineUp = new LineUp(activity2.getFormation().get(), team, activity2);

        lineUpRepository.save(activityLineUp);
        lineUpRepository.save(activity2LineUp);

        LineUpPosition lineUpPosition = new LineUpPosition(activityLineUp, player, 1);
        LineUpPosition lineUpPosition2 = new LineUpPosition(activity2LineUp, player2, 1);

        lineUpPositionRepository.save(lineUpPosition);
        lineUpPositionRepository.save(lineUpPosition2);

        Assertions.assertEquals(85, activityService.getAveragePlayTime(player2, team));

    }


    @Test
    void testGettingPlayersInLineupForActivity() throws Exception {

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

        List<Fact> factList1 = new ArrayList<>();
        Substitution sub = new Substitution("Player was taken off, player2 on", "10", activity, player, player2); //
        Substitution sub2 = new Substitution("second fact: Player2 was taken off, player on", "30", activity, player2, player); // +20
        Substitution sub3 = new Substitution("third fact: Player was taken off, player2 on", "40", activity, player, player2);
        Substitution sub4 = new Substitution("fourth fact: Player2 was taken off, player on", "80", activity, player2, player); // +40

        Substitution sub5 = new Substitution("fourth fact: Player was taken off, player2 on", "100", activity, player, player2); // +20 ?

        factRepository.save(sub);
        factRepository.save(sub2);
        factRepository.save(sub3);
        factRepository.save(sub4);
        factRepository.save(sub5);

        factList1.add(sub);
        factList1.add(sub2);
        factList1.add(sub3);
        factList1.add(sub4);
        factList1.add(sub5);


        Activity activity2 = new Activity(ActivityType.Friendly, team, "FRIENDLY with Team",
                LocalDateTime.of(2024, 1,1,6,30),
                LocalDateTime.of(2024, 1,1,8,30),
                creator,  new Location(null, null, null,
                "dunedin", null, "New Zealand"));

        List<Fact> factList2 = new ArrayList<>();
        Substitution subf = new Substitution("Player2 was taken off 10 mins in, player on", "10", activity, player2, player); // +10 = 70
        Substitution subf2 = new Substitution("second fact: Player was taken off, player2 on 30 mins in", "30", activity, player, player2);
        Substitution subf3 = new Substitution("third fact: Player2 was taken off after 40 mins, player on", "40", activity, player2, player); // +10 = 80
        Substitution subf4 = new Substitution("fourth fact: Player was taken off 50 mins in, player2 on", "50", activity, player, player2); // + 70 (120-50) = 150

        factRepository.save(subf);
        factRepository.save(subf2);
        factRepository.save(subf3);
        factRepository.save(subf4);

        factList2.add(subf);
        factList2.add(subf2);
        factList2.add(subf3);
        factList2.add(subf4);
        activity2.addFactList(factList2);

        Formation formation = new Formation("1", team);
        formationRepository.save(formation);
        activity.setFormation(formation);
        activity2.setFormation(formation);
        activity.addFactList(factList1);
        activityRepository.save(activity);
        activityRepository.save(activity2);


        LineUp activityLineUp = new LineUp(activity.getFormation().get(), team, activity);

        lineUpRepository.save(activityLineUp);

        LineUpPosition lineUpPosition = new LineUpPosition(activityLineUp, player, 1);
        LineUpPosition lineUpPosition2 = new LineUpPosition(activityLineUp, player2, 1);

        lineUpPositionRepository.save(lineUpPosition);
        lineUpPositionRepository.save(lineUpPosition2);

        List<User> expectedUserList = new ArrayList<>();
        expectedUserList.add(player);
        expectedUserList.add(player2);

        Assertions.assertEquals(expectedUserList, activityService.playersInLineUpForActivity(activity));
    }


    @Test
    void testGettingTop5UsersWithPlayTimeAndAverageInTeam() throws Exception {

        Location location = new Location(null, null, null, "Christchurch", null,
                "New Zealand");
        Team team = new Team("Team 900", "Programming");
        User creator = new User("Test", "Account", "test123@test.com", "Password1!", location);
        User player = new User("sam", "Account", "sam@test.com", "Password1!", location);
        User player2 = new User("bob", "Account", "bob@test.com", "Password1!", location);
        User playerNoFacts = new User("john", "Account", "john@test.com", "Password1!", location);

        player.joinTeam(team);
        player2.joinTeam(team);
        playerNoFacts.joinTeam(team);

        Activity activity = new Activity(ActivityType.Game, team, "Game with Team",
                LocalDateTime.of(2023, 1,1,6,30),
                LocalDateTime.of(2023, 1,1,8,30),
                creator,  new Location(null, null, null,
                "Christchurch", null, "New Zealand"));

        List<Fact> factList1 = new ArrayList<>();
        Substitution sub = new Substitution("Player was taken off, player2 on", "10", activity, player, player2); //
        Substitution sub2 = new Substitution("second fact: Player2 was taken off, player on", "30", activity, player2, player); // +20
        Substitution sub3 = new Substitution("third fact: Player was taken off, player2 on", "40", activity, player, player2);
        Substitution sub4 = new Substitution("fourth fact: Player2 was taken off, player on", "80", activity, player2, player); // +40

        Substitution sub5 = new Substitution("fourth fact: Player was taken off, player2 on", "100", activity, player, player2); // +20 ?

        factRepository.save(sub);
        factRepository.save(sub2);
        factRepository.save(sub3);
        factRepository.save(sub4);
        factRepository.save(sub5);

        factList1.add(sub);
        factList1.add(sub2);
        factList1.add(sub3);
        factList1.add(sub4);
        factList1.add(sub5);



        Formation formation = new Formation("2", team);
        formationRepository.save(formation);
        activity.setFormation(formation);
        activity.addFactList(factList1);
        activityRepository.save(activity);


        LineUp activityLineUp = new LineUp(activity.getFormation().get(), team, activity);

        lineUpRepository.save(activityLineUp);

        LineUpPosition lineUpPosition = new LineUpPosition(activityLineUp, player, 1);

        lineUpPositionRepository.save(lineUpPosition);

        Map<User, List<Long>> playtimeAndAvgPlaytimeTop5Expected = new HashMap<>();

        List<Long> playtimeAndAvgPlaytimePlayerExpected = new ArrayList<>();

        playtimeAndAvgPlaytimePlayerExpected.add(40L);
        playtimeAndAvgPlaytimePlayerExpected.add(40L);

        playtimeAndAvgPlaytimeTop5Expected.put(player, playtimeAndAvgPlaytimePlayerExpected);

        Assertions.assertEquals(playtimeAndAvgPlaytimeTop5Expected, activityService.top5UsersWithPlayTimeAndAverageInTeam(team));


    }

    /**
     * sets up the actvity with a lineup and a user in the lineup
     * @return the actvity to be tested against for the substitution tests
     * @throws Exception
    */
    private Activity setUpSubTests() throws Exception {
        Location location = new Location(null, null, null, "Christchurch", null,
                "New Zealand");
        Team team = new Team("Team 900", "Programming");
        User creator = new User("Test", "Account", "test123@test.com", "Password1!", location);
        player = new User("sam", "Account", "sam@test.com", "Password1!", location);
        player2 = new User("bob", "Account", "bob@test.com", "Password1!", location);
        player3 = new User("john", "Account", "john@test.com", "Password1!", location);

        player.joinTeam(team);
        player2.joinTeam(team);
        player3.joinTeam(team);

        teamRepository.save(team);

        Activity activity = new Activity(ActivityType.Game, team, "Game with Team",
                LocalDateTime.of(2023, 1,1,6,30),
                LocalDateTime.of(2023, 1,1,8,30),
                creator,  new Location(null, null, null,
                "Christchurch", null, "New Zealand"));

        Formation formation = new Formation("2", team);
        formationRepository.save(formation);
        activity.setFormation(formation);
        activityRepository.save(activity);


        LineUp activityLineUp = new LineUp(activity.getFormation().get(), team, activity);

        lineUpRepository.save(activityLineUp);

        LineUpPosition lineUpPosition = new LineUpPosition(activityLineUp, player, 1);


        lineUpPositionRepository.save(lineUpPosition);

        return activity;
    }

    @Test
    void testGettingAllPlayersThatAreCurrentlyPlayingWithNoSubs() throws Exception {
        Activity activity = setUpSubTests();
        List<User> currPlaying = activityService.getAllPlayersCurrentlyPlaying(activity.getId());

        Assertions.assertEquals(1, currPlaying.size());
        Assertions.assertEquals(currPlaying.get(0), player);
    }


    @Test
    void testGettingAllPlayersThatAreCurrentlyPlayingWithSubs() throws Exception {
        Activity activity = setUpSubTests();
        Substitution sub = new Substitution("subbing player off", "1", activity, player, player2);
        factRepository.save(sub);
        List<User> currPlaying = activityService.getAllPlayersCurrentlyPlaying(activity.getId());

        Assertions.assertEquals(1, currPlaying.size());
        Assertions.assertEquals(currPlaying.get(0), player2);
    }

    @Test
    void testGettingAllPlayersThatAreCurrentlyPlayingWithSubOffAndOn() throws Exception {
        Activity activity = setUpSubTests();
        Substitution sub = new Substitution("subbing player off", "1", activity, player, player2);
        Substitution sub2 = new Substitution("subbing player on again", "1", activity, player2, player);
        factRepository.save(sub);
        factRepository.save(sub2);
        List<User> currPlaying = activityService.getAllPlayersCurrentlyPlaying(activity.getId());

        Assertions.assertEquals(1, currPlaying.size());
        Assertions.assertEquals(currPlaying.get(0), player);
    }

    @Test
    void testGettingTeamActivities_withPersonalActivities() throws Exception {
        Team t = new Team("Test Team", "Hockey");
        teamRepository.save(t);
        User user = new User("Test", "Account", "tab.team900@gmail.com", "password", new Location("1 Place", "B", "Ilam", "CHCH", "808", "NZ"));
        userRepository.save(user);
        t.setMember(user);
        teamRepository.save(t);
        Activity game = new Activity(ActivityType.Game, t, "A Test Game",
                LocalDateTime.of(2024, 1,1,6,30),
                LocalDateTime.of(2024, 1,1,8,30), user,
                new Location("Test", "Test", "Test", "test", "Tst", "test"));
        Activity training = new Activity(ActivityType.Training, null, "A Test Game",
                LocalDateTime.of(2024, 1,1,6,30),
                LocalDateTime.of(2024, 1,1,8,30), user,
                new Location("Test", "Test", "Test", "test", "Tst", "test"));
        activityRepository.save(game);
        activityRepository.save(training);

        List<Activity> expectedFutureActivities = List.of(game);
        Assertions.assertEquals(expectedFutureActivities, activityService.getAllFutureTeamActivitiesForUser(user));
    }

    @Test
    void testGettingPersonalActivities_withTeamActivities() throws Exception {
        Team t = new Team("Test Team", "Hockey");
        teamRepository.save(t);
        User user = new User("Test", "Account", "tab.team900@gmail.com", "password", new Location("1 Place", "B", "Ilam", "CHCH", "808", "NZ"));
        userRepository.save(user);
        t.setMember(user);
        teamRepository.save(t);
        Activity game = new Activity(ActivityType.Game, t, "A Test Game",
                LocalDateTime.of(2024, 1,1,6,30),
                LocalDateTime.of(2024, 1,1,8,30), user,
                new Location("Test", "Test", "Test", "test", "Tst", "test"));
        Activity training = new Activity(ActivityType.Training, null, "A Test Game",
                LocalDateTime.of(2024, 1,1,6,30),
                LocalDateTime.of(2024, 1,1,8,30), user,
                new Location("Test", "Test", "Test", "test", "Tst", "test"));
        activityRepository.save(game);
        activityRepository.save(training);

        List<Activity> expectedFutureActivities = List.of(training);
        Assertions.assertEquals(expectedFutureActivities, activityService.getAllFuturePersonalActivitiesForUser(user));
    }

    @Test
    void testGettingFuturePersonalActivities_OnlyPastPersonalActivities() throws Exception {
        Team t = new Team("Test Team", "Hockey");
        teamRepository.save(t);
        User user = new User("Test", "Account", "tab.team900@gmail.com", "password", new Location("1 Place", "B", "Ilam", "CHCH", "808", "NZ"));
        userRepository.save(user);
        t.setMember(user);
        teamRepository.save(t);

        Activity training = new Activity(ActivityType.Training, null, "training",
                LocalDateTime.of(2021, 1,1,6,30),
                LocalDateTime.of(2021, 1,1,8,30), user,
                new Location("Test", "Test", "Test", "test", "Tst", "test"));

        Activity other = new Activity(ActivityType.Other, null, "other",
                LocalDateTime.of(2021, 1,2,6,30),
                LocalDateTime.of(2021, 1,2,8,30), user,
                new Location("Test", "Test", "Test", "test", "Tst", "test"));

        activityRepository.save(other);
        activityRepository.save(training);

        List<Activity> expectedFutureActivities = List.of();
        Assertions.assertEquals(expectedFutureActivities, activityService.getAllFuturePersonalActivitiesForUser(user));
    }

    @Test
    void testGettingFutureTeamActivities_OnlyPastTeamActivities() throws Exception {
        Team t = new Team("Test Team", "Hockey");
        teamRepository.save(t);
        User user = new User("Test", "Account", "tab.team900@gmail.com", "password", new Location("1 Place", "B", "Ilam", "CHCH", "808", "NZ"));
        userRepository.save(user);
        t.setMember(user);
        teamRepository.save(t);

        Activity game = new Activity(ActivityType.Game, t, "game",
                LocalDateTime.of(2021, 1,1,6,30),
                LocalDateTime.of(2021, 1,1,8,30), user,
                new Location("Test", "Test", "Test", "test", "Tst", "test"));

        Activity friendly = new Activity(ActivityType.Friendly, null, "friendly",
                LocalDateTime.of(2021, 1,2,6,30),
                LocalDateTime.of(2021, 1,2,8,30), user,
                new Location("Test", "Test", "Test", "test", "Tst", "test"));

        activityRepository.save(game);
        activityRepository.save(friendly);

        List<Activity> expectedFutureActivities = List.of();
        Assertions.assertEquals(expectedFutureActivities, activityService.getAllFutureTeamActivitiesForUser(user));
    }


}
