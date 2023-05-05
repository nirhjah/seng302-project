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



//    @When("I enter valid values for <firstName>, <lastName>, <emailAddress>, <dateOfBirth>, <password>, <confirmPassword>, " +
//            "<city> and <country>")
//    public void iEnterValidValuesForFirstNameLastNameEmailAddressDateOfBirthPasswordConfirmPasswordCityAndCountry() {
//
//        PlaywrightBrowser.page.locator("input#first-name").type("John");
//        PlaywrightBrowser.page.locator("input#last-name").type("Doe");
//        PlaywrightBrowser.page.fill("input#date-of-birth", "1994-12-04");
//        PlaywrightBrowser.page.locator("input#email").type("john@test.com");
//        PlaywrightBrowser.page.locator("input#password").type("Password123!");
//        PlaywrightBrowser.page.locator("input#confirm-password").type("Password123!");
//        PlaywrightBrowser.page.locator("input#city").type("Christchurch");
//        PlaywrightBrowser.page.locator("input#country").type("NZ");
//
//    }

//    @Then("I see my user page")
//    public void iSeeMyUserPage() {
//
//        Assertions.assertEquals("http://" + PlaywrightBrowser.baseUrl + "/user-info?name=2", PlaywrightBrowser.page.url());
//
//
//    }

    @And("Click the register button")
    public void clickTheRegisterButton() {

        PlaywrightBrowser.page.locator("#registerButton").click();


    }

    @When("I enter invalid values for <firstName> or <lastName>")
    public void iEnterInvalidValuesForFirstNameOrLastName() {

        PlaywrightBrowser.page.locator("input#first-name").type("@@@");
        PlaywrightBrowser.page.locator("input#last-name").type("@@@");
    }

    @Then("I see an error message on the register page")
    public void iSeeAnErrorMessageOnTheRegisterPage() {
        PlaywrightBrowser.page.waitForLoadState(LoadState.NETWORKIDLE);

        Assertions.assertEquals(PlaywrightBrowser.page.locator(".error-message").count() > 0, true);


    }

    @When("I enter an invalid email address <email>")
    public void iEnterAnInvalidEmailAddressEmail() {
        PlaywrightBrowser.page.locator("input#email").type("test@");



    }

    @When("I enter an invalid date of birth <dateOfBirth>")
    public void iEnterAnInvalidDateOfBirthDateOfBirth() {

        PlaywrightBrowser.page.fill("input#date-of-birth", "2020-12-04");

    }

    @When("I enter an invalid email <email>")
    public void iEnterAnInvalidEmailEmail() {
        PlaywrightBrowser.page.locator("input#email").type("admin@gmail.com");

    }

    @Then("I see an error message on the register page telling me the email is already in use")
    public void iSeeAnErrorMessageOnTheRegisterPageTellingMeTheEmailIsAlreadyInUse() {
        PlaywrightBrowser.page.waitForLoadState(LoadState.NETWORKIDLE);
        Assertions.assertEquals(PlaywrightBrowser.page.locator(".error-message").count() > 0, true);

    }

    @When("I enter a different password for <password> and <confirmPassword>")
    public void iEnterADifferentPasswordForPasswordAndConfirmPassword() {

        PlaywrightBrowser.page.locator("input#password").type("Password123!");
        PlaywrightBrowser.page.locator("input#confirm-password").type("Password123");
    }

    @Then("I get an error message on the register page telling me the passwords do not match")
    public void iGetAnErrorMessageOnTheRegisterPageTellingMeThePasswordsDoNotMatch() {
        PlaywrightBrowser.page.waitForLoadState(LoadState.NETWORKIDLE);
        Assertions.assertEquals(PlaywrightBrowser.page.locator(".error-message").count() > 0, true);
    }
}
