package nz.ac.canterbury.seng302.tab.helper.exceptions;

public class UnmatchedSportException extends RuntimeException {
    public UnmatchedSportException(String sportName1, String sportName2) {
        super("Sports didn't match: " + sportName1 + " and " + sportName2);
    }
}