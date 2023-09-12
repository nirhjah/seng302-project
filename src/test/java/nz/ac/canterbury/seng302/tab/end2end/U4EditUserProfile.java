package nz.ac.canterbury.seng302.tab.end2end;

import com.microsoft.playwright.options.LoadState;
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

        String nameValue = PlaywrightBrowser.page.locator(".user-label:has(.attribute-type:has-text('Name')) .name").textContent();
        String dobValue = PlaywrightBrowser.page.locator(".user-label:has(.attribute-type:has-text('Date of Birth')) .dob").textContent();
        String emailValue = PlaywrightBrowser.page.locator(".user-label:has(.attribute-type:has-text('Email')) .email").textContent();
        String locationValue = PlaywrightBrowser.page.locator(".user-label:has(.attribute-type:has-text('Location')) .location").textContent();

        DEFAULT_USER_NAME= nameValue;
        DEFAULT_EMAIL=emailValue;
        DEFAULT_DOB=dobValue;
        DEFAULT_LOCATION=locationValue;
        Assertions.assertTrue(currentUrl.contains("user-info"));
    }

    @When("I hit the edit button")
    public void i_hit_the_edit_button(){
        PlaywrightBrowser.page.locator("a[href='editUser']").click();
        PlaywrightBrowser.page.waitForTimeout(2000);
    }

    @When ("I hit the save button")
    public void i_hit_the_save_button(){
        PlaywrightBrowser.page.locator("#submit").click();

    }

    @Then("I can see the edit profile form with all my details prepopulated except the passwords.")
    public void i_can_see_the_edit_profile_form_with_all_my_details_prepopulated_except_the_passwords() {
        PlaywrightBrowser.page.waitForLoadState(LoadState.NETWORKIDLE);

        String firstNameValue = PlaywrightBrowser.page.locator("#first-name").evaluate("el => el.value").toString();
        String lastNameValue = PlaywrightBrowser.page.locator("#last-name").evaluate("el => el.value").toString();
        String emailValue = PlaywrightBrowser.page.locator("#email").evaluate("el => el.value").toString();
        String dobValue = PlaywrightBrowser.page.locator("#date-of-birth").evaluate("el => el.value").toString();

//        location inputs
        String addressLine1Value = PlaywrightBrowser.page.locator("#address-line-1").evaluate("el => el.value").toString();
        String addressLine2Value = PlaywrightBrowser.page.locator("#address-line-2").evaluate("el => el.value").toString();
        String suburbValue = PlaywrightBrowser.page.locator("#suburb").evaluate("el => el.value").toString();
        String postcodeValue = PlaywrightBrowser.page.locator("#postcode").evaluate("el => el.value").toString();
        String cityValue = PlaywrightBrowser.page.locator("#city").evaluate("el => el.value").toString();
        String countryValue = PlaywrightBrowser.page.locator("#country").evaluate("el => el.value").toString();

        String[] parts = dobValue.split("-");
        String rearrangedDob = parts[2] + "-" + parts[1] + "-" + parts[0];

        Assertions.assertEquals(DEFAULT_USER_NAME,firstNameValue + " " + lastNameValue);
        Assertions.assertEquals(DEFAULT_EMAIL, emailValue);
        Assertions.assertEquals(DEFAULT_DOB, rearrangedDob);

//        location checking
        Assertions.assertTrue(DEFAULT_LOCATION.contains(addressLine1Value));
        Assertions.assertTrue(DEFAULT_LOCATION.contains(addressLine2Value));
        Assertions.assertTrue(DEFAULT_LOCATION.contains(suburbValue));
        Assertions.assertTrue(DEFAULT_LOCATION.contains(postcodeValue));
        Assertions.assertTrue(DEFAULT_LOCATION.contains(cityValue));
        Assertions.assertTrue(DEFAULT_LOCATION.contains(countryValue));

    }

    @Given("I am on the edit profile form,")
    public void i_am_on_the_edit_profile_form() {
        PlaywrightBrowser.page.locator("a[href='editUser']").click();
    }

    @Given("I enter valid values for my first name, last name, and date of birth,")
    public void i_enter_valid_values_for_my_first_name_last_name_email_address_and_date_of_birth() {
        PlaywrightBrowser.page.locator("#first-name").clear();
        PlaywrightBrowser.page.locator("#last-name").clear();
        PlaywrightBrowser.page.locator("#date-of-birth").clear();

        PlaywrightBrowser.page.locator("#first-name").type("changedFirstName");
        PlaywrightBrowser.page.locator("#last-name").type("changedLastName");
        PlaywrightBrowser.page.locator("#date-of-birth").type("01-01-2000");

    }

    @Then("my new details are saved.")
    public void my_new_details_are_saved() {
        String nameValue = PlaywrightBrowser.page.locator(".user-label:has(.attribute-type:has-text('Name')) .name").textContent();
        String dobValue = PlaywrightBrowser.page.locator(".user-label:has(.attribute-type:has-text('Date of Birth')) .dob").textContent();
        DEFAULT_USER_NAME= "changedFirstName changedLastName";
        DEFAULT_DOB=dobValue;

        Assertions.assertEquals("changedFirstName changedLastName", nameValue);
        Assertions.assertEquals("01-01-2000", dobValue);

    }

    @Given("I enter invalid values \\(i.e. empty strings or non-alphabetical characters except a hypen or space) for my firstname,")
    public void i_enter_invalid_values_i_e_empty_strings_or_non_alphabetical_characters_except_a_hypen_or_space_for_my_firstname() {
        PlaywrightBrowser.page.locator("#first-name").clear();
    }

    @Then("an error message tells me these fields contain invalid values.")
    public void an_error_message_tells_me_these_fields_contain_invalid_values() {
        PlaywrightBrowser.page.locator(".error-message").isVisible();
    }

    @Given("I enter a malformed or empty email address,")
    public void i_enter_a_malformed_or_empty_email_address() {
        PlaywrightBrowser.page.locator("#email").type("$%^&*(KJHK(*&(*&(");
    }

    @Then("an error message tells me the email address is invalid")
    public void an_error_message_tells_me_the_email_address_is_invalid() {
        PlaywrightBrowser.page.locator(".error-message").isVisible();
    }

}
