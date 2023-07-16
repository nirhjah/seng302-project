package nz.ac.canterbury.seng302.tab.end2end;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.tab.entity.Formation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

public class U32CreateFormationFeature {

    @BeforeAll
    public static void setup() {
        PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl + "/login");
        PlaywrightBrowser.page.locator("input#username").type("admin@gmail.com");
        PlaywrightBrowser.page.locator("input#password").type("1");
        PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl + "/createTeam");
        PlaywrightBrowser.page.locator("input#name").type("team");
        PlaywrightBrowser.page.locator("input#sport").type("football");
        PlaywrightBrowser.page.locator("input#city").type("Christchurch");
        PlaywrightBrowser.page.locator("input#country").type("New Zealand");
        PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl + "/login?teamID=1");
        PlaywrightBrowser.page.locator("div#formations-tab").click();
        PlaywrightBrowser.page.locator("li#create-formation-li").click();
        PlaywrightBrowser.page.locator("button#create-formation-button").click();
    }

    @Given("I am on my team’s profile")
    public void iAmOnMyTeamSProfile() throws Exception {
        PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl + "/login?teamID=1");
    }

    @When("I click on a UI element to see all the team’s formations")
    public void iClickOnAUIElementToSeeAllTheTeamSFormations() {
        PlaywrightBrowser.page.locator("#formations-tab").click();
    }

    @Then("I see a list of all formations for that team")
    public void iSeeAListOfAllFormationsForThatTeam() throws Exception {
        Assertions.assertEquals(1, PlaywrightBrowser.page.locator(".formation-li").count());
        Assertions.assertEquals("1-4-4-2", PlaywrightBrowser.page.locator(".formation-li").textContent());
    }

    @Given("I am on my team’s formation page")
    public void iAmOnMyTeamSFormationPage() throws Exception {
        iAmOnMyTeamSProfile();
        iClickOnAUIElementToSeeAllTheTeamSFormations();
    }

    @When("I click on a UI element to create a new line-up")
    public void iClickOnAUIElementToCreateANewLineUp() {

    }

    @Then("I see a graphical representation of a sport pitch corresponding to the sport of that team")
    public void iSeeAGraphicalRepresentationOfASportPitchCorrespondingToTheSportOfThatTeam() {
    }

    @Given("I set up the number of players per sector")
    public void iSetUpTheNumberOfPlayersPerSector() {
    }

    @When("the number of players per sector is invalid \\(I.e. does not respect the pattern of a number followed by a dash except for the last number), or is empty,")
    public void theNumberOfPlayersPerSectorIsInvalidIEDoesNotRespectThePatternOfANumberFollowedByADashExceptForTheLastNumberOrIsEmpty() {
    }

    @Then("an error message tells me that the formation is invalid.")
    public void anErrorMessageTellsMeThatTheFormationIsInvalid() {
    }

    @Given("I am on the formation creation page")
    public void iAmOnTheFormationCreationPage() {
    }

    @When("I specify a number of players per sector on the pitch in the form of dash separated numbers starting from the back line up to the front line on the pitch")
    public void iSpecifyANumberOfPlayersPerSectorOnThePitchInTheFormOfDashSeparatedNumbersStartingFromTheBackLineUpToTheFrontLineOnThePitch() {
    }

    @Then("a formation is generated that matches the formation string")
    public void aFormationIsGeneratedThatMatchesTheFormationString() {
    }

    @Given("I have correctly set up a formation with the number of players per sector")
    public void iHaveCorrectlySetUpAFormationWithTheNumberOfPlayersPerSector() {
    }

    @When("I click on the create formation button")
    public void iClickOnTheCreateFormationButton() {
    }

    @Then("the formation is persisted in the system")
    public void theFormationIsPersistedInTheSystem() {
    }
}
