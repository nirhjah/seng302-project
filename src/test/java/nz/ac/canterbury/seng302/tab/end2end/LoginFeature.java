package nz.ac.canterbury.seng302.tab.end2end;

import com.microsoft.playwright.options.LoadState;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

public class LoginFeature {

    @When("I hit the button to login")
    public void iHitTheButtonToLogin() {
        PlaywrightBrowser.page.locator(".hamburger").click();
        PlaywrightBrowser.page.locator("text=Login").click();
    }

    @Then("I see a login form")
    public void iSeeALoginForm() {
        PlaywrightBrowser.page.waitForLoadState(LoadState.NETWORKIDLE);

        Assertions.assertEquals("http://" + PlaywrightBrowser.baseUrl + "/login", PlaywrightBrowser.page.url());
    }

    @Given("I am on the login form")
    public void i_have_navigated_to_the_login_page() {
        PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl + "/login");
    }

    @When("I enter valid email and password")
    public void iEnterValidEmailAddressEmailAndPasswordPassword() {
        PlaywrightBrowser.page.locator("input#username").type("admin@gmail.com");
        PlaywrightBrowser.page.locator("input#password").type("1");
    }

    @And("I hit the login button")
    public void iHitTheLoginButton() {
        PlaywrightBrowser.page.locator("#loginButton").click();
    }

    @Then("I am brought to my profile page")
    public void iAmBroughtToMyProfilePage() {
        PlaywrightBrowser.page.waitForLoadState(LoadState.NETWORKIDLE);

        Assertions.assertEquals("http://" + PlaywrightBrowser.baseUrl + "/user-info?name=1", PlaywrightBrowser.page.url());

    }

    @When("I enter an invalid {string} email and {string} password")
    public void iEnterAnInvalidEmailAddressEmailAndPasswordPassword(String email, String password) {
        PlaywrightBrowser.page.locator("input#username").type(email);
        PlaywrightBrowser.page.locator("input#password").type(password);
    }

    @Then("I see an error message on the login page telling me the email or password is invalid")
    public void iSeeAnErrorMessageOnTheLoginPageTellingMeTheEmailIsInvalid() {
        PlaywrightBrowser.page.waitForLoadState(LoadState.NETWORKIDLE);
        Assertions.assertEquals("Invalid Email or Password", PlaywrightBrowser.page.locator(".error-message").textContent());
    }

    @When("I enter a email not known to the system")
    public void iEnterAEmailNotKnownToTheSystem() {
        PlaywrightBrowser.page.locator("input#username").type("tester@gmail.com");
        PlaywrightBrowser.page.locator("input#password").type("1");
    }

    @When("I enter an invalid password {string} for an email that is known")
    public void iEnterAnEmptyOrIncorrectPasswordForAnEmailThatIsKnown(String password) {
        PlaywrightBrowser.page.locator("input#username").type("admin@gmail.com");
        PlaywrightBrowser.page.locator("input#password").type(password);
    }

    @When("I click the cancel button")
    public void iClickTheCancelButton() {
        PlaywrightBrowser.page.locator("#cancelButton").click();
    }

    @Then("I am taken to the home page")
    public void iAmTakenToTheHomePage() {
        PlaywrightBrowser.page.waitForLoadState(LoadState.NETWORKIDLE);

        Assertions.assertEquals("http://" + PlaywrightBrowser.baseUrl + "/login?continue=/home", PlaywrightBrowser.page.url());
    }
}
