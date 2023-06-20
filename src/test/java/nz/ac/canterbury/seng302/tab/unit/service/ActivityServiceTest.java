package nz.ac.canterbury.seng302.tab.unit.service;

import nz.ac.canterbury.seng302.tab.service.ActivityService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

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

}
