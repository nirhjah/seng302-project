package nz.ac.canterbury.seng302.tab.end2end;

import com.microsoft.playwright.options.LoadState;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

public class UB4RecordingWhiteboards {

    @Before("@recording_whiteboards")
    public void setup() {
        DefaultFunctions.pwLogin();
        DefaultFunctions.pwCreateTeamForWhiteboard();
    }

    @Given("I am drawing on a whiteboard")
    public void iAmDrawingOnAWhiteboard() {
        PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl + "/view-teams?page=1");
        PlaywrightBrowser.page.locator(".card-wrapper").locator("h5:has-text('whiteboardteam')").first().click();
        PlaywrightBrowser.page.locator("#whiteboard").click();
        PlaywrightBrowser.page.waitForLoadState(LoadState.NETWORKIDLE);

        PlaywrightBrowser.page.click(".draw-button");
        PlaywrightBrowser.page.hover(".upper-canvas");
        PlaywrightBrowser.page.mouse().down();
        PlaywrightBrowser.page.mouse().move(100, 100);
        PlaywrightBrowser.page.mouse().move(200, 200);
        PlaywrightBrowser.page.mouse().up();
    }


    @When("I click on the record button")
    public void iClickOnTheRecordButton() {
        PlaywrightBrowser.page.click(".recording-button");

    }

    @And("I click on the record button again")
    public void iClickOnTheRecordButtonAgain() {
        PlaywrightBrowser.page.waitForTimeout(2000);
        PlaywrightBrowser.page.click(".recording-button");
    }

    @Then("I am shown a  pops up with a save page to save the video")
    public void iAmShownAPopsUpWithASavePageToSaveTheVideo() {
        Assertions.assertTrue(PlaywrightBrowser.page.locator("#recording-modal").isVisible());
    }

    @And("I must give the video a name")
    public void iMustGiveTheVideoAName() {
        PlaywrightBrowser.page.locator("#submit-recording").click();
        Assertions.assertTrue(PlaywrightBrowser.page.locator("#recording-modal").isVisible());
        PlaywrightBrowser.page.locator("input#recording-name").type("my recording");
    }
}
