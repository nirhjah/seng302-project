package nz.ac.canterbury.seng302.tab.end2end;


import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

public class ResetPasswordFeature {


    @Given("I am on the login page")
    public void i_have_navigated_to_the_login_page() {
        PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl + "/login");
    }
    @Given("I am on the lost password form")
    public void i_am_on_the_lost_password_form() {
        PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl + "/forgot-password");
    }



    @When("I hit the lost password button")
    public void i_hit_the_lost_password_button() {
        PlaywrightBrowser.page.locator("text=Forgot password").click();
    }

    @When("I enter an email with invalid format")
    public void i_enter_an_email_with_invalid_format() {
        PlaywrightBrowser.page.locator("input#email").type("abc@gmail");
        PlaywrightBrowser.page.locator("text=Send reset password link").click();
    }

    @When("I enter a valid email that is not known to the system")
    public void i_enter_valid_email_not_known_to_system() {
        PlaywrightBrowser.page.locator("input#email").type("alice@gmail.com");
        PlaywrightBrowser.page.locator("text=Send reset password link").click();
    }

    @Then("An error message tells me the email address is invalid")
    public void error_message_tells_me_email_address_invalid() {
        PlaywrightBrowser.page.locator(".error-message");
        Assertions.assertEquals(PlaywrightBrowser.page.locator(".error-message").textContent(), "Must be a well-formed email");
    }

    @Then("I see a form asking me for my email address")
    public void i_see_a_form_asking_for_my_email_address() {
        PlaywrightBrowser.page.locator("input#email");
    }

    @Then("A confirmation message tells me that an email was sent to the address if it was recognised")
    public void confirmation_message_tells_email_sent_to_address_if_recognised() {
        PlaywrightBrowser.page.locator(".reset-password-info");
        Assertions.assertEquals(PlaywrightBrowser.page.locator(".reset-password-info").textContent(), "If your email is registered with our system, you will receive a link to reset your password shortly.");
    }

}
