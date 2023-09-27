package nz.ac.canterbury.seng302.tab.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import nz.ac.canterbury.seng302.tab.validator.logic.ActivityDescriptionCheck;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

public class ActivityFormValidators {

    public static final String ACTIVITY_TYPE_MSG = "Activity type is required";

    public static final String TEAM_REQUIRED_MSG = "A team is required for this activity type";

    public static final String DESC_TOO_LONG_MSG = "Description must be 150 characters or less";

    public static final String NO_DESC_MSG = "Description is required";

    public static final String DESC_CONTAINS_INVALID_CHAR_MSG = "Description must contain at least 1 letter";

    public static final String START_DATE_REQUIRED_MSG = "Start date and time is required";

    public static final String END_DATE_REQUIRED_MSG = "End date and time is required";

    public static final String END_BEFORE_START_MSG = "Start date and time must be before end date and time";

    public static final String ACTIVITY_BEFORE_TEAM_CREATION = "Activity must be after creation date of team: ";

    public static final String NOT_A_COACH_OR_MANAGER = "You need to be a coach or a manager to do this";

    public static final String FORMATION_DOES_NOT_EXIST_MSG = "The specified team does not have this formation";

    // U27 AC7 - An invalid description is "...made of numbers or non-alphabetical characters only"
    // This means an *invalid* description contains no letters
    // Therefore, a *valid* description contains a letter anywhere
    // Other than that, we're not picky about what they type; emoji, numbers, etc.
    public static final String DESCRIPTION_REGEX = "(.*)\\p{L}(.*)";
    public static final String SCORE_FORMATS_DONT_MATCH_MSG = "The score formats do not match";

    public static final String OTHER_SCORE_CANNOT_BE_EMPTY_MSG = "Other score field cannot be empty";

    public static final String ADDING_GOAL_BEFORE_ACTIVITY_START_MSG = "You can only add a goal once the activity starts";
    public static final String ADDING_STAT_BEFORE_START_TIME_MSG = "You can only add an overall score once the activity starts";

    public static final String FIELD_CANNOT_BE_BLANK_MSG = "Field cannot be blank";

    public static final String PLAYER_IS_REQUIRED_MSG = "Player is required";

    public static final String GOAL_NOT_SCORED_WITHIN_DURATION = "Goal must be scored within duration of activity";

    /**
     * Checks start time is valid
     */
    /**/@Target({ METHOD, FIELD, ANNOTATION_TYPE })
    /**/@Retention(RUNTIME)
    /**/@Constraint(validatedBy = {})
    /**/@Documented
    @NotNull(message = START_DATE_REQUIRED_MSG)
    public @interface startActivityValidator {
        String message() default "";

        Class<?>[] groups() default {};

        Class<? extends Payload>[] payload() default {};
    }

    /**
     * Checks end time is valid
     */
    /**/@Target({ METHOD, FIELD, ANNOTATION_TYPE })
    /**/@Retention(RUNTIME)
    /**/@Constraint(validatedBy = {})
    /**/@Documented
    @NotNull(message = END_DATE_REQUIRED_MSG)
    public @interface endActivityValidator {
        String message() default "";

        Class<?>[] groups() default {};

        Class<? extends Payload>[] payload() default {};
    }


    /**
     * Checks that an activity type has been selected
     */
    /**/@Target({ METHOD, FIELD, ANNOTATION_TYPE })
    /**/@Retention(RUNTIME)
    /**/@Constraint(validatedBy = {})
    /**/@Documented
    @NotNull(message = ACTIVITY_TYPE_MSG)
    public @interface activityTypeValidator {
        String message() default "";

        Class<?>[] groups() default {};

        Class<? extends Payload>[] payload() default {};
    }

    /**
     * Checks that a team has been selected, if the activity type has been selected and is game or friendly
     */
    /**/@Target({ METHOD, FIELD, ANNOTATION_TYPE })
    /**/@Retention(RUNTIME)
    /**/@Constraint(validatedBy = {})
    /**/@Documented
    public @interface teamSelectionValidator {
        String message() default TEAM_REQUIRED_MSG;

        Class<?>[] groups() default {};

        Class<? extends Payload>[] payload() default {};
    }


    /**
     * Checks that description isn't too long or too short and matches regex of valid chars
     * (contains at least one letter)
     */
    /**/@Target({ METHOD, FIELD, ANNOTATION_TYPE })
    /**/@Retention(RUNTIME)
    /**/@Constraint(validatedBy = {ActivityDescriptionCheck.class})
    /**/@Documented
    @NotBlank(message = NO_DESC_MSG)
    @Size(max = 150, message = DESC_TOO_LONG_MSG)
    public @interface descriptionValidator {

        String regexMatch() default DESCRIPTION_REGEX;
        String message() default DESC_CONTAINS_INVALID_CHAR_MSG;

        Class<?>[] groups() default {};

        Class<? extends Payload>[] payload() default {};
    }

}
