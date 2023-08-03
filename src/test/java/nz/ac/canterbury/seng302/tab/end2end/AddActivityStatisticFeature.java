package nz.ac.canterbury.seng302.tab.end2end;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static nz.ac.canterbury.seng302.tab.end2end.DefaultFunctions.pwCreateActivity;


public class AddActivityStatisticFeature {
    @Before("@Add_activity_statistics")
    public void setup() {
        DefaultFunctions.pwLogin();
    }
    @Given("there is an activity type of game or friendly")
    public void there_is_an_activity_type_of_game_or_friendly(){
        pwCreateActivity();
        PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl + "/view-activity?activityID=1");
    }

    @When("I am adding a substitution,")
    public void i_am_adding_a_substitution() {
        PlaywrightBrowser.page.locator("div.submit-button button").evaluate("button => button.click()");
        PlaywrightBrowser.page.locator("[name='factType']").selectOption("SUBSTITUTION");
    }

    @Then("I must specify the player who was taken off, the one who was put on and the time that this occured.")
    public void i_must_specify_the_player_who_was_taken_off_the_one_who_was_put_on_and_the_time_that_this_occured() {
        PlaywrightBrowser.page.locator("[name='factType']").selectOption("SUBSTITUTION");

    }
}
