package nz.ac.canterbury.seng302.tab.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 
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

    // Matches the alphabet, spaces (e.g. 'van Beethoven'),
    // and hyphens (e.g. 'Taylor-Joy'), because names can have those.
    public static final String VALID_NAME_REGEX = "[a-zA-Z\\- ]+";

    public static final String NOT_BLANK_MSG = "Field can't be blank";
    public static final String INVALID_NAME_MSG = "Names can only contain letters, spaces, and hyphens";

    /**/@Target({ METHOD, FIELD, ANNOTATION_TYPE })
    /**/@Retention(RUNTIME)
    /**/@Constraint(validatedBy = {})
    /**/@Documented
    @NotBlank(message = NOT_BLANK_MSG)
    @Pattern(regexp = VALID_NAME_REGEX, message = INVALID_NAME_MSG)
    @Size(max=100)
    public @interface NameValidator {
        String message() default "";
        Class<?>[] groups() default {};
        Class<? extends Payload>[] payload() default {};

    }

    /**/@Target({ METHOD, FIELD, ANNOTATION_TYPE })
    /**/@Retention(RUNTIME)
    /**/@Constraint(validatedBy = {})
    /**/@Documented
    @NotBlank(message = NOT_BLANK_MSG)
    @Email(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}", flags = Pattern.Flag.CASE_INSENSITIVE)
    public @interface EmailValidator {
        String message() default "";
        Class<?>[] groups() default {};
        Class<? extends Payload>[] payload() default {};

    }
}
