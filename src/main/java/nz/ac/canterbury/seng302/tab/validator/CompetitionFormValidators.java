package nz.ac.canterbury.seng302.tab.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

public class CompetitionFormValidators {

    public static final String NO_GRADE_MSG = "A Grade consisting of all attributes is required";

    public static final String NO_COMPETITORS_MSG = "You must select at least one competitor";

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


}
