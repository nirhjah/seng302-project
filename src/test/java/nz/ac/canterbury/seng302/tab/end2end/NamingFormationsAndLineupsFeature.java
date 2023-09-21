package nz.ac.canterbury.seng302.tab.end2end;

import com.microsoft.playwright.options.LoadState;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class NamingFormationsAndLineupsFeature {

    @Before("@naming_formations_and_lineups")
    public void setup() {
        PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl + "/populate_database");
        DefaultFunctions.pwLogin();
        DefaultFunctions.pwCreateNewTeamWithFormationAndActivity();

    }

    @Given("I name a lineup that I am creating")
    public void iNameALineupThatIAmCreating() {
        PlaywrightBrowser.page.locator("#activities").click();
        PlaywrightBrowser.page.waitForLoadState(LoadState.NETWORKIDLE);
        PlaywrightBrowser.page.locator("#editActivityBtn").first().click();
        PlaywrightBrowser.page.locator("#formation-dropdown").selectOption("2");

        PlaywrightBrowser.page.click(".reserves-li:nth-child(1)");
        PlaywrightBrowser.page.locator("#player1").click();
        PlaywrightBrowser.page.click(".reserves-li:nth-child(2)");
        PlaywrightBrowser.page.locator("#player2").click();


        PlaywrightBrowser.page.locator("input#lineUpName").type("lineup1");


    }

    @When("the lineup is saved")
    public void theLineupIsSaved() {
        PlaywrightBrowser.page.click("button:has-text('Save')");
    }

    @Then("the lineup displays with its name anywhere the lineup is displayed")
    public void theLineupDisplaysWithItsNameAnywhereTheLineupIsDisplayed() {

        PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl + "/view-teams?page=1");
        PlaywrightBrowser.page.locator(".card-wrapper").locator("h5:has-text('0')").first().click();
        PlaywrightBrowser.page.waitForLoadState(LoadState.NETWORKIDLE);
        PlaywrightBrowser.page.locator("#whiteboard").click();
        PlaywrightBrowser.page.waitForLoadState(LoadState.NETWORKIDLE);
    }
}
