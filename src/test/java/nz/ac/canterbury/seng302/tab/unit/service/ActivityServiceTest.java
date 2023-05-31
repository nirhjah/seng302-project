package nz.ac.canterbury.seng302.tab.unit.service;

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


    /**
     * Tests if both teams scores for an activity are of the same format with a hyphen, return true
     */
    @Test
    public void ifActivityScoreBothSameFormat_Hyphen_returnTrue() {
        List<String> activityScore = new ArrayList<>();
        activityScore.add("141-9");
        activityScore.add("94-3");
        Assertions.assertTrue(activityService.validateActivityScore(activityScore));
    }


    /**
     * Tests if both teams scores for an activity are of different format where only first score has a hyphen, return false
     */
    @Test
    public void ifActivityScoreBothDifferentFormat_FirstScoreHyphen_returnFalse() {
        List<String> activityScore = new ArrayList<>();
        activityScore.add("141-9");
        activityScore.add("94");
        Assertions.assertFalse(activityService.validateActivityScore(activityScore));
    }

    /**
     * Tests if both teams scores for an activity are of the same format with a number only, return true
     */
    @Test
    public void ifActivityScoreBothSameFormat_NumberOnly_returnTrue() {
        List<String> activityScore = new ArrayList<>();
        activityScore.add("141");
        activityScore.add("94");
        Assertions.assertTrue(activityService.validateActivityScore(activityScore));
    }

    /**
     * Tests if both teams scores for an activity are of different format where only first team has score number only, return false
     */
    @Test
    public void ifActivityScoreBothDifferentFormat_OneScoreNumberOnly_returnFalse() {
        List<String> activityScore = new ArrayList<>();
        activityScore.add("99");
        activityScore.add("94-23");
        Assertions.assertFalse(activityService.validateActivityScore(activityScore));
    }

}
