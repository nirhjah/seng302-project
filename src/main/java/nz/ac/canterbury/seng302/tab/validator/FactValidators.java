package nz.ac.canterbury.seng302.tab.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

public class FactValidators {

    public static final String timeErrorMessage = "Error: The event must occur within the duration of the game";

    public static final String descriptionErrorMessage = "Error: Description is a required field";

    public static final String descriptionLengthMessage = "Error: Description cannot exceed 150 characters";

    /**/@Target({ METHOD, FIELD, ANNOTATION_TYPE })
    /**/@Retention(RUNTIME)
    /**/@Constraint(validatedBy = {})
    /**/@Documented
    @NotBlank(message = descriptionErrorMessage)
    @Size(max = 150, message = descriptionLengthMessage)
    public @interface descriptionValidator {
        String message() default descriptionErrorMessage;

        Class<?>[] groups() default {};

        Class<? extends Payload>[] payload() default {};
    }
}
