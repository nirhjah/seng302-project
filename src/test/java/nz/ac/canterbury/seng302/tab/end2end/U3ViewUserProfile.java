package nz.ac.canterbury.seng302.tab.end2end;

import com.microsoft.playwright.options.LoadState;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class U3ViewUserProfile {
    @Given("I am logged in,")
    public void i_am_logged_in() {
        DefaultFunctions.pwLogin();
    }
    @When("I click on the my profile button,")
    public void i_click_on_the_button() {
        PlaywrightBrowser.page.locator(".navbar-user-button").click();
        PlaywrightBrowser.page.locator("a[href='user-info/self']").click();
        PlaywrightBrowser.page.waitForLoadState(LoadState.NETWORKIDLE);
    }

    @Then("I see all my details")
    public void i_can_see_all_my_details() {
        PlaywrightBrowser.page.waitForTimeout(2000);
        List<String> h3TextList = (List<String>) PlaywrightBrowser.page.evaluate("() => { " +
                "const h3Elements = Array.from(document.querySelectorAll('h3')); " +
                "return h3Elements.map(element => element.textContent); " +
                "}");
        Assertions.assertTrue(h3TextList.contains("Admin Admin"));
        Assertions.assertTrue(h3TextList.contains("admin@gmail.com"));
        Assertions.assertTrue(h3TextList.contains("adminAddr1 adminAddr2 adminSuburb, adminCity, 4dm1n, adminLand "));
    }

    @Given("I am on my user profile page,")
    public void i_am_on_my_user_profile_page() {
        i_am_logged_in();

    }

    @Then("I cannot edit any of my details that are shown to me")
    public void i_cannot_edit_any_of_my_details_that_are_shown_to_me() {
        boolean allH3NotContentEditable = (boolean) PlaywrightBrowser.page.evaluate("() => { " +
                "const h3Elements = Array.from(document.querySelectorAll('h3')); " +
                "return h3Elements.every(element => element.getAttribute('contentEditable') !== 'true'); " +
                "}");

        Assertions.assertTrue(allH3NotContentEditable);
    }
}
