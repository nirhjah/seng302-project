package nz.ac.canterbury.seng302.tab.validator.logic;

import java.time.LocalDate;
import java.time.Period;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import nz.ac.canterbury.seng302.tab.validator.UserFormValidators;
import nz.ac.canterbury.seng302.tab.validator.UserFormValidators.DateOfBirthValidator;

/**
 * <p>Implementation for the {@link UserFormValidators} annotation.</p>
 * Checks whether the provided date is at least <code>minimumAge()</code> years before now.
 */
public class DateOfBirthCheck implements ConstraintValidator<DateOfBirthValidator, LocalDate> {

    private int minimumAge;

    @Override
    public void initialize(DateOfBirthValidator constraintAnnotation) {
        this.minimumAge = constraintAnnotation.minimumAge();
    }

    @Override
    public boolean isValid(LocalDate dateOfBirth, ConstraintValidatorContext context) {
        if (dateOfBirth == null) return false;
        int ageInYears = Period.between(dateOfBirth, LocalDate.now()).getYears();
        return (ageInYears >= minimumAge);
    }
}
