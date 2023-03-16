package nz.ac.canterbury.seng302.tab.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CreateTeamFormControllerTest {

    private static String teamNameUnicodeRegex;

    /** A sport can be letters, space, apostrophes or dashes **/
    private static String sportUnicodeRegex;

    /** A sport can be letters, space, apostrophes or dashes **/
    private static String locationUnicodeRegex;

    private static CreateTeamFormController createTeamFormController;

    @BeforeAll
    public static void setUp() {
        createTeamFormController = new CreateTeamFormController();
        teamNameUnicodeRegex = createTeamFormController.getTeamNameUnicodeRegex();
        sportUnicodeRegex = createTeamFormController.getSportUnicodeRegex();
        locationUnicodeRegex = createTeamFormController.getLocationUnicodeRegex();
    }

    @Test
    public void validTeamNameWithLetters() {
        String validName = "test";
        assertTrue(validName.matches(teamNameUnicodeRegex));
    }

    @Test
    public void validTeamNameWithNumbers() {
        String validNameWithNumbers = "123";
        assertTrue(validNameWithNumbers.matches(teamNameUnicodeRegex));
    }

    @Test
    public void validTeamNameWithCurlyBraces() {
        String validNameWithCurlyBraces = "{hello}";
        assertTrue(validNameWithCurlyBraces.matches(teamNameUnicodeRegex));
    }

    @Test
    public void validTeamNameWithDots() {
        String validNameWithDots = "hello.hello";
        assertTrue(validNameWithDots.matches(teamNameUnicodeRegex));
    }

    @Test
    public void invalidTeamNameWithDashes() {
        String invalid = "invalid-name";
        assertFalse(invalid.matches(teamNameUnicodeRegex));
    }

    @Test
    public void invalidTeamNameWithApostrophes() {
        String invalid = "invalid'name";
        assertFalse(invalid.matches(teamNameUnicodeRegex));
    }

    @Test
    public void invalidCharsTeamName() {
        String invalid = "!@#$%^&*()";
        assertFalse(invalid.matches(teamNameUnicodeRegex));
    }

    /** Sport Regex Test**/

    @Test
    public void validSportWithLetters() {
        String validName = "test";
        assertTrue(validName.matches(sportUnicodeRegex));
    }

    @Test
    public void invalidSportWithNumbers() {
        String validNameWithNumbers = "123";
        assertFalse(validNameWithNumbers.matches(sportUnicodeRegex));
    }

    @Test
    public void invalidSportWithCurlyBraces() {
        String validNameWithCurlyBraces = "{hello}";
        assertFalse(validNameWithCurlyBraces.matches(sportUnicodeRegex));
    }

    @Test
    public void invalidSportWithDots() {
        String dots = "hello.hello";
        assertFalse(dots.matches(sportUnicodeRegex));
    }

    @Test
    public void validSportWithDashes() {
        String valid = "invalid-name";
        assertTrue(valid.matches(sportUnicodeRegex));
    }

    @Test
    public void validSportWithApostrophes() {
        String valid = "invalid'name";
        assertTrue(valid.matches(sportUnicodeRegex));
    }

    @Test
    public void validSportWithSpaces() {
        String valid = "invalid name";
        assertTrue(valid.matches(sportUnicodeRegex));
    }

    @Test
    public void invalidCharsSport() {
        String invalid = "!@#$%^&*()";
        assertFalse(invalid.matches(sportUnicodeRegex));
    }

    /** Location Regex Test**/

    @Test
    public void validLocationWithLetters() {
        String validName = "test";
        assertTrue(validName.matches(locationUnicodeRegex));
    }

    @Test
    public void invalidLocationWithNumbers() {
        String validNameWithNumbers = "123";
        assertFalse(validNameWithNumbers.matches(locationUnicodeRegex));
    }

    @Test
    public void invalidLocationWithCurlyBraces() {
        String validNameWithCurlyBraces = "{hello}";
        assertFalse(validNameWithCurlyBraces.matches(locationUnicodeRegex));
    }

    @Test
    public void invalidLocationWithDots() {
        String dots = "hello.hello";
        assertFalse(dots.matches(locationUnicodeRegex));
    }

    @Test
    public void validLocationWithDashes() {
        String valid = "invalid-name";
        assertTrue(valid.matches(locationUnicodeRegex));
    }

    @Test
    public void validLocationWithApostrophes() {
        String valid = "invalid'name";
        assertTrue(valid.matches(locationUnicodeRegex));
    }

    @Test
    public void validLocationWithSpaces() {
        String valid = "invalid name";
        assertTrue(valid.matches(locationUnicodeRegex));
    }

    @Test
    public void invalidCharsLocation() {
        String invalid = "!@#$%^&*()";
        assertFalse(invalid.matches(locationUnicodeRegex));
    }
}
