package nz.ac.canterbury.seng302.tab.end2end;

import com.microsoft.playwright.options.LoadState;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

public class RegisterFeature {

    @Given("I have connected to the system's main URL")
    public void i_have_navigated_to_the_login_page() {
        PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl);
    }

    @When("I hit the button to register")
    public void i_hit_the_register_button() {

        PlaywrightBrowser.page.locator("text=Register").click();
    }

    @Then("I see a registration form")
    public void i_see_a_registration_form() {
        PlaywrightBrowser.page.waitForLoadState(LoadState.NETWORKIDLE);

        Assertions.assertEquals("http://" + PlaywrightBrowser.baseUrl + "/register", PlaywrightBrowser.page.url());
    }

}
