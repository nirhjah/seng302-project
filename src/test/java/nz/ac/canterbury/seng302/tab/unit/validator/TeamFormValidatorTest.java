package nz.ac.canterbury.seng302.tab.unit.validator;

import nz.ac.canterbury.seng302.tab.validator.TeamFormValidators;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TeamFormValidatorTest {

    @Test
    public void teamName_onlyAcceptableSpecialChar_regexShouldReject() {
        Assertions.assertFalse("{}".matches(TeamFormValidators.VALID_TEAM_NAME_REGEX));
    }

    @Test
    public void teamName_specialCharAndAlphaNumeric_regexShouldAccept() {
        Assertions.assertTrue("{team}".matches(TeamFormValidators.VALID_TEAM_NAME_REGEX));
    }

    @Test
    public void teamName_specialCharAndSpaceAndAlphaNumeric_regexShouldAccept() {
        Assertions.assertTrue("{team }".matches(TeamFormValidators.VALID_TEAM_NAME_REGEX));
    }

    @Test
    public void teamName_specialCharAndSpace_regexShouldReject() {
        Assertions.assertFalse("{ }".matches(TeamFormValidators.VALID_TEAM_NAME_REGEX));
    }

    @Test
    public void teamName_onlySpace_regexShouldReject() {
        Assertions.assertFalse(" ".matches(TeamFormValidators.VALID_TEAM_NAME_REGEX));
    }
    @Test
    public void teamName_onlyDot_regexShouldReject() {
        Assertions.assertFalse(".".matches(TeamFormValidators.VALID_TEAM_NAME_REGEX));
    }

    @Test
    public void teamName_allSpecialChar_regexShouldReject() {
        Assertions.assertFalse(". { }".matches(TeamFormValidators.VALID_TEAM_NAME_REGEX));
    }

    @Test
    public void teamName_allSpecialCharAndAlphanumeric_regexShouldAccept() {
        Assertions.assertTrue("a.b {4}".matches(TeamFormValidators.VALID_TEAM_NAME_REGEX));
    }

    @Test
    public void sportName_spaceAndAlphaNumeric_regexShouldAccept() {
        Assertions.assertTrue("a b".matches(TeamFormValidators.VALID_TEAM_SPORT_REGEX));
    }

    @Test
    public void sportName_allAlphaNumeric_regexShouldAccept() {
        Assertions.assertTrue("abcxyz".matches(TeamFormValidators.VALID_TEAM_SPORT_REGEX));
    }

    @Test
    public void sportName_allAlphabetAndHyphen_regexShouldAccept() {
        Assertions.assertTrue("abc-xyz".matches(TeamFormValidators.VALID_TEAM_SPORT_REGEX));
    }

    @Test
    public void sportName_allAlphabetAndApostrophe_regexShouldAccept() {
        Assertions.assertTrue("abc'xyz".matches(TeamFormValidators.VALID_TEAM_SPORT_REGEX));
    }

    @Test
    public void sportName_hasNumbers_regexShouldReject() {
        Assertions.assertFalse("a1".matches(TeamFormValidators.VALID_TEAM_SPORT_REGEX));
    }
}
