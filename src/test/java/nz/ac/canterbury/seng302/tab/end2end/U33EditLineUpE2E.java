package nz.ac.canterbury.seng302.tab.end2end;

import com.microsoft.playwright.options.LoadState;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

public class U33EditLineUpE2E {


    private static boolean isSetupExecuted = false;

    String subName;

    int counter = 0;
    @Before("@edit_line_up_e2e")
    public void setup() {
        PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl + "/populate_database");
        DefaultFunctions.pwLogin();
        if (!isSetupExecuted) {
            DefaultFunctions.pwCreateNewTeamWithFormationAndActivity();
            isSetupExecuted = true;
        }
    }


    @Given("I am the manager of a team")
    public void i_am_the_manager_of_a_team() {
        PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl + "/populate_database");

        PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl + "/view-teams?page=1");
        PlaywrightBrowser.page.locator(".card-wrapper").locator("h5:has-text('0')").first().click();

        PlaywrightBrowser.page.waitForLoadState(LoadState.NETWORKIDLE);

    }

    @Given("viewing the edit page for a team activity for that team")
    public void viewing_the_edit_page_for_a_team_activity_for_that_team() {
        //first click view team activities
        PlaywrightBrowser.page.locator("#activities").click();
        PlaywrightBrowser.page.waitForLoadState(LoadState.NETWORKIDLE);
        PlaywrightBrowser.page.locator("#editActivityBtn").first().click();
        PlaywrightBrowser.page.locator("#formation-dropdown").selectOption("2");

    }

    @When("I select a player")
    public void i_select_a_player() {
        PlaywrightBrowser.page.locator(".reserves-li").first().click();
    }

    @Then("I can set a position for that player to be in")
    public void i_can_set_a_position_for_that_player_to_be_in() {
        PlaywrightBrowser.page.locator("#player1").click();
    }

    @Given("the activity has type game or friendly and has a selected formation")
    public void the_activity_has_type_game_or_friendly_and_has_a_selected_formation() {
        PlaywrightBrowser.page.locator("#activityType").selectOption("Game");
    }

    @When("I add a player from the team to selected the formation")
    public void i_add_a_player_from_the_team_to_selected_the_formation() {
        PlaywrightBrowser.page.locator(".reserves-li").first().click();
        PlaywrightBrowser.page.locator("#player1").click();
    }

    @Then("that player is unable to be added to the formation again")
    public void that_player_is_unable_to_be_added_to_the_formation_again() {
        Assertions.assertEquals(1, PlaywrightBrowser.page.locator(".in-formation").first().count());
    }


    @Then("that players picture and name are displayed at the correct position")
    public void that_players_picture_and_name_are_displayed_at_the_correct_position() {

       Assertions.assertTrue(!PlaywrightBrowser.page.querySelector("#player1 h3").textContent().isEmpty());
    }


    @When("I attempt to save an empty formation")
    public void iAttemptToSaveAnEmptyFormation() {
        PlaywrightBrowser.page.locator("#formation-dropdown").selectOption("2");

        PlaywrightBrowser.page.click("button:has-text('Save')");
    }

    @Then("the formation is not saved and an error message is shown telling me the line-up is not complete")
    public void theFormationIsNotSavedAndAnErrorMessageIsShownTellingMeTheLineUpIsNotComplete() {
        Assertions.assertEquals("The line-up is not complete", PlaywrightBrowser.page.locator(".error-message").textContent());    }

    @And("all starting positions on the formation a filled with players")
    public void all_starting_positions_on_the_formation_a_filled_with_players() {

        PlaywrightBrowser.page.click(".reserves-li:nth-child(1)");
        PlaywrightBrowser.page.locator("#player1").click();
        PlaywrightBrowser.page.click(".reserves-li:nth-child(2)");
        PlaywrightBrowser.page.locator("#player2").click();

    }

    @When("I select another player")
    public void i_select_another_player() {
        PlaywrightBrowser.page.click(".reserves-li:nth-child(3)");
    }

    @Then("I can add the selected player to the list of substitutes")
    public void i_can_add_the_selected_player_to_the_list_of_substitutes() {

        PlaywrightBrowser.page.querySelector(".reserves-li:nth-child(3)").querySelector("button").click();

    }

    @And("add them to the list of substitutes")
    public void add_them_to_the_list_of_substitutes() {
        PlaywrightBrowser.page.querySelector(".reserves-li:nth-child(3)").querySelector("button").click();
        subName = PlaywrightBrowser.page.querySelector(".li-player-name").innerText();
    }

    @Then("that players name and profile picture are displayed")
    public void that_players_name_and_profile_picture_are_displayed() {
        Assertions.assertEquals(PlaywrightBrowser.page.querySelector(".subs-li").querySelector(".li-player-name").innerText(), subName);
    }
}
