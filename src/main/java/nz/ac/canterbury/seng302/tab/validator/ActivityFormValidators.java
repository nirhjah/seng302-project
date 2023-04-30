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

    public static final String DESC_TOO_LONG_MSG = "Description must be less than 150 characters";

    public static final String NO_DESC_MSG = "Description is required";

    public static final String DESC_CONTAINS_INVALID_CHAR_MSG = "Description must contain letters";

    public static final String START_DATE_REQUIRED_MSG = "Start date and time is required";

    public static final String END_DATE_REQUIRED_MSG = "End date and time is required";

    public static final String END_BEFORE_START_MSG = "Start date and time must be before end date and time";

    public static final String ACTIVITY_BEFORE_TEAM_CREATION = "Activity must be after creation date of team";

    /**
     * Regex taken from <a href="https://stackoverflow.com/a/56276700">...</a>
     */
    public static final String DESCRIPTION_REGEX = "^(?=[^A-Za-z]*[A-Za-z])[ -~]*$";

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
    @NotBlank(message = ACTIVITY_TYPE_MSG)
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
