package nz.ac.canterbury.seng302.tab.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import nz.ac.canterbury.seng302.tab.validator.logic.CountryCitySuburbCheck;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

public class TeamFormValidators {

    public static final String NOT_BLANK_MSG = "Field can't be blank";

    public static final String VALID_COUNTRY_SUBURB_CITY_REGEX = "^\\p{L}+[\\- '\\p{L}]*$";

    public static final String INVALID_COUNTRY_SUBURB_CITY_MSG = "May include letters, hyphens, apostrophes and spaces. Must start with letter";

    public static final String VALID_ADDRESS_REGEX = "^(?=.*[\\p{L}\\p{N}])(?:[\\- ,./#'\\p{L}\\p{N}])*$";

    public static final String INVALID_ADDRESS_MSG = "May include letters, numbers, spaces, commas, periods, hyphens, forward slashes, apostrophes and pound signs. Must start with letter or number";

    public static final String VALID_POSTCODE_REGEX = "^[\\p{L}\\p{N}]+[\\-/\\p{L}\\p{N}]*$";

    public static final String INVALID_POSTCODE_MSG = "May include letters, numbers, forward slashes, and hyphens. Must start with letter or number";

//    public static final String sportUnicodeRegex = "^\\p{L}+[\\- '\\p{L}]*$";
//
//    public static final String InvalidSportMSG = "May only include letters, spaces, apostrophes and dashes";
//
//    public static final String teamNameUnicodeRegex = "^[\\p{L}\\p{N}\\s]+[}{.\\p{L}\\p{N}\\s]+$";
//
//    public static final String InvalidTeamNameMSG = "May only include letters, numbers, spaces, dots and curly brackets";



//    /**/@Target({ METHOD, FIELD, ANNOTATION_TYPE })
//    /**/@Retention(RUNTIME)
//    /**/@Constraint(validatedBy = {})
//    /**/@Documented
//    @NotBlank(message = NOT_BLANK_MSG)
//    @Size(max = 30)
//    @Pattern(regexp = sportUnicodeRegex, message = InvalidSportMSG)
//    public @interface sportNameValidator {
//        String message() default "";
//
//        Class<?>[] groups() default {};
//
//        Class<? extends Payload>[] payload() default {};
//
//    }
//
//    /**/@Target({ METHOD, FIELD, ANNOTATION_TYPE })
//    /**/@Retention(RUNTIME)
//    /**/@Constraint(validatedBy = {})
//    /**/@Documented
//    @NotBlank(message = NOT_BLANK_MSG)
//    @Size(max = 30)
//    @Pattern(regexp = teamNameUnicodeRegex, message = InvalidSportMSG)
//    public @interface teamNameValidator {
//        String message() default "";
//
//        Class<?>[] groups() default {};
//
//        Class<? extends Payload>[] payload() default {};
//
//    }

    /**/@Target({ METHOD, FIELD, ANNOTATION_TYPE })
    /**/@Retention(RUNTIME)
    /**/@Constraint(validatedBy = CountryCitySuburbCheck.class)
    /**/@Documented
    @NotBlank(message = NOT_BLANK_MSG)
    @Size(max = 30)
    public @interface countryCitySuburbValidator {

        String regexMatch() default "^\\p{L}+[\\- '\\p{L}]*$";
        String message() default "";

        Class<?>[] groups() default {};

        Class<? extends Payload>[] payload() default {};
    }

    /**
     * Checks that the provided address line is valid.
     * <ul>
     * <li>Must not be blank (At least 1 non-whitespace)</li>
     * <li>Can't be longer than 30 characters</li>
     * <li>Can only contain letters, hyphens, and spaces</li>
     * </ul>
     */
    /**/@Target({ METHOD, FIELD, ANNOTATION_TYPE })
    /**/@Retention(RUNTIME)
    /**/@Constraint(validatedBy = {})
    /**/@Documented
    @Size(max = 30)
    @Pattern(regexp = "^$|" +VALID_ADDRESS_REGEX, message = INVALID_ADDRESS_MSG)
    public @interface addressValidator {
        String message() default "";

        Class<?>[] groups() default {};

        Class<? extends Payload>[] payload() default {};

    }

    /**/@Target({ METHOD, FIELD, ANNOTATION_TYPE })
    /**/@Retention(RUNTIME)
    /**/@Constraint(validatedBy = {})
    /**/@Documented
    @Size(max = 30)
    @Pattern(regexp = "^$|" +VALID_COUNTRY_SUBURB_CITY_REGEX, message = INVALID_COUNTRY_SUBURB_CITY_MSG)
    public @interface suburbValidator {
        String message() default "";

        Class<?>[] groups() default {};

        Class<? extends Payload>[] payload() default {};

    }

    /**
     * Checks that the provided postcode line is valid.
     * <ul>
     * <li>Must not be blank (At least 1 non-whitespace)</li>
     * <li>Can't be longer than 30 characters</li>
     * <li>Can only contain letters, hyphens, and spaces</li>
     * </ul>
     */
    /**/@Target({ METHOD, FIELD, ANNOTATION_TYPE })
    /**/@Retention(RUNTIME)
    /**/@Constraint(validatedBy = {})
    /**/@Documented
    @Size(max = 30)
    @Pattern(regexp = "^$|" +VALID_POSTCODE_REGEX, message = INVALID_POSTCODE_MSG)
    public @interface postcodeValidator {
        String message() default "";

        Class<?>[] groups() default {};

        Class<? extends Payload>[] payload() default {};

    }
}
