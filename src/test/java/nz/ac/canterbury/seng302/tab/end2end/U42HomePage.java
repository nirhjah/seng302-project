package nz.ac.canterbury.seng302.tab.end2end;

import com.microsoft.playwright.options.LoadState;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

public class U42HomePage {

    @Before("@home_page")
    public void setup() {
        DefaultFunctions.pwLogin();
    }

    @Given("I am logged in")
    public void iAmLoggedIn() {
        //already logged in
    }


    @And("I am apart of at least one team")
    public void iAmApartOfAtLeastOneTeam() {
        Assertions.assertNotNull(PlaywrightBrowser.page.locator(".card-container .card"));
    }

    @When("I am viewing the home page")
    public void iAmViewingTheHomePage() {
        PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl + "/home");
    }

    @Then("I can see the team\\(s) that I am apart of")
    public void iCanSeeTheTeamSThatIAmApartOf() {
        Assertions.assertTrue(PlaywrightBrowser.page.locator(".homeCard li").count() >= 1);
        
    }

    @And("I can click on each one to be taken to the team profile page")
    public void iCanClickOnEachOneToBeTakenToTheTeamProfilePage() {
        PlaywrightBrowser.page.locator(".homeCard li").first().click();

    }

    @And("that team has an upcoming activity")
    public void thatTeamHasAnUpcomingActivity() {
        PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl + "/view-teams?page=1");
        PlaywrightBrowser.page.locator(".card-wrapper").locator("h5:has-text('0')").first().click();
        PlaywrightBrowser.page.locator("#activities").click();
        PlaywrightBrowser.page.waitForLoadState(LoadState.NETWORKIDLE);
        Assertions.assertTrue(PlaywrightBrowser.page.querySelector("span.details").innerText().contains("02 Apr 2025 - 02 Apr 2026"));
    }

    @Then("I can see an overview of details for the upcoming activity")
    public void iCanSeeAnOverviewOfDetailsForTheUpcomingActivity() {
        Assertions.assertTrue(PlaywrightBrowser.page.locator(".teamActivitiesDiv li").count() >= 1);
    }

    @And("I can click on the activity to be taken to the view activity page")
    public void iCanClickOnTheActivityToBeTakenToTheViewActivityPage() {
        PlaywrightBrowser.page.locator(".teamActivitiesDiv li").first().click();
    }

    @And("I have created an upcoming personal activity")
    public void iHaveCreatedAnUpcomingPersonalActivity() {
        DefaultFunctions.pwCreatePersonalActivity();

    }
}
