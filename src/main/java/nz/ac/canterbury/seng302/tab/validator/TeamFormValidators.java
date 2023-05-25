package nz.ac.canterbury.seng302.tab.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import nz.ac.canterbury.seng302.tab.validator.logic.TeamNameCheck;
import nz.ac.canterbury.seng302.tab.validator.logic.TeamSportCheck;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

public class TeamFormValidators {

    public static final String NOT_BLANK_MSG = "Field can't be blank";

    /**
     * A team name can be alphanumeric, dots and curly braces. Must start with on
     * alphabetical character
     */
    public static final String VALID_TEAM_NAME_REGEX = "[\\p{L}\\p{N}\\{\\}\\. ]*$";
    public static final String INVALID_TEAM_NAME_MSG = "May include letters, hyphens, apostrophes and spaces.";

    /**
     * A sport can be letters, space, apostrophes or hyphens. Must start with on
     * alphabetical character
     **/
    public static final String VALID_TEAM_SPORT_REGEX = "[\\- '\\p{L}]*$";
    public static final String INVALID_CHARACTERS_MSG = "Field contains invalid values";
    public static final String INVALID_TEAM_SPORT_MSG = "May include letters, hyphens, apostrophes and spaces. Must start with letter";



    /**
     * Checks the team sport is not blank and matches
     * the regex of valid sports
     */
    /**/@Target({ METHOD, FIELD, ANNOTATION_TYPE })
    /**/@Retention(RUNTIME)
    /**/@Constraint(validatedBy = {TeamSportCheck.class})
    /**/@Documented
    @NotBlank(message = NOT_BLANK_MSG)
    @Size(max = 30)
    public @interface teamSportValidator {

        String regexMatch() default VALID_TEAM_SPORT_REGEX;
        String message() default "";

        Class<?>[] groups() default {};

        Class<? extends Payload>[] payload() default {};
    }


    /**
     * Checks the team name is not blank and matches
     * the regex of valid team names
     */
    /**/@Target({ METHOD, FIELD, ANNOTATION_TYPE })
    /**/@Retention(RUNTIME)
    /**/@Constraint(validatedBy = {TeamNameCheck.class})
    /**/@Documented
    @NotBlank(message = NOT_BLANK_MSG)
    @Size(max = 30)
    public @interface teamNameValidator {

        String regexMatch() default VALID_TEAM_NAME_REGEX;
        String message() default "";

        Class<?>[] groups() default {};

        Class<? extends Payload>[] payload() default {};
    }

}
