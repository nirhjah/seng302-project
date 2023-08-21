package nz.ac.canterbury.seng302.tab.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotNull;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

public class CompetitionFormValidators {

    public static final String NO_GRADE_MSG = "A Grade consisting of all attributes is required";

    public static final String NO_COMPETITORS_MSG = "You must select at least one competitor";

    public static final String NO_DATE_MSG = "Date and time is required";

    /**
     * Checks that a grade value has been selected
     */
    /**/@Target({ METHOD, FIELD, ANNOTATION_TYPE })
    /**/@Retention(RUNTIME)
    /**/@Constraint(validatedBy = {})
    /**/@Documented
    public @interface gradeSelectionValidator {
        String message() default NO_GRADE_MSG;

        Class<?>[] groups() default {};

        Class<? extends Payload>[] payload() default {};
    }


    /**
     * Checks start time is valid
     */
    /**/@Target({ METHOD, FIELD, ANNOTATION_TYPE })
    /**/@Retention(RUNTIME)
    /**/@Constraint(validatedBy = {})
    /**/@Documented
    @NotNull(message = NO_DATE_MSG)
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
    @NotNull(message = NO_DATE_MSG)
    public @interface endActivityValidator {
        String message() default "";
        Class<?>[] groups() default {};
        Class<? extends Payload>[] payload() default {};
    }
}
