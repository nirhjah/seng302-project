package nz.ac.canterbury.seng302.tab.validator.logic;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import nz.ac.canterbury.seng302.tab.validator.ActivityFormValidators;

import java.util.List;
import java.util.Objects;


/**

Positions are inputted as a list of strings from the frontend.
 Each string represents a single player's position.
 Example:

1 12893845438  <-- player ID
2 89548734745
2 X     <-- an X represents no player.

 an `X` marks the existence of a player.
 no X means that there is no player.

 If there are
So this validator should fail.

 */
public class LineupStringCheck implements ConstraintValidator<ActivityFormValidators.playerPositionValidator, List<String>> {

    private boolean isPositionValid(String string) {
        String[] split = string.split(" ");
        if (split.length < 2) {
            return false;
        }
        return Objects.equals(split[1], "X");
    }

    @Override
    public boolean isValid(List<String> inputs, ConstraintValidatorContext context) {
        for (String each: inputs) {
            if (!isPositionValid(each)) {

            }
        }
    }
}
