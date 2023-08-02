package nz.ac.canterbury.seng302.tab.form;

import nz.ac.canterbury.seng302.tab.entity.Activity;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.enums.FactType;

public class CreateEventForm {
  
  private FactType factType;

  private String description;

  private String time;

  private Activity activity;

  private String overallScoreTeam;

  private String overallScoreOpponent;

  public CreateEventForm() {
  }

  private User subOn;



  public User getSubOn() {
    return subOn;
  }

  public void setSubOn(User subOn) {
    this.subOn = subOn;
  }

  public String getOverallScoreTeam() {
    return overallScoreTeam;
  }

  public void setOverallScoreTeam(String overallScoreTeam) {
    this.overallScoreTeam = overallScoreTeam;
  }

  public String getOverallScoreOpponent() {
    return overallScoreOpponent;
  }

  public void setOverallScoreOpponent(String overallScoreOpponent) {
    this.overallScoreOpponent = overallScoreOpponent;
  }

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

