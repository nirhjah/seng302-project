package nz.ac.canterbury.seng302.tab.helper.exceptions;

import nz.ac.canterbury.seng302.tab.entity.Grade;

public class UnmatchedGradeException extends RuntimeException {
    public UnmatchedGradeException(Grade grade1, Grade grade2) {
        super("Grades didn't match: [" + grade1.getDisplayString() + "] and [" + grade2.getDisplayString() + "]");
    }
}
