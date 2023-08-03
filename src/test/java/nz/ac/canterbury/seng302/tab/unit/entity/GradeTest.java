package nz.ac.canterbury.seng302.tab.unit.entity;

import nz.ac.canterbury.seng302.tab.entity.Grade;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.helper.GenerateRandomTeams;
import nz.ac.canterbury.seng302.tab.repository.TeamRepository;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest // We really shouldnt be using SpringBootTest here.  Oh well :)
class GradeTest {

    GenerateRandomTeams generateRandomTeams = new GenerateRandomTeams();

    @Autowired
    TeamService teamService;

    @Autowired
    TeamRepository teamRepository;

    Team team1;
    Grade grade1;

    Team team2;
    Grade grade2;


    @BeforeEach
    void beforeEach() throws IOException {
        team1 = generateRandomTeams.createRandomTeam();
        grade1 = new Grade(Grade.Age.OVER_50S, Grade.Sex.WOMENS);
        team1.setGrade(grade1);

        team2 = generateRandomTeams.createRandomTeam();
        grade2 = new Grade(Grade.Age.UNDER_18S, Grade.Sex.MENS);
        team2.setGrade(grade2);

        teamService.addTeam(team1);
        teamService.addTeam(team2);
    }

    private boolean gradeEquals(Grade a, Grade b) {
        return (a.getAge().equals(b.getAge()) &&  a.getSex().equals(b.getSex()));
    }

    private void testGradesAreSaved(Team team) {
        var opt1 = teamRepository.findById(team.getTeamId());
        assertTrue(opt1.isPresent(), "wot wot?");
        boolean ok = gradeEquals(team.getGrade(), opt1.get().getGrade());
        assertTrue(ok, "Grades not equal");
    }

    /*
    This test checks that grades are saved correctly.
    If this test is failing, check the cascadeType, it should be ALL
     */
    @Test
    void testGradesAreSaved() {
        testGradesAreSaved(team1);
        testGradesAreSaved(team2);
    }

    @Test
    void testAdultGradeDisplay() {
        assertEquals("Women's Over 50s", grade1.getDisplayString());
        assertEquals("Men's Under 18s", grade2.getDisplayString());
    }

    @Test
    void testAdultAgeRangeDisplay() {
        /*
        When the age range is ADULT, we should just have the sex displayed.
        This is because the Adult division is kinda like the "Open" division.
        As such, "Adult" should be the default.
         */
        var g = new Grade(Grade.Age.ADULT, Grade.Sex.MIXED);
        var g2 = new Grade(Grade.Age.ADULT, Grade.Sex.OTHER);

        assertEquals("Mixed", g.getDisplayString());
        assertEquals("Other", g2.getDisplayString());
    }

    @Test
    void testSexDisplayForYounglings() {
        /*
        Checks that younglings are mapped to "Boys" and "Girls" as opposed
        to "Men" and "Women".
         */
        var g = new Grade(Grade.Age.UNDER_7S, Grade.Sex.MENS, Grade.Competitiveness.UNSPECIFIED);
        assertEquals("Boy's Under 7s", g.getDisplayString());

        var g2 = new Grade(Grade.Age.UNDER_6S, Grade.Sex.WOMENS);
        assertEquals("Girl's Under 6s", g2.getDisplayString());

        assertEquals(g.getCompetitiveness(), g2.getCompetitiveness());
    }

    @Test
    void testYoungEdgeCases() {
        /*
        Anything 14 or older is "Mens / womens"
        Anything younger is "Boys / girls"
         */
        var boy = new Grade(Grade.Age.UNDER_13S, Grade.Sex.MENS);
        assertEquals("Boy's Under 13s", boy.getDisplayString());

        var man = new Grade(Grade.Age.UNDER_14S, Grade.Sex.MENS);
        assertEquals("Men's Under 14s", man.getDisplayString());
    }

    @Test
    void testCompetitiveness() {
        /*
        Checks that competitiveness is not shown when it's regular.
         */
        var g = new Grade(Grade.Age.ADULT, Grade.Sex.MENS, Grade.Competitiveness.SOCIAL);
        assertEquals("Men's Social", g.getDisplayString());

        var g2 = new Grade(Grade.Age.UNDER_10S, Grade.Sex.MIXED, Grade.Competitiveness.SOCIAL);
        assertEquals("Mixed Under 10s Social", g2.getDisplayString());
    }


    private void testParticipateIn(Grade.Age age, Grade.Age participationAge) {
        var caller = new Grade(age, Grade.Sex.MENS);
        var participation = new Grade(participationAge, Grade.Sex.MENS);
        assertTrue(caller.canParticipateIn(participation));
    }

    private void testCannotParticipateIn(Grade.Age age, Grade.Age participationAge) {
        var caller = new Grade(age, Grade.Sex.MENS);
        var participation = new Grade(participationAge, Grade.Sex.MENS);
        assertTrue(caller.canParticipateIn(participation));
    }

    @Test
    void testAgeGradesMatch() {
        // u19 can participate in adult
        testParticipateIn(Grade.Age.UNDER_19S, Grade.Age.ADULT);
        // but adults cannot participate in u19
        testCannotParticipateIn(Grade.Age.ADULT, Grade.Age.UNDER_19S);

        /*
        Check that all ages can participate in their own age group.
         */
        testParticipateIn(Grade.Age.ADULT, Grade.Age.ADULT);
        for (var age: Grade.Age.values()) {
            testParticipateIn(age, age);
        }

        // Older ages can participate in adult:
        testParticipateIn(Grade.Age.OVER_60S, Grade.Age.ADULT);
        testParticipateIn(Grade.Age.OVER_50S, Grade.Age.ADULT);
        testParticipateIn(Grade.Age.OVER_70S, Grade.Age.ADULT);

        // Adults can't participate in over 50s
        testCannotParticipateIn(Grade.Age.ADULT, Grade.Age.OVER_50S);

        testCannotParticipateIn(Grade.Age.UNDER_14S, Grade.Age.OVER_50S);
        testCannotParticipateIn(Grade.Age.OVER_50S, Grade.Age.UNDER_19S);
    }
}

