package nz.ac.canterbury.seng302.tab.end2end;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class U10EditTeamDetailsFeature {

    @Given("I am on my team profile page,")
    public void i_am_on_my_team_profile_page() {
    }

    @When("I hit the edit button,")
    public void i_hit_the_edit_button() {
    }

    @Then("I see the edit team details form with all its details prepopulated.")
    public void i_see_the_edit_team_details_form_with_all_its_details_prepopulated() {
    }

    @Given("I am on the edit team profile form,")
    public void i_am_on_the_edit_team_profile_form() {
    }

    @Given("I enter valid values for the name, location and sport,")
    public void i_enter_valid_values_for_the_name_location_and_sport() {
    }

    @When("I hit the save button, then the new details are saved.")
    public void i_hit_the_save_button_then_the_new_details_are_saved() {
    }

    @Given("I enter an invalid value for the team�s name \\(e.g., non-alphanumeric other than dots or curly brackets, name made of only acceptable non-alphanumeric),")
    public void i_enter_an_invalid_value_for_the_team_s_name_e_g_non_alphanumeric_other_than_dots_or_curly_brackets_name_made_of_only_acceptable_non_alphanumeric() {
    }

    @When("I hit the save button,")
    public void i_hit_the_save_button() {
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

    @Given("I am on the edit profile form,")
    public void i_am_on_the_edit_profile_form() {

    }

    @When("I hit the cancel button, I come back to the team�s profile page,")
    public void i_hit_the_cancel_button_i_come_back_to_the_team_s_profile_page() {

    }

    @When("no changes have been made to its profile")
    public void no_changes_have_been_made_to_its_profile() {

    }
}
