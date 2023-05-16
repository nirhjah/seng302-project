package nz.ac.canterbury.seng302.tab.validator;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.*;
import nz.ac.canterbury.seng302.tab.validator.logic.*;

/**
 * A collection of validation annotation groups relating to user forms (registration, editing, ...)
 * <p>This allows consistent validation across the project by reusing the same settings.</p>
 */
public class UserFormValidators {

    /*
     * This works by combining pre-existing Jakarta
     * validators into a single annotation.
     * When adding these, make the validator annotations clearer
     * than the required preamble.
     * 
     * Code from
     * https://docs.jboss.org/hibernate/stable/validator/reference/en-US/html_single
     * /?v=5.4#section-constraint-composition
     */

    // Matches any language's alphabet, spaces, and hyphens
    // (e.g. 'van Beethoven', 'Taylor-Joy')...
    public static final String VALID_NAME_REGEX = "^[\\p{L}\\- ]+$";
    public static final String NOT_BLANK_MSG = "Field cannot be blank";
    public static final String WELL_FORMED_EMAIL = "Must be a well-formed email";
    public static final String WEAK_PASSWORD_MESSAGE = "Password does not meet the requirements";

    public static final String VALID_EMAIL_REGEX = "(?i)[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}";
    public static final String INVALID_NAME_MSG = "Contains Invalid characters";
    public static final String VALID_COUNTRY_SUBURB_CITY_REGEX = "^\\p{L}+[\\- '\\p{L}]*$";
    public static final String INVALID_COUNTRY_SUBURB_CITY_MSG = "Contains Invalid characters";
    public static final String VALID_ADDRESS_REGEX = "^(?=.*[\\p{L}\\p{N}])(?:[\\- ,./#'\\p{L}\\p{N}])*$";
    public static final String INVALID_ADDRESS_MSG = "Contains Invalid characters";
    public static final String VALID_POSTCODE_REGEX = "^[\\p{L}\\p{N}]+[\\-/\\p{L}\\p{N}]*$";
    public static final String INVALID_POSTCODE_MSG = "Contains Invalid characters";

    /**
     * Checks that the provided name is valid.
     * <ul>
     * <li>Must not be blank (At least 1 non-whitespace)</li>
     * <li>Can't be longer than 100 characters</li>
     * <li>Can only contain letters, hyphens, and spaces</li>
     * </ul>
     */
    /**/@Target({ METHOD, FIELD, ANNOTATION_TYPE })
    /**/@Retention(RUNTIME)
    /**/@Constraint(validatedBy = { NameCheck.class })
    /**/@Documented
    @NotBlank(message = NOT_BLANK_MSG)
    public @interface NameValidator {
        String message() default INVALID_NAME_MSG;

        Class<?>[] groups() default {};

        Class<? extends Payload>[] payload() default {};

    }

    /**
     * Checks that the provided email is valid.
     * <ul>
     * <li>Must not be blank (At least 1 non-whitespace)</li>
     * <li>Can't be longer than 100 characters</li>
     * <li>Must be a well-formed email</li>
     * </ul>
     */
    /**/ @Target({ METHOD, FIELD, ANNOTATION_TYPE })
    /**/@Retention(RUNTIME)
    /**/@Constraint(validatedBy = { EmailCheck.class })
    /**/@Documented
    @NotBlank(message = NOT_BLANK_MSG)
    public @interface EmailValidator {
        String message() default WELL_FORMED_EMAIL;

        Class<?>[] groups() default {};

        Class<? extends Payload>[] payload() default {};

    }

    /**
     * Checks that the provided date was at least a certain number of years ago
     * (default of 13).
     * <ul>
     * <li>Must not be null</li>
     * <li>Must be at least <code>minimumAge</code> years ago</li>
     * </ul>
     * The implementation is at {@link DateOfBirthCheck}
     */
    /**/@Target({ METHOD, FIELD, ANNOTATION_TYPE })
    /**/@Retention(RUNTIME)
    /**/@Constraint(validatedBy = DateOfBirthCheck.class)
    /**/@Documented
    @NotNull(message = NOT_BLANK_MSG)
    public @interface DateOfBirthValidator {
        int minimumAge() default 13;

        String message() default "You are not old enough to join";

        Class<?>[] groups() default {};

        Class<? extends Payload>[] payload() default {};
    }


    /**
     * Check that password is valid
     * <ul>
     * <li>Must not be null</li>
     * <li>Must meet requirements </li>
     * <li>Must not be in any other field </li>
     * </ul>
     * The implementation is at {@link PasswordCheck}
     */
    /**/@Target({ METHOD, FIELD, ANNOTATION_TYPE })
    /**/@Retention(RUNTIME)
    /**/@Constraint(validatedBy = PasswordCheck.class)
    /**/@Documented
    @NotNull(message = NOT_BLANK_MSG)
    public @interface PasswordValidator {

        String message() default "Password does not meet the requirements";

        Class<?>[] groups() default {};

        Class<? extends Payload>[] payload() default {};
    }


    /**
     * Checks that the provided country is valid.
     * <ul>
     * <li>Must not be blank (At least 1 non-whitespace)</li>
     * <li>Can't be longer than 30 characters</li>
     * <li>Can only contain letters, hyphens, and spaces</li>
     * </ul>
     */
    /**/@Target({ METHOD, FIELD, ANNOTATION_TYPE })
    /**/@Retention(RUNTIME)
    /**/@Constraint(validatedBy = { CountryCheck.class })
    /**/@Documented
    @NotBlank(message = NOT_BLANK_MSG)
    public @interface countryValidator {
        String message() default INVALID_COUNTRY_SUBURB_CITY_MSG;

        Class<?>[] groups() default {};

        Class<? extends Payload>[] payload() default {};

    }

    /**
     * Checks that the provided city is valid.
     * <ul>
     * <li>Must not be blank (At least 1 non-whitespace)</li>
     * <li>Can't be longer than 30 characters</li>
     * <li>Can only contain letters, hyphens, and spaces</li>
     * </ul>
     */
    /**/@Target({ METHOD, FIELD, ANNOTATION_TYPE })
    /**/@Retention(RUNTIME)
    /**/@Constraint(validatedBy = { CityCheck.class })
    /**/@Documented
    @NotBlank(message = NOT_BLANK_MSG)
    public @interface cityValidator {
        String message() default INVALID_COUNTRY_SUBURB_CITY_MSG;

        Class<?>[] groups() default {};

        Class<? extends Payload>[] payload() default {};

    }

    /**
     * Checks that the provided suburb is valid.
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
    @Pattern(regexp = "^$|" +VALID_COUNTRY_SUBURB_CITY_REGEX, message = INVALID_COUNTRY_SUBURB_CITY_MSG)
    public @interface suburbValidator {
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
