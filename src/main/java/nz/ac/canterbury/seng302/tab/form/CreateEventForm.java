package nz.ac.canterbury.seng302.tab.form;

import nz.ac.canterbury.seng302.tab.entity.Activity;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.enums.FactType;
import nz.ac.canterbury.seng302.tab.validator.LocationValidators;
import nz.ac.canterbury.seng302.tab.validator.TeamFormValidators;

public class CreateEventForm {
  
  private FactType factType;

  private String description;

  private String time;

  private Activity activity;
  
  public FactType getFactType() {
    return factType;
  }

  public void setFactType(FactType factType) {
    this.factType = factType;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getTime() {
    return time;
  }

  public void setTime(String time) {
    this.time = time;
  }

  public Activity getActivity() {
    return activity;
  }

  public void setActivity(Activity activity) {
    this.activity = activity;
  }

  
}

