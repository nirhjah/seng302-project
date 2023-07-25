package nz.ac.canterbury.seng302.tab.end2end;

import com.microsoft.playwright.options.LoadState;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

public class U33EditLineUpE2E {


    private static boolean isSetupExecuted = false;

    @Before("@edit_line_up_e2e")
    public void setup() {
        DefaultFunctions.pwLogin();
        if (!isSetupExecuted) {
            DefaultFunctions.pwCreateTeam();
            DefaultFunctions.pwCreateFormation();
            DefaultFunctions.pwCreateActivity();
            isSetupExecuted = true;
        }
    }


    @Given("I am the manager of a team")
    public void i_am_the_manager_of_a_team() {
        // Go to team profile, check edit team role button is available that means youre manager
        PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl + "/profile?teamID=1");
        PlaywrightBrowser.page.waitForLoadState(LoadState.NETWORKIDLE);


    }

    @Given("viewing the edit page for a team activity for that team")
    public void viewing_the_edit_page_for_a_team_activity_for_that_team() {
        //first click view team activities
        PlaywrightBrowser.page.locator("#viewTeamActivitiesBtn").click();
        PlaywrightBrowser.page.waitForLoadState(LoadState.NETWORKIDLE);
        PlaywrightBrowser.page.locator("#editActivityBtn").first().click();
    }

    @When("I select a position")
    public void i_select_a_position() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("I can set a player from the team to that position")
    public void i_can_set_a_player_from_the_team_to_that_position() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("that players picture and name are displayed at the correct position")
    public void that_players_picture_and_name_are_displayed_at_the_correct_position() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Given("the activity has type game or friendly and has a selected formation")
    public void the_activity_has_type_game_or_friendly_and_has_a_selected_formation() {
        //check that the activityType is "Game"
        PlaywrightBrowser.page.locator("#activityType").selectOption("Game");
        // PlaywrightBrowser.page.locator("#team").selectOption("team");
        //select formation from formation-dropdown 1-4-4-2
        PlaywrightBrowser.page.locator("#formation-dropdown").selectOption("1-4-4-2");

    }

    @When("I add a player from the team to selected the formation")
    public void i_add_a_player_from_the_team_to_selected_the_formation() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("that player is unable to be added to the formation again")
    public void that_player_is_unable_to_be_added_to_the_formation_again() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("that player�s picture and name are displayed at the correct position")
    public void that_player_s_picture_and_name_are_displayed_at_the_correct_position() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Given("all starting positions on the formation a filled with players")
    public void all_starting_positions_on_the_formation_a_filled_with_players() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @When("I select another player")
    public void i_select_another_player() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("I can add the selected player to the list of substitutes")
    public void i_can_add_the_selected_player_to_the_list_of_substitutes() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @When("add them to the list of substitutes")
    public void add_them_to_the_list_of_substitutes() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("that players name and profile picture are displayed")
    public void that_players_name_and_profile_picture_are_displayed() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @When("I attempt to save an empty formation")
    public void i_attempt_to_save_an_empty_formation() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("the formation is not saved and an error message is shown telling me the line-up is not complete")
    public void the_formation_is_not_saved_and_an_error_message_is_shown_telling_me_the_line_up_is_not_complete() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @When("I attempt to cancel editing the activity")
    public void i_attempt_to_cancel_editing_the_activity() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("the activity will return to the state it was prior to editing")
    public void the_activity_will_return_to_the_state_it_was_prior_to_editing() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

}
