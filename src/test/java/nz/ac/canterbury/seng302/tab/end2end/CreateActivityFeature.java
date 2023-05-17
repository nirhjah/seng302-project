package nz.ac.canterbury.seng302.tab.end2end;

import com.microsoft.playwright.options.LoadState;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

public class CreateActivityFeature {

    @When("I hit the button to create an activity")
    public void i_hit_the_register_button() {
        PlaywrightBrowser.page.locator(".hamburger").click();
        PlaywrightBrowser.page.locator("text=Create Activity").click();
    }

    @Then("I see the create activity form")
    public void i_see_a_registration_form() {
        PlaywrightBrowser.page.waitForLoadState(LoadState.NETWORKIDLE);

        Assertions.assertEquals("http://" + PlaywrightBrowser.baseUrl + "/createActivity", PlaywrightBrowser.page.url());
    }
}
