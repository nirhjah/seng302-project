package nz.ac.canterbury.seng302.tab.end2end;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class ViewTeamAggregatedStatistics {

    @Before("@view_aggregated_team_statistics")
    public void view_team_stats_init() {

        DefaultFunctions.pwLogin();
        DefaultFunctions.pwCreateNewTeamWithFormationAnd5Activity();


    }

    @Given("I am on the team profile page")
    public void i_am_on_the_team_profile_page() {
        PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl + "/view-teams?page=1");
        PlaywrightBrowser.page.locator(".card-wrapper").locator("h5:has-text('team ag stats team')").first().click();

       // PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl + "/team-info?teamID=4");
        PlaywrightBrowser.page.waitForLoadState(LoadState.NETWORKIDLE);
    }

    @When("I click on a UI element to view the aggregated team statistics")
    public void i_click_on_a_ui_element_to_view_the_aggregated_team_statistics() {
        PlaywrightBrowser.page.locator("div.tab#stats-tab").click();

    }

    @Then("I will see the team statistics")
    public void i_will_see_the_team_statistics() {
        PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl + "/view-teams?page=1");
        PlaywrightBrowser.page.locator(".card-wrapper").locator("h5:has-text('team ag stats team')").first().click();

       // PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl + "/team-info?teamID=4");
        PlaywrightBrowser.page.waitForLoadState(LoadState.NETWORKIDLE);
        PlaywrightBrowser.page.locator(".statistics-tab");

    }

    @Given("I see the aggregated team statistics")
    public void i_see_the_aggregated_team_statistics() {
        PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl + "/view-teams?page=1");
        PlaywrightBrowser.page.locator(".card-wrapper").locator("h5:has-text('team ag stats team')").first().click();

        //  PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl + "/team-info?teamID=4");
        PlaywrightBrowser.page.waitForLoadState(LoadState.NETWORKIDLE);
        PlaywrightBrowser.page.locator("div.tab#stats-tab").click();

    }


    @Then("I will see a section that displays the trend of the matches outcomes i.e win, lost, drew of the last 5 matches")
    public void i_will_see_a_section_that_displays_the_trend_of_the_matches_outcomes_i_e_win_lost_drew_of_the_last_matches() {

        for (int i = 1; i < 6; i++) {
            PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl + "/view-activity?activityID=" + i);
            PlaywrightBrowser.page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add Outcome")).click();
            PlaywrightBrowser.page.selectOption("#activityOutcomes", "Win");
            PlaywrightBrowser.page.click(".registerLoginButton");
        }


        PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl + "/view-activity?activityID=2");

        PlaywrightBrowser.page.locator(".general-activity-right button").click();

        PlaywrightBrowser.page.selectOption("#activityOutcomes", "Win");

        PlaywrightBrowser.page.click(".registerLoginButton");

        PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl + "/view-teams?page=1");
        PlaywrightBrowser.page.locator(".card-wrapper").locator("h5:has-text('team ag stats team')").first().click();

        // PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl + "/team-info?teamID=4");
        PlaywrightBrowser.page.waitForLoadState(LoadState.NETWORKIDLE);
        PlaywrightBrowser.page.locator("div.tab#stats-tab").click();
        Assertions.assertEquals(5, PlaywrightBrowser.page.locator(".last5 li").count());

    }

    @Given("there are activities \\(game or friendly)  that have won, lost or drew overall,")
    public void there_are_activities_game_or_friendly_that_have_won_lost_or_drew_overall() {

        PlaywrightBrowser.page.waitForTimeout(120000); //waiting for activity to end

        PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl + "/view-activity?activityID=2");
        PlaywrightBrowser.page.waitForLoadState(LoadState.NETWORKIDLE);
        PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl + "/view-activity?activityID=2");

        PlaywrightBrowser.page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add Outcome")).click();
        PlaywrightBrowser.page.selectOption("#activityOutcomes", "Win");
        PlaywrightBrowser.page.click(".registerLoginButton");

    }

    @Then("I can see a total of how many games or friendlies the team has played, won, lost and drew overall")
    public void i_can_see_a_total_of_how_many_games_or_friendlies_the_team_has_played_won_lost_and_drew_overall() {
        PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl + "/view-teams?page=1");
        PlaywrightBrowser.page.locator(".card-wrapper").locator("h5:has-text('team ag stats team')").first().click();

      //  PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl + "/team-info?teamID=4");
        PlaywrightBrowser.page.waitForLoadState(LoadState.NETWORKIDLE);
        PlaywrightBrowser.page.locator("div.tab#stats-tab").click();

        Assertions.assertEquals("10", PlaywrightBrowser.page.locator("#cell-1 h4").innerText());
        Assertions.assertEquals("1", PlaywrightBrowser.page.locator("#cell-2 h4").innerText());
        Assertions.assertEquals("0", PlaywrightBrowser.page.locator("#cell-3 h4").innerText());
        Assertions.assertEquals("0", PlaywrightBrowser.page.locator("#cell-4 h4").innerText());



    }

    @Given("I have at least 5 activities")
    public void i_have_at_least_activities() {
        PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl + "/view-teams?page=1");
        PlaywrightBrowser.page.locator(".card-wrapper").locator("h5:has-text('team ag stats team')").first().click();
        PlaywrightBrowser.page.waitForLoadState(LoadState.NETWORKIDLE);

        PlaywrightBrowser.page.locator("button:has-text('Activities')").first();
        // PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl + "/viewTeamActivities?page=1&teamID=4");
        PlaywrightBrowser.page.waitForLoadState(LoadState.NETWORKIDLE);

    }

    @Given("they are of either type Game or Friendly")
    public void they_are_of_either_type_game_or_friendly() {
        PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl + "/view-teams?page=1");
        PlaywrightBrowser.page.locator(".card-wrapper").locator("h5:has-text('team ag stats team')").first().click();
        PlaywrightBrowser.page.waitForLoadState(LoadState.NETWORKIDLE);

        PlaywrightBrowser.page.locator("button:has-text('Activities')").first().click();

        // PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl + "/viewTeamActivities?page=1&teamID=4");
        PlaywrightBrowser.page.waitForLoadState(LoadState.NETWORKIDLE);

        Assertions.assertNotNull(PlaywrightBrowser.page.waitForSelector("ul.activities-table"));
    }

    @When("I am viewing team aggregated statistics")
    public void i_am_viewing_team_aggregated_statistics() {
        PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl + "/view-teams?page=1");
        PlaywrightBrowser.page.locator(".card-wrapper").locator("h5:has-text('team ag stats team')").first().click();

        //  PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl + "/team-info?teamID=4");
        PlaywrightBrowser.page.waitForLoadState(LoadState.NETWORKIDLE);
        PlaywrightBrowser.page.locator(".statistics-tab");
        PlaywrightBrowser.page.waitForTimeout(110000); //waiting for activity to end
    }

    @Then("I can see a trend of the last 5 activities in terms of win, loss and draw")
    public void i_can_see_a_trend_of_the_last_activities_in_terms_of_win_loss_and_draw() {

        PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl + "/view-activity?activityID=2");
        PlaywrightBrowser.page.waitForLoadState(LoadState.NETWORKIDLE);
        PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl + "/view-activity?activityID=2");

        PlaywrightBrowser.page.locator("div.tab#score-tab").click();

        PlaywrightBrowser.page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add Overall Score")).click();
        PlaywrightBrowser.page.fill("input#overall-score-team", String.valueOf(4));
        PlaywrightBrowser.page.fill("input#overall-score-opponent", String.valueOf(5));
        PlaywrightBrowser.page.locator("div.overall-score-modal-content button").first().click();

        PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl + "/view-teams?page=1");
        PlaywrightBrowser.page.locator(".card-wrapper").locator("h5:has-text('team ag stats team')").first().click();

      //  PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl + "/team-info?teamID=4");
        PlaywrightBrowser.page.waitForLoadState(LoadState.NETWORKIDLE);
        PlaywrightBrowser.page.locator("div.tab#stats-tab").click();

        String winImg = "/image/icons/win.svg";
        Assertions.assertTrue( PlaywrightBrowser.page.locator("img[src='" + winImg + "']").first().isVisible());
    }

    @Then("I can see the date and score of the activity")
    public void i_can_see_the_date_and_score_of_the_activity() {

        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
        String formattedDate = currentDate.format(dateFormatter);
        String dateText = formattedDate + " - " + formattedDate;
        String scoreText = "Score: 4 : 5";


        Assertions.assertTrue(PlaywrightBrowser.page.locator("h4:text('" + dateText + "')").isVisible());
        Assertions.assertTrue(PlaywrightBrowser.page.locator("h4:text('" + scoreText + "')").isVisible());
    }

    @Then("I can click on each activity to be taken to that activitys page")
    public void i_can_click_on_each_activity_to_be_taken_to_that_activitys_page() {
        PlaywrightBrowser.page.locator("ul.last5Ul li").first().click();
        PlaywrightBrowser.page.waitForLoadState(LoadState.NETWORKIDLE);

        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
        String formattedDate = currentDate.format(dateFormatter);
        String activityTitleText = "Game: " + formattedDate;

        Assertions.assertTrue(PlaywrightBrowser.page.locator("h2:text('" + activityTitleText + "')").first().isVisible());

    }

    @Given("There are at least 5 or more members of the team")
    public void there_are_at_least_or_more_members_of_the_team() {
        PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl + "/populate_database");

    }

    @Given("they all have scored in the activity")
    public void they_all_have_scored_in_the_activity() {

        PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl + "/view-activity?activityID=2");
        PlaywrightBrowser.page.locator("div.tab#score-tab").click();


        for (int i = 0; i < 5; i++) {


            PlaywrightBrowser.page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add Goal")).click();
            PlaywrightBrowser.page.fill("input#time-of-goal", String.valueOf(1));
            PlaywrightBrowser.page.fill("input#goal-value", String.valueOf(i+1));

            PlaywrightBrowser.page.locator("#createGoal").click();



        }
    }

    @Then("I can see the top 5 scorers with how many goals they scored")
    public void i_can_see_the_top_scorers_with_how_many_goals_they_scored() {
        PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl + "/view-teams?page=1");
        PlaywrightBrowser.page.locator(".card-wrapper").locator("h5:has-text('team ag stats team')").first().click();

        // PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl + "/team-info?teamID=4");
        PlaywrightBrowser.page.waitForLoadState(LoadState.NETWORKIDLE);

        PlaywrightBrowser.page.locator("div.tab#stats-tab").click();

        Assertions.assertTrue(PlaywrightBrowser.page.locator(".top5Players li").count() >= 5);
    }

    @When("I am viewing the team aggregated statistics")
    public void i_am_viewing_the_team_aggregated_statistics() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("I can see the top 5 players sorted by overall play time")
    public void i_can_see_the_top_players_sorted_by_overall_play_time() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("I can see their average play time too")
    public void i_can_see_their_average_play_time_too() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @When("I click on the View team activities button")
    public void i_click_on_the_view_team_activities_button() {
        PlaywrightBrowser.page.getByText("View Team Activities").click();
        PlaywrightBrowser.page.waitForLoadState(LoadState.NETWORKIDLE);

    }

    @Then("I will see the list of activities this team has taken part in")
    public void i_will_see_the_list_of_activities_this_team_has_taken_part_in() {
        assertThat(PlaywrightBrowser.page
                .getByRole(AriaRole.HEADING,
                        new Page.GetByRoleOptions().setName("Team Activities")))
                .isVisible();
    }





}
