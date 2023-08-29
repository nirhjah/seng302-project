package nz.ac.canterbury.seng302.tab.end2end;

import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;

import com.microsoft.playwright.options.LoadState;

import io.cucumber.java.en.But;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

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

    @When("I set my first and last name as {string} {string}")
    public void i_set_my_first_and_last_name_as(String firstName, String lastName) {
        PlaywrightBrowser.page.locator("input#first-name").type(firstName);
        PlaywrightBrowser.page.locator("input#last-name").type(lastName);
    }

    @When("I set my date of birth as {string}")
    public void i_set_my_date_of_birth_as(String dob) {
        PlaywrightBrowser.page.fill("input#date-of-birth", dob);
    }

    @But("I am only {int} years old")
    public void i_am_only_years_old(int age) {
        LocalDate dob = LocalDate.now().minusYears(age);
        PlaywrightBrowser.page.fill("input#date-of-birth", dob.toString());
    }

    /**
     * Issue: The database is persistent, so this test will pass only once
     * Solution: Add a UUID to the beginning of the email address.
     */
    @When("I set my email as {string}")
    public void i_set_my_email_as(String email) {
        String uuid = UUID.randomUUID().toString();
        PlaywrightBrowser.page.locator("input#email").type(uuid + email);
    }

    @When("I set my password as {string}")
    public void i_set_my_password_as(String password) {
        PlaywrightBrowser.page.locator("input#password").type(password);
        PlaywrightBrowser.page.locator("input#confirm-password").type(password);
    }

    @But("I set my confirm password as {string}")
    public void i_set_my_confirm_password_as(String confirmPassword) {
        PlaywrightBrowser.page.locator("input#confirm-password").type(confirmPassword);
    }

    @When("Click the next button")
    public void clickTheNextButton() {
        PlaywrightBrowser.page.locator(".next-button").click();
    }

    @When("I set my city and country as {string} {string}")
    public void i_set_my_city_and_country_as(String city, String country) {
        PlaywrightBrowser.page.locator("input#city").type(city);
        PlaywrightBrowser.page.locator("input#country").type(country);
    }

    @When("Click the register button")
    public void click_the_register_button() {
        PlaywrightBrowser.page.locator("button.register-button").click();
    }

    @Then("I receive a message stating an email has been sent")
    public void i_receive_a_message_stating_an_email_has_been_sent() {
        // Firstly, you should be redirected to the login page
        String expectedLoginPage = "http://" + PlaywrightBrowser.baseUrl + "/login";
        Assertions.assertEquals(expectedLoginPage, PlaywrightBrowser.page.url());
        // Secondly, that page should inform you that an email has been sent
        String emailMessage = PlaywrightBrowser.page.locator("p.message-dropdown-text").innerText();
        Assertions.assertTrue(emailMessage.contains("An email has been sent to your email address"));
    }

    @Then("I see an error message on {string} stating {string}")
    public void i_see_an_error_message_stating(String element, String expectedMsg) {
        PlaywrightBrowser.page.waitForLoadState(LoadState.NETWORKIDLE);

        // Thymeleaf form error messages are given a class name of that field's name
        String errorMessage = PlaywrightBrowser.page.locator(".error-message." + element).innerText();

        Assertions.assertEquals(expectedMsg, errorMessage);

    }
}
