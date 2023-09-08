package nz.ac.canterbury.seng302.tab.end2end;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

public class U33EditLineUpE2E {


    private static boolean isSetupExecuted = false;

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
        PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl + "/team-info?teamID=2");
        PlaywrightBrowser.page.waitForLoadState(LoadState.NETWORKIDLE);

    }

    @Given("viewing the edit page for a team activity for that team")
    public void viewing_the_edit_page_for_a_team_activity_for_that_team() {
        //first click view team activities
        PlaywrightBrowser.page.locator("#activities").click();
        PlaywrightBrowser.page.waitForLoadState(LoadState.NETWORKIDLE);
        PlaywrightBrowser.page.locator("#editActivityBtn").first().click();
        PlaywrightBrowser.page.locator("#formation-dropdown").selectOption("1-4-4-2");

    }

    @When("I select a player")
    public void i_select_a_player() {
        PlaywrightBrowser.page.locator(".reserves-li").first().click();
    }

    @Then("I can set a position for that player to be in")
    public void i_can_set_a_position_for_that_player_to_be_in() {
        PlaywrightBrowser.page.locator("#player6").click();
    }

    @Given("the activity has type game or friendly and has a selected formation")
    public void the_activity_has_type_game_or_friendly_and_has_a_selected_formation() {
        PlaywrightBrowser.page.locator("#activityType").selectOption("Game");
    }

    @When("I add a player from the team to selected the formation")
    public void i_add_a_player_from_the_team_to_selected_the_formation() {
        PlaywrightBrowser.page.locator(".reserves-li").first().click();
        PlaywrightBrowser.page.locator("#player6").click();
    }

    @Then("that player is unable to be added to the formation again")
    public void that_player_is_unable_to_be_added_to_the_formation_again() {
        Assertions.assertEquals(1, PlaywrightBrowser.page.locator(".in-formation").first().count());
    }


    @Then("that players picture and name are displayed at the correct position")
    public void that_players_picture_and_name_are_displayed_at_the_correct_position() {
       Assertions.assertTrue(PlaywrightBrowser.page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Admin")).isVisible());
    }






}
