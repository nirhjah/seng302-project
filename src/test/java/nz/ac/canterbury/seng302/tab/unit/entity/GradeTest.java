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
        grade1 = new Grade(Grade.Age.OVER_50s, Grade.Sex.WOMENS);
        team1.setGrade(grade1);

        team2 = generateRandomTeams.createRandomTeam();
        grade2 = new Grade(Grade.Age.UNDER_7S, Grade.Sex.MENS);
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
        assertEquals(grade1.getDisplayString(), "Over 50s Women's");
        // TODO: We should change this to "Under 7s boys" as opposed to Mens
        assertEquals(grade2.getDisplayString(), "Under 7s Mens");
    }

    @Test
    public void testAdultAgeRange() {
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
}

