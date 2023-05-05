package nz.ac.canterbury.seng302.tab.end2end;

import com.microsoft.playwright.options.LoadState;
import io.cucumber.java.en.And;
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
        PlaywrightBrowser.page.locator(".hamburger").click();
        PlaywrightBrowser.page.locator("text=Register").click();
    }

    @Then("I see a registration form")
    public void i_see_a_registration_form() {
        PlaywrightBrowser.page.waitForLoadState(LoadState.NETWORKIDLE);

        Assertions.assertEquals("http://" + PlaywrightBrowser.baseUrl + "/register", PlaywrightBrowser.page.url());
    }

    @Given("I am on the register page")
    public void iAmOnTheRegisterPage() {

        PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl);
        PlaywrightBrowser.page.waitForLoadState(LoadState.NETWORKIDLE);
        Assertions.assertEquals("http://" + PlaywrightBrowser.baseUrl + "/register", PlaywrightBrowser.page.url());

    }


    @When("I enter valid values for <firstName>, <lastName>, <emailAddress>, <dateOfBirth>, <password>, <confirmPassword>, " +
            "<city> and <country>")
    public void iEnterValidValuesForFirstNameLastNameEmailAddressDateOfBirthPasswordConfirmPasswordCityAndCountry() {

        PlaywrightBrowser.page.locator("#first-name").type("John");
        PlaywrightBrowser.page.locator("#last-name").type("Doe");
        PlaywrightBrowser.page.locator("#date-of-birth").type("01202000");
        PlaywrightBrowser.page.locator("#email").type("john@test.com");
        PlaywrightBrowser.page.locator("#password").type("Password123!");
        PlaywrightBrowser.page.locator("#confirm-password").type("Password123");
        PlaywrightBrowser.page.locator("#city").type("Christchurch");
        PlaywrightBrowser.page.locator("#country").type("NZ");

    }

    @Then("I see my user page")
    public void iSeeMyUserPage() {

        Assertions.assertEquals("http://" + PlaywrightBrowser.baseUrl + "/user-info?name=1", PlaywrightBrowser.page.url());


    }
}
