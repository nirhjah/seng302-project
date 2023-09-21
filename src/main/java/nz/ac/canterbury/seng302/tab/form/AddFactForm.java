package nz.ac.canterbury.seng302.tab.form;

import nz.ac.canterbury.seng302.tab.validator.FactValidators;

public class AddFactForm {

    private String timeOfFact;

    @FactValidators.descriptionValidator
    private String description;

    public String getTimeOfFact() {
        return timeOfFact;
    }

    public void setTimeOfFact(String timeOfEvent) {
        this.timeOfFact = timeOfEvent;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
