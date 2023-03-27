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
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import nz.ac.canterbury.seng302.tab.validator.logic.DateOfBirthCheck;

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

    public static final String NOT_BLANK_MSG = "Field can't be blank";
    public static final String INVALID_NAME_MSG = "Names can only contain letters, spaces, and hyphens";

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
    /**/@Constraint(validatedBy = {})
    /**/@Documented
    @NotBlank(message = NOT_BLANK_MSG)
    @Size(max = 100)
    @Pattern(regexp = VALID_NAME_REGEX, message = INVALID_NAME_MSG)
    public @interface NameValidator {
        String message() default "";

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
    /**/@Constraint(validatedBy = {})
    /**/@Documented
    @NotBlank(message = NOT_BLANK_MSG)
    @Size(max = 100)
    @Email(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}", flags = Pattern.Flag.CASE_INSENSITIVE)
    public @interface EmailValidator {
        String message() default "";

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

}