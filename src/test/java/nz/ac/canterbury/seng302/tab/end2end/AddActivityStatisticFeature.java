package nz.ac.canterbury.seng302.tab.end2end;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;


public class AddActivityStatisticFeature {
    @Given("there is an activity type of game or friendly")
    public void there_is_an_activity_type_of_game_or_friendly(){
        DefaultFunctions.pwLogin();
        DefaultFunctions.pwCreateTeam();
        DefaultFunctions.pwCreateActivity();
        PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl + "/view-activity?activityID=1");
    }

    @When("I am adding a substitution,")
    public void i_am_adding_a_substitution() {
        PlaywrightBrowser.page.locator("div#add-activity-button button").click();
        PlaywrightBrowser.page.locator("[name='factType']").selectOption("SUBSTITUTION");
    }

    @Then("I must specify the player who was taken off, the one who was put on and the time that this occured.")
    public void i_must_specify_the_player_who_was_taken_off_the_one_who_was_put_on_and_the_time_that_this_occured() {
        PlaywrightBrowser.page.locator("[name='factType']").selectOption("SUBSTITUTION");
    }

    @When("I specify a time for the substitution")
    public void i_specify_a_time_for_the_substitution(){

    }

    @Then("it is the amount of hours, minutes, seconds into the activity")
    public void it_is_the_amount_of_hours_minutes_seconds_into_the_activity(){

    }
}
