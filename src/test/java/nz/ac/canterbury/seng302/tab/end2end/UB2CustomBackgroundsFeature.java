package nz.ac.canterbury.seng302.tab.end2end;

import com.microsoft.playwright.options.LoadState;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

public class UB2CustomBackgroundsFeature {

    @Before("@custom_background")
    public void create_team_init() {
        DefaultFunctions.pwLogin();
        DefaultFunctions.pwCreateTeamForWhiteboard();
    }
    @Given("I am creating a whiteboard")
    public void i_am_creating_a_whiteboard() {
        PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl + "/view-teams?page=2");
        PlaywrightBrowser.page.locator(".card-wrapper").locator("h5:has-text('whiteboardteam')").first().click();
        PlaywrightBrowser.page.locator("#whiteboard").click();
        PlaywrightBrowser.page.waitForLoadState(LoadState.NETWORKIDLE);
    }

    @When("I select the field type")
    public void i_select_the_field_type() {
        PlaywrightBrowser.page.locator("#pitch").click();
    }

    @Then("I can choose from a list of field types")
    public void i_can_choose_from_a_list_of_field_types() {
        PlaywrightBrowser.page.isVisible("#pitch option");
    }

    @Then("the selected pitch {string} becomes the background of the whiteboard")
    public void the_selected_pitch_becomes_the_background_of_the_whiteboard(String string) {
        PlaywrightBrowser.page.selectOption("#pitch",string);

        String backgroundImage = String.valueOf(PlaywrightBrowser.page.locator(".canvas-container").evaluate("element => window.getComputedStyle(element).backgroundImage"));

        String pitchName = string.replace(" ", "-").toLowerCase();
        Assertions.assertTrue(backgroundImage.contains(pitchName));



    }
}
