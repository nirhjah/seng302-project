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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    public void beforeEach() throws IOException {
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
    public void testGradesAreSaved() {
        testGradesAreSaved(team1);
        testGradesAreSaved(team2);
    }

    @Test
    public void testAdultGradeDisplay() {
        assertEquals(grade1.getDisplayString(), "Women's Over 50s");
        assertEquals(grade2.getDisplayString(), "Men's Under 18s");
    }

    @Test
    public void testAdultAgeRangeDisplay() {
        /*
        When the age range is ADULT, we should just have the sex displayed.
        This is because the Adult division is kinda like the "Open" division.
        As such, "Adult" should be the default.
         */
        var g = new Grade(Grade.Age.ADULT, Grade.Sex.MIXED);
        var g2 = new Grade(Grade.Age.ADULT, Grade.Sex.OTHER);

        assertEquals(g.getDisplayString(), "Mixed");
        assertEquals(g2.getDisplayString(), "Other");
    }

    @Test
    public void testSexDisplayForYounglings() {
        /*
        Checks that younglings are mapped to "Boys" and "Girls" as opposed
        to "Men" and "Women".
         */
        var g = new Grade(Grade.Age.UNDER_7S, Grade.Sex.MENS, Grade.Competitiveness.UNSPECIFIED);
        assertEquals(g.getDisplayString(), "Boy's Under 7s");

        var g2 = new Grade(Grade.Age.UNDER_6S, Grade.Sex.WOMENS);
        assertEquals(g2.getDisplayString(), "Girl's Under 6s");

        assertEquals(g.getCompetitiveness(), g2.getCompetitiveness());
    }

    @Test
    public void testYoungEdgeCases() {
        /*
        Anything 14 or older is "Mens / womens"
        Anything younger is "Boys / girls"
         */
        var boy = new Grade(Grade.Age.UNDER_13S, Grade.Sex.MENS);
        assertEquals(boy.getDisplayString(), "Boy's Under 13s");

        var man = new Grade(Grade.Age.UNDER_14S, Grade.Sex.MENS);
        assertEquals(man.getDisplayString(), "Men's Under 14s");
    }

    @Test
    public void testCompetitiveness() {
        /*
        Checks that competitiveness is not shown when it's regular.
         */
        var g = new Grade(Grade.Age.ADULT, Grade.Sex.MENS, Grade.Competitiveness.SOCIAL);
        assertEquals(g.getDisplayString(), "Men's Social");

        var g2 = new Grade(Grade.Age.UNDER_10S, Grade.Sex.MIXED, Grade.Competitiveness.SOCIAL);
        assertEquals(g2.getDisplayString(), "Mixed Under 10s Social");
    }
}

