package nz.ac.canterbury.seng302.tab.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotNull;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

public class CompetitionValidators {

    public static final String NO_GRADE_MSG = "Grade is required";



    /**
     * Checks that an activity type has been selected
     */
    /**/@Target({ METHOD, FIELD, ANNOTATION_TYPE })
    /**/@Retention(RUNTIME)
    /**/@Constraint(validatedBy = {})
    /**/@Documented
    @NotNull(message = NO_GRADE_MSG)
    public @interface competitionGradeValidator {
        String message() default "";

        Class<?>[] groups() default {};

        Class<? extends Payload>[] payload() default {};
    }


}
