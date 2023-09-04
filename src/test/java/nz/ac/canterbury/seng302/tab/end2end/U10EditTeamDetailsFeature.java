package nz.ac.canterbury.seng302.tab.end2end;

import com.microsoft.playwright.options.LoadState;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

public class U10EditTeamDetailsFeature {
    private static boolean isSetupExecuted = false;

    private final String TEAM_NAME = "team";
    private final String SPORT= "football";
    private final String CITY ="Christchurch";
    private final String COUNTRY ="New Zealand";
    @Before("@edit_team")
    public void create_team_init() {
        DefaultFunctions.pwLogin();
        if (!isSetupExecuted) {
            DefaultFunctions.pwCreateTeam();
            isSetupExecuted = true;
        }
    }

    @Given("I am on my team profile page,")
    public void i_am_on_my_team_profile_page() {
        PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl + "/team-info?teamID=1");
    }

    @When("I hit the edit button,")
    public void i_hit_the_edit_button() {
        PlaywrightBrowser.page.locator("#edit-profile").click();
    }

    @Then("I see the edit team details form with all its details prepopulated.")
    public void i_see_the_edit_team_details_form_with_all_its_details_prepopulated() {
        String nameValue = String.valueOf(PlaywrightBrowser.page.evaluate("() => document.querySelector('input#name').value"));
        String sportValue = String.valueOf(PlaywrightBrowser.page.evaluate("() => document.querySelector('input#sport').value"));
        String cityValue = String.valueOf(PlaywrightBrowser.page.evaluate("() => document.querySelector('input#city').value"));
        String countryValue = String.valueOf(PlaywrightBrowser.page.evaluate("() => document.querySelector('input#country').value"));
        Assertions.assertEquals(TEAM_NAME, nameValue);
        Assertions.assertEquals(SPORT, sportValue);
        Assertions.assertEquals(CITY, cityValue);
        Assertions.assertEquals(COUNTRY, countryValue);

    }

    @Given("I am on the edit team profile form,")
    public void i_am_on_the_edit_team_profile_form() {
        PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl + "/team-info?teamID=1");
        PlaywrightBrowser.page.locator("#edit-profile").click();

    }

    @Given("I enter valid values for the name, location and sport,")
    public void i_enter_valid_values_for_the_name_location_and_sport() {
        PlaywrightBrowser.page.locator("input#name").clear();
        PlaywrightBrowser.page.locator("input#address-line-1").clear();
        PlaywrightBrowser.page.locator("input#city").clear();
        PlaywrightBrowser.page.locator("input#country").clear();
        PlaywrightBrowser.page.locator("input#sport").clear();

        PlaywrightBrowser.page.locator("input#name").type("Testing");
        PlaywrightBrowser.page.locator("input#address-line-1").type("addressline1");
        PlaywrightBrowser.page.locator("input#city").type("city");
        PlaywrightBrowser.page.locator("input#country").type("country");
        PlaywrightBrowser.page.locator("input#sport").type("sport");
    }

    @When("I hit the save button, then the new details are saved.")
    public void i_hit_the_save_button_then_the_new_details_are_saved() {
        PlaywrightBrowser.page.locator(".submit-button button").click();
        PlaywrightBrowser.page.waitForLoadState(LoadState.NETWORKIDLE);

        String nameValue = String.valueOf(PlaywrightBrowser.page.evaluate("() => document.querySelector('h3[data-cy=\"name\"]').textContent"));
        Assertions.assertEquals("Testing", nameValue);

    }

    @Given("I enter an invalid value for the team�s name \\(e.g., non-alphanumeric other than dots or curly brackets, name made of only acceptable non-alphanumeric),")
    public void i_enter_an_invalid_value_for_the_team_s_name_e_g_non_alphanumeric_other_than_dots_or_curly_brackets_name_made_of_only_acceptable_non_alphanumeric() {
    }

    @When("I hit the save button,")
    public void i_hit_the_save_button() {
        PlaywrightBrowser.page.locator(".submit-button button").click();
        PlaywrightBrowser.page.waitForLoadState(LoadState.NETWORKIDLE);
    }

    @Then("an error message tells me the name contains invalid characters.")
    public void an_error_message_tells_me_the_name_contains_invalid_characters() {

    }

    @Given("I enter invalid values \\(i.e. empty strings or non-alphabetical characters) for the location or sport,")
    public void i_enter_invalid_values_i_e_empty_strings_or_non_alphabetical_characters_for_the_location_or_sport() {
    }

    @Then("an error message tells me these fields contain invalid values.")
    public void an_error_message_tells_me_these_fields_contain_invalid_values() {

    }

    @When("I hit the cancel button, I come back to the team�s profile page,")
    public void i_hit_the_cancel_button_i_come_back_to_the_team_s_profile_page() {

    }

    @When("no changes have been made to its profile")
    public void no_changes_have_been_made_to_its_profile() {

    }

    @And("I enter an invalid value for the team’s name \\(e.g., non-alphanumeric other than dots or curly brackets, name made of only acceptable non-alphanumeric),")
    public void iEnterAnInvalidValueForTheTeamSNameEGNonAlphanumericOtherThanDotsOrCurlyBracketsNameMadeOfOnlyAcceptableNonAlphanumeric() {
    }

    @When("I hit the cancel button, I come back to the team’s profile page,")
    public void iHitTheCancelButtonIComeBackToTheTeamSProfilePage() {
        PlaywrightBrowser.page.locator(".cancel-link a").click();
        PlaywrightBrowser.page.waitForLoadState(LoadState.NETWORKIDLE);
    }
}
