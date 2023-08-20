package nz.ac.canterbury.seng302.tab.end2end;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URL;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class U8CreateTeamFeature {

    private static final String CREATE_TEAM_URL = "/createTeam";
    private static final String HOME_URL = "/home";

    @Before("@create_team")
    public void create_team_init() {
        DefaultFunctions.pwLogin();
    }

    @Given("I am on the home page")
    public void i_am_on_the_home_page() {
        PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl + HOME_URL);
    }

    @Given("I am on the Create Team page")
    public void i_am_on_the_create_team_page() {
        PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl + CREATE_TEAM_URL);
    }

    @When("I click on the Create Team button")
    public void i_click_on_the_create_team_button() {
        // The navbar collapses into a hamburger menu if the screen's too small.
        // So depending on the browser window's size, it may not be there
        var hamburger = PlaywrightBrowser.page.locator(".hamburger");
        if (hamburger.isVisible()) {
            hamburger.click();
        }
        PlaywrightBrowser.page.locator("li.navbar-create-button").click();
        PlaywrightBrowser.page.locator("text=Create Team").click();
    }

    @When("I enter a team name of {string}")
    public void i_enter_a_team_name_of(String name) {
        PlaywrightBrowser.page.locator("input#name").type(name);
    }

    @When("I enter a team sport of {string}")
    public void i_enter_a_team_sport_of(String sport) {
        PlaywrightBrowser.page.locator("input#sport").type(sport);
    }

    @When("I enter a team city of {string} in the country {string}")
    public void i_enter_a_team_city_of_in_the_country(String city, String country) {
        PlaywrightBrowser.page.locator("input#city").type(city);
        PlaywrightBrowser.page.locator("input#country").type(country);
    }

    @When("I enter a team address of {string}")
    public void i_enter_a_team_address_of(String addr1) {
        PlaywrightBrowser.page.locator("input#address-line-1").type(addr1);
    }


    @When("I submit the Create Team form")
    public void i_submit_the_create_team_form() {
        PlaywrightBrowser.page.locator("[data-cy=submit]").click();
    }

    @When("I click on the Cancel button")
    public void i_click_on_the_cancel_button() {
        PlaywrightBrowser.page.locator("[data-cy=cancel]").click();
    }

    @Then("I am brought to this team's page")
    public void i_am_brought_to_this_team_s_page() throws Exception {
        URL url = new URL(PlaywrightBrowser.page.url());
        assertEquals("/team-info", url.getPath());
    }

    @Then("the team name, sport, and location are {string}, {string}, and {string}")
    public void the_team_name_sport_and_location_are_and(String name, String sport, String location) {
        String actualName = PlaywrightBrowser.page.locator("[data-cy=name]").innerText();
        String actualSport = PlaywrightBrowser.page.locator("[data-cy=sport]").innerText();
        String actualLocation = PlaywrightBrowser.page.locator("[data-cy=location]").innerText();
        assertEquals(name, actualName);
        assertEquals(sport, actualSport);
        assertEquals(location, actualLocation);
    }

    @Then("the Create Team form is not submitted")
    @Then("I am brought to the Create Team page")
    public void i_am_brought_to_the_create_team_page() {
        String expectedUrl = "http://" + PlaywrightBrowser.baseUrl + CREATE_TEAM_URL;
        assertEquals(expectedUrl, PlaywrightBrowser.page.url());
    }

    @Then("I am brought to the home page")
    public void i_am_brought_to_the_home_page() {
        String expectedUrl = "http://" + PlaywrightBrowser.baseUrl + HOME_URL;
        assertEquals(expectedUrl, PlaywrightBrowser.page.url());
    }

    @Then("the page's title is {string}")
    public void the_page_s_title_is(String title) {
        String actualTitleText = PlaywrightBrowser.page.innerText("title");
        assertEquals(title, actualTitleText);
    }

    @Then("the page's header is {string}")
    public void the_page_s_header_is(String header) {
        String actualHeaderText = PlaywrightBrowser.page.innerText("h1[data-cy=header]");
        assertEquals(header, actualHeaderText);
    }

    @Then("the page's submit button is {string}")
    public void the_page_s_submit_button_is(String buttonText) {
        String actualButtonText = PlaywrightBrowser.page.innerText("button[type=submit]");
        assertEquals(buttonText, actualButtonText);
    }
}
