package nz.ac.canterbury.seng302.tab.end2end;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

public class U4EditUserProfile {

    private String DEFAULT_USER_NAME = "Admin Admin";
    private String DEFAULT_EMAIL= "admin@gmail.com";
    private String DEFAULT_DOB ="01-01-1970";
    private String DEFAULT_LOCATION ="adminAddr1 adminAddr2 adminSuburb, adminCity, 4dm1n, adminLand ";
    @Before("@edit_user")
    public void setUp() {
        DefaultFunctions.pwLogin();
    }

    @Given("I am on the user profile page,")
    public void i_am_on_the_user_profile_page() {
        String currentUrl = PlaywrightBrowser.page.url();
        Assertions.assertTrue(currentUrl.contains("user-info"));

        String nameValue = String.valueOf(PlaywrightBrowser.page.evaluate("() => document.querySelector('.name').innerText"));
        String emailValue = String.valueOf(PlaywrightBrowser.page.evaluate("() => document.querySelector('.email').innerText"));
        String dobValue = String.valueOf(PlaywrightBrowser.page.evaluate("() => document.querySelector('.dob').innerText"));
        String locationValue = String.valueOf(PlaywrightBrowser.page.evaluate("() => document.querySelector('.location').innerText"));
        System.out.println(nameValue + " " + emailValue + " " + dobValue + " " + locationValue);
    }

    @When("I hit the edit button")
    public void i_hit_the_edit_button(){
        PlaywrightBrowser.page.locator("a[href='editUser']").click();
        PlaywrightBrowser.page.waitForTimeout(2000);
    }

    @When ("I hit the save button")
    public void i_hit_the_save_button(){

    }

    @Then("I can see the edit profile form with all my details prepopulated except the passwords.")
    public void i_can_see_the_edit_profile_form_with_all_my_details_prepopulated_except_the_passwords() {

    }

    @Given("I am on the edit profile form,")
    public void i_am_on_the_edit_profile_form() {

    }

    @Given("I enter valid values for my first name, last name, email address, and date of birth,")
    public void i_enter_valid_values_for_my_first_name_last_name_email_address_and_date_of_birth() {

    }

    @Then("my new details are saved.")
    public void my_new_details_are_saved() {
    }

    @Given("I enter invalid values \\(i.e. empty strings or non-alphabetical characters except a hypen or space) for my firstname,")
    public void i_enter_invalid_values_i_e_empty_strings_or_non_alphabetical_characters_except_a_hypen_or_space_for_my_firstname() {
    }

    @Then("an error message tells me these fields contain invalid values.")
    public void an_error_message_tells_me_these_fields_contain_invalid_values() {
    }

    @Given("I enter a malformed or empty email address,")
    public void i_enter_a_malformed_or_empty_email_address() {
    }

    @Then("an error message tells me the email address is invalid")
    public void an_error_message_tells_me_the_email_address_is_invalid() {
    }

}
