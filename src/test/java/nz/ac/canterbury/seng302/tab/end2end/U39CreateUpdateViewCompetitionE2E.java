package nz.ac.canterbury.seng302.tab.end2end;

import com.microsoft.playwright.options.LoadState;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

public class U39CreateUpdateViewCompetitionE2E {

    @Before("@create_view_update_competition_e2e")
    public void setup() {

        DefaultFunctions.pwLogin();
        DefaultFunctions.pwCreateTeamForCompetition();
        DefaultFunctions.pwCreateCompetition();

    }

    @Given("I am viewing details about a team and that team is enrolled in a competition,")
    public void i_am_viewing_details_about_a_team_and_that_team_is_enrolled_in_a_competition() {

        PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl + "/view-teams?page=1");
        PlaywrightBrowser.page.locator(".card-wrapper").locator("h5:has-text('CompetitionTeam')").first().click();
        PlaywrightBrowser.page.waitForLoadState(LoadState.NETWORKIDLE);
        PlaywrightBrowser.page.locator("div.tab#competitions-tab").click();

    }

    @When("I click a dedicated ui element to view that competition,")
    public void i_click_a_dedicated_ui_element_to_view_that_competition() {
        PlaywrightBrowser.page.locator("div.competitions li").first().click();
        PlaywrightBrowser.page.waitForLoadState(LoadState.NETWORKIDLE);
    }

    @Then("I am brought to a page with details about the competition, including name, grade level, sport and all teams involved in said competition.")
    public void i_am_brought_to_a_page_with_details_about_the_competition_including_name_grade_level_sport_and_all_teams_involved_in_said_competition() {
        Assertions.assertEquals("Men's Open Professional", PlaywrightBrowser.page.locator("#compGrade").textContent());
        Assertions.assertEquals("soccercomp", PlaywrightBrowser.page.locator("#compName").textContent());
        Assertions.assertEquals("soccer", PlaywrightBrowser.page.locator("#compSport").textContent());
        Assertions.assertEquals("CompetitionTeam", PlaywrightBrowser.page.locator("div.teamOrUserListDisplay li").first().textContent().trim());


    }

    @Given("I am on a page dedicated to displaying competitions and there exists past and current competitions for a sport,")
    public void i_am_on_a_page_dedicated_to_displaying_competitions_and_there_exists_past_and_current_competitions_for_a_sport() {

        PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl + "/view-all-competitions?page=1");

    }

    @When("I apply a filter for that sport and select an option to display all competitions,")
    public void i_apply_a_filter_for_that_sport_and_select_an_option_to_display_all_competitions() {
        PlaywrightBrowser.page.locator(".selectBox").nth(1).click();
        PlaywrightBrowser.page.locator("input[type=checkbox][value=soccer]").check();
        PlaywrightBrowser.page.locator(".selectBox").nth(0).click();

        PlaywrightBrowser.page.locator(".apply-filters-button").click();

    }

    @Then("I am shown all competitions, past and current for the selected sport.")
    public void i_am_shown_all_competitions_past_and_current_for_the_selected_sport() {
        Assertions.assertEquals("soccercomp", PlaywrightBrowser.page.locator("div.competition-name").first().textContent());

    }

    @When("I apply a filter for that sport and I select an option to display only current competitions,")
    public void i_apply_a_filter_for_that_sport_and_i_select_an_option_to_display_only_current_competitions() {

        PlaywrightBrowser.page.locator(".selectBox").nth(1).click();
        PlaywrightBrowser.page.locator("input[type=checkbox][value=soccer]").check();
        PlaywrightBrowser.page.locator(".selectBox").nth(0).click();
        PlaywrightBrowser.page.locator("input[type=checkbox][value=Current]").check();

        PlaywrightBrowser.page.locator(".apply-filters-button").click();
    }

    @Then("I am shown only current competitions for the selected sport.")
    public void i_am_shown_only_current_competitions_for_the_selected_sport() {
        Assertions.assertTrue(PlaywrightBrowser.page.locator("div.emptySearchResults").isVisible());
    }

    @When("I apply a filter for that sport and I select an option to display only past competitions,")
    public void i_apply_a_filter_for_that_sport_and_i_select_an_option_to_display_only_past_competitions() {
        PlaywrightBrowser.page.locator(".selectBox").nth(1).click();
        PlaywrightBrowser.page.locator("input[type=checkbox][value=soccer]").check();
        PlaywrightBrowser.page.locator(".selectBox").nth(0).click();
        PlaywrightBrowser.page.locator("input[type=checkbox][value=Past]").check();

        PlaywrightBrowser.page.locator(".apply-filters-button").click();
    }

    @Then("I am shown only past competitions for the selected sport.")
    public void i_am_shown_only_past_competitions_for_the_selected_sport() {
        Assertions.assertTrue(PlaywrightBrowser.page.locator("div.emptySearchResults").isVisible());

    }

}
